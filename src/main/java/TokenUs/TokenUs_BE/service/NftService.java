package TokenUs.TokenUs_BE.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TokenUs.TokenUs_BE.apiPayload.code.status.ErrorStatus;
import TokenUs.TokenUs_BE.apiPayload.exception.GeneralException;
import TokenUs.TokenUs_BE.converter.NftConverter;
import TokenUs.TokenUs_BE.domain.Nft;
import TokenUs.TokenUs_BE.domain.Transaction;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.domain.enums.TransactionType;
import TokenUs.TokenUs_BE.domain.mapping.VideoInterest;
import TokenUs.TokenUs_BE.dto.NftRequestDTO;
import TokenUs.TokenUs_BE.dto.NftResponseDTO;
import TokenUs.TokenUs_BE.repository.*;
import TokenUs.TokenUs_BE.web3.contract.VideoNFT;
import TokenUs.TokenUs_BE.web3.contract.VideoNFTMarketplace;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

@Service
public class NftService {

    private final Web3j web3j;
    private final Credentials credentials;

    private final VideoNFT videoNftContract;
    private final VideoNFTMarketplace marketplaceContract;

    private final NftConverter nftConverter;
    private final NftRepository nftRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final VideoInterestRepository videoInterestRepository;
    private final VideoRepository videoRepository;

    private static final String TRANSFER_EVENT_HASH =
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

    public NftService(
            Web3j web3j,
            @Value("${web3.private-key}") String privateKey,
            @Value("${web3.videonft-contract-address}") String videoNftcontractAddress,
            @Value("${web3.marketplace-contract-address}") String marketplaceContractAddress,
            @Value(("${CHAIN_ID}")) long chainId,
            NftConverter nftConverter,
            NftRepository nftRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            VideoInterestRepository videoInterestRepository,
            VideoRepository videoRepository)
            throws Exception {
        this.web3j = web3j;
        this.credentials = Credentials.create(privateKey);
        this.nftConverter = nftConverter;
        this.nftRepository = nftRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.videoInterestRepository = videoInterestRepository;
        this.videoRepository = videoRepository;

        RawTransactionManager txManager = new RawTransactionManager(web3j, credentials, chainId);

        this.videoNftContract =
                VideoNFT.load(
                        videoNftcontractAddress,
                        web3j,
                        txManager,
                        new StaticGasProvider(
                                BigInteger.valueOf(30_000_000_000L),
                                BigInteger.valueOf(6_500_000)));

        this.marketplaceContract =
                VideoNFTMarketplace.load(
                        marketplaceContractAddress,
                        web3j,
                        txManager,
                        new StaticGasProvider(
                                BigInteger.valueOf(30_000_000_000L),
                                BigInteger.valueOf(6_500_000)));
    }

    public NftResponseDTO.NFTMintResultDTO mintVideoNFT(
            NftRequestDTO.NFTMintRequestDTO request, User creator) throws Exception {
        // 1. 스마트 컨트랙트 호출
        TransactionReceipt receipt =
                videoNftContract
                        .mintVideoNFT(
                                request.getVideoId(),
                                request.getNftName(),
                                request.getNftSymbol(),
                                request.getTotalSupply(),
                                request.getPrice(),
                                request.getCreatorAddress())
                        .send();

        String txHash = receipt.getTransactionHash();
        System.out.println("✅ Mint txHash: " + txHash);

        // 2. tokenId 리스트 추출 (Transfer 이벤트에서)
        List<BigInteger> tokenIdList = extractTokenIdsFromLogs(receipt);

        // 3. NFT 도메인 객체 생성 & 저장
        List<Nft> savedNfts = new ArrayList<>();

        for (BigInteger tokenId : tokenIdList) {
            Nft nft =
                    nftConverter.toNft(
                            request,
                            tokenId,
                            getUserIdFromAddress(request.getCreatorAddress()),
                            request.getNftName(),
                            request.getNftSymbol());

            nft = nftRepository.save(nft); // 저장된 엔티티 다시 할당

            savedNfts.add(nft);

            // ✅ 여기에 넣어야 nft 스코프 안에 있음
            Transaction transaction =
                    Transaction.builder()
                            .txHash(txHash)
                            .type(TransactionType.MINT)
                            .seller(creator)
                            .buyer(null)
                            .nft(nft)
                            .build();

            transactionRepository.save(transaction);
        }

        // 4. 응답 DTO 구성
        List<NftResponseDTO.NFTInfoDTO> nftInfos = nftConverter.toDTOList(savedNfts);

        return NftResponseDTO.NFTMintResultDTO.builder()
                .transactionHash(txHash)
                .mintedNFTs(nftInfos)
                .build();
    }

    public NftResponseDTO.NFTMintResultDTO mintResultGet(NftRequestDTO.NFTMintRequestGetDTO request)
            throws Exception {

        List<BigInteger> tokenIdList = request.getTokenIdList();

        List<Nft> savedNfts = new ArrayList<>();

        for (BigInteger tokenId : tokenIdList) {
            Nft nft =
                    nftConverter.toNft_V2(
                            request,
                            tokenId,
                            request.getCreatorId(),
                            request.getNftName(),
                            request.getNftSymbol());

            nft = nftRepository.save(nft); // 저장된 엔티티 다시 할당

            savedNfts.add(nft);

            Transaction transaction =
                    Transaction.builder()
                            .txHash(request.getTxHash())
                            .type(TransactionType.MINT)
                            .seller(userRepository.getReferenceById(request.getCreatorId()))
                            .buyer(null)
                            .nft(nft)
                            .build();

            transactionRepository.save(transaction);
        }

        // 4. 응답 DTO 구성
        List<NftResponseDTO.NFTInfoDTO> nftInfos = nftConverter.toDTOList(savedNfts);

        return NftResponseDTO.NFTMintResultDTO.builder()
                .transactionHash(request.getTxHash())
                .mintedNFTs(nftInfos)
                .build();
    }

    private List<BigInteger> extractTokenIdsFromLogs(TransactionReceipt receipt) {
        List<BigInteger> tokenIds = new ArrayList<>();

        for (Log log : receipt.getLogs()) {
            if (log.getTopics().size() == 4 && log.getTopics().get(0).equals(TRANSFER_EVENT_HASH)) {
                String tokenIdHex = log.getTopics().get(3);
                if (tokenIdHex != null && tokenIdHex.length() >= 2) {
                    BigInteger tokenId = new BigInteger(tokenIdHex.substring(2), 16);
                    tokenIds.add(tokenId);
                }
            }
        }

        return tokenIds;
    }

    public Long getUserIdFromAddress(String walletAddress) {
        return userRepository
                .findByWalletAddress(walletAddress)
                .orElseThrow(() -> new IllegalArgumentException("해당 지갑 주소의 유저를 찾을 수 없습니다."))
                .getId();
    }

    public String safeTransferNFT(String from, String to, BigInteger tokenId) throws Exception {
        return videoNftContract.safeTransferFrom(from, to, tokenId).send().getTransactionHash();
    }

    // 판매 등록 메서드
    public NftResponseDTO.NFTListResultDTO listNft(
            NftRequestDTO.listNftRequestDTO request, Long loginUserId) throws Exception {

        BigInteger tokenId = request.getTokenId();
        BigInteger price = request.getPrice();

        // 1. 로그인한 사용자의 wallet address 확인
        String walletAddress =
                userRepository
                        .findById(loginUserId)
                        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."))
                        .getWalletAddress();

        System.out.println("🚨DB상 소유자" + walletAddress);

        // 2. 스마트 컨트랙트 상 NFT 소유자 확인
        String onChainOwner = videoNftContract.ownerOf(request.getTokenId()).send();
        System.out.println("🚨Chain상 소유자" + onChainOwner);

        String senderAddress = credentials.getAddress();
        System.out.println("📍현재 Web3j sender: " + senderAddress);

        if (!walletAddress.equalsIgnoreCase(onChainOwner)) {
            throw new IllegalAccessException("NFT의 실제 소유자가 아닙니다.");
        }

        // 3. 마켓플레이스 컨트랙트에 list 요청
        TransactionReceipt receipt =
                marketplaceContract.listNFT(request.getTokenId(), request.getPrice()).send();
        System.out.println("RECEIPT" + receipt);

        String txHash = receipt.getTransactionHash();

        // DB 업데이트
        Nft nft =
                nftRepository
                        .findByTokenId(tokenId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("해당 tokenId의 NFT가 존재하지 않습니다."));

        nft.setIsListed(true);
        nft.setCurrentPrice(price);
        nftRepository.save(nft);

        // 🧾 트랜잭션 기록
        Transaction transaction =
                Transaction.builder()
                        .txHash(txHash)
                        .type(TransactionType.LIST)
                        .seller(nft.getOwner())
                        .buyer(null)
                        .nft(nft)
                        .build();
        transactionRepository.save(transaction);

        return NftResponseDTO.NFTListResultDTO.builder()
                .id(nft.getId())
                .transactionHash(receipt.getTransactionHash())
                .nftName(nft.getNftName())
                .nftSymbol(nft.getNftSymbol())
                .tokenId(request.getTokenId())
                .currentPrice(nft.getCurrentPrice())
                .mintPrice(nft.getMintPrice())
                .sellerAddress(nft.getOwner().getWalletAddress())
                .isListed(true)
                .build();
    }

    public NftResponseDTO.NFTListResultDTO listNftResultGet(
            NftRequestDTO.listNftResultGetDTO request) throws Exception {

        Nft nft =
                nftRepository
                        .findByTokenId(request.getTokenId())
                        .orElseThrow(
                                () -> new IllegalArgumentException("해당 tokenId의 NFT가 존재하지 않습니다."));

        nft.setIsListed(true);
        nft.setCurrentPrice(request.getPrice());
        nftRepository.save(nft);

        // 🧾 트랜잭션 기록
        Transaction transaction =
                Transaction.builder()
                        .txHash(request.getTxHash())
                        .type(TransactionType.LIST)
                        .seller(nft.getOwner())
                        .buyer(null)
                        .nft(nft)
                        .build();
        transactionRepository.save(transaction);

        return NftResponseDTO.NFTListResultDTO.builder()
                .id(nft.getId())
                .transactionHash(request.getTxHash())
                .nftName(nft.getNftName())
                .nftSymbol(nft.getNftSymbol())
                .tokenId(request.getTokenId())
                .currentPrice(nft.getCurrentPrice())
                .mintPrice(nft.getMintPrice())
                .sellerAddress(nft.getOwner().getWalletAddress())
                .isListed(true)
                .build();
    }

    public NftResponseDTO.NFTListResultDTO delistNftResultGet(
            NftRequestDTO.listNftResultGetDTO request) throws Exception {

        Nft nft =
                nftRepository
                        .findByTokenId(request.getTokenId())
                        .orElseThrow(
                                () -> new IllegalArgumentException("해당 tokenId의 NFT가 존재하지 않습니다."));

        nft.setIsListed(false);
        nft.setCurrentPrice(request.getPrice());
        nftRepository.save(nft);

        // 🧾 트랜잭션 기록
        Transaction transaction =
                Transaction.builder()
                        .txHash(request.getTxHash())
                        .type(TransactionType.DELIST)
                        .seller(nft.getOwner())
                        .buyer(null)
                        .nft(nft)
                        .build();
        transactionRepository.save(transaction);

        return NftResponseDTO.NFTListResultDTO.builder()
                .id(nft.getId())
                .transactionHash(request.getTxHash())
                .nftName(nft.getNftName())
                .nftSymbol(nft.getNftSymbol())
                .tokenId(request.getTokenId())
                .currentPrice(nft.getCurrentPrice())
                .mintPrice(nft.getMintPrice())
                .sellerAddress(nft.getOwner().getWalletAddress())
                .isListed(false)
                .build();
    }

    public List<NftResponseDTO.listedNFTInfoDTO> getListedNfts(Long loginUserId) throws Exception {
        System.out.println("[SERVICE] getListedNfts() 호출됨");

        // 1. 컨트랙트에서 판매중인 NFT 목록 호출
        System.out.println("[SERVICE] getListedNFTs().send() 호출 직전");
        Tuple3<List<BigInteger>, List<String>, List<BigInteger>> result =
                marketplaceContract.getListedNFTs().send();
        System.out.println("[SERVICE] getListedNFTs().send() 성공");

        System.out.println("result " + result);

        List<BigInteger> tokenIds = result.component1();
        List<String> sellerAddresses = result.component2();
        List<BigInteger> prices = result.component3();

        System.out.println("[SERVICE] 가져온 tokenIds 수: " + tokenIds.size());
        System.out.println("[SERVICE] 가져온 sellerAddresses 수: " + sellerAddresses.size());
        System.out.println("[SERVICE] 가져온 prices 수: " + prices.size());

        List<NftResponseDTO.listedNFTInfoDTO> listedNfts = new ArrayList<>();

        // 로그인한 사용자 정보 가져오기
        Optional<User> loginUser =
                loginUserId != null ? userRepository.findById(loginUserId) : Optional.empty();

        BigInteger floorPrice = null; // floorPrice 초기화

        for (int i = 0; i < tokenIds.size(); i++) {
            BigInteger tokenId = tokenIds.get(i);

            System.out.println("[SERVICE] 처리 중인 tokenId: " + tokenId);
            ;
            Optional<Nft> optionalNft = nftRepository.findByTokenId(tokenId);

            if (optionalNft.isPresent()) {
                Nft nft = optionalNft.get();
                System.out.println(
                        "[SERVICE] DB에서 찾은 NFT ID: "
                                + nft.getId()
                                + ", isListed: "
                                + nft.getIsListed());

                // listed 값이 true인 경우 floorPrice 계산
                if (nft.getIsListed()) {
                    if (floorPrice == null || nft.getCurrentPrice().compareTo(floorPrice) < 0) {
                        floorPrice = nft.getCurrentPrice();
                        System.out.println("[SERVICE] 현재 floorPrice 갱신: " + floorPrice);
                    }
                }

                // DTO 변환
                NftResponseDTO.listedNFTInfoDTO dto =
                        nftConverter.toListedNFTInfoDTO(nft, sellerAddresses.get(i), floorPrice);
                listedNfts.add(dto);
            }
        }
        System.out.println("[SERVICE] 최종 반환할 listedNfts 크기: " + listedNfts.size());
        return listedNfts;
    }

    public NftResponseDTO.NFTListResultDTO delistNFT(
            NftRequestDTO.NftDelistRequestDTO request, Long loginUserId) throws Exception {
        BigInteger tokenId = request.getTokenId();

        // 🔐 소유자 인증 (체인 or DB 기준)
        Nft nft =
                nftRepository
                        .findByTokenId(tokenId)
                        .orElseThrow(() -> new IllegalArgumentException("NFT를 찾을 수 없습니다."));

        String walletAddress =
                userRepository
                        .findById(loginUserId)
                        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."))
                        .getWalletAddress();

        String onChainOwner = videoNftContract.ownerOf(tokenId).send();
        if (!walletAddress.equalsIgnoreCase(onChainOwner)) {
            throw new IllegalAccessException("NFT의 실제 소유자가 아닙니다.");
        }

        // 📦 스마트 컨트랙트 호출
        TransactionReceipt receipt = marketplaceContract.delistNFT(tokenId).send();
        String txHash = receipt.getTransactionHash();

        // 🗂 DB 상태 변경
        nft.setIsListed(false);
        nft.setCurrentPrice(null);
        nftRepository.save(nft);

        // 🧾 트랜잭션 기록
        Transaction transaction =
                Transaction.builder()
                        .txHash(txHash)
                        .type(TransactionType.DELIST)
                        .seller(nft.getOwner())
                        .buyer(null)
                        .nft(nft)
                        .build();
        transactionRepository.save(transaction);

        return NftResponseDTO.NFTListResultDTO.builder()
                .id(nft.getId())
                .transactionHash(receipt.getTransactionHash())
                .tokenId(request.getTokenId())
                .nftName(nft.getNftName())
                .nftSymbol(nft.getNftSymbol())
                .currentPrice(nft.getCurrentPrice())
                .mintPrice(nft.getMintPrice())
                .sellerAddress(nft.getOwner().getWalletAddress())
                .isListed(false)
                .build();
    }

    public NftResponseDTO.NFTPurchaseResultDTO purchaseNFT(
            NftRequestDTO.NFTPurchaseRequestDTO request, Long loginUserId) throws Exception {

        BigInteger tokenId = request.getTokenId();

        // 💰 가격 확인
        BigInteger price =
                nftRepository
                        .findByTokenId(tokenId)
                        .orElseThrow(() -> new IllegalArgumentException("NFT를 찾을 수 없습니다."))
                        .getCurrentPrice();

        User originalOwner =
                nftRepository
                        .findByTokenId(tokenId)
                        .orElseThrow(() -> new IllegalArgumentException("NFT를 찾을 수 없습니다."))
                        .getOwner();

        // 👛 구매자 지갑 주소
        String buyerAddress =
                userRepository
                        .findById(loginUserId)
                        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."))
                        .getWalletAddress();

        // 🧾 온체인 가격 확인
        Tuple4<BigInteger, String, BigInteger, Boolean> listing =
                marketplaceContract.listings(tokenId).send();
        BigInteger onChainPrice = listing.getValue3();

        System.out.println("📦 체인 상 가격 (wei): " + onChainPrice);
        System.out.println("💰 컨트랙트로 전달하는 가격(wei): " + price);
        System.out.println("🧾 tx.sender: " + credentials.getAddress());
        System.out.println("🧾 chain approved: " + videoNftContract.getApproved(tokenId).send());
        System.out.println("🧾 ownerOf: " + videoNftContract.ownerOf(tokenId).send());

        // ✅ 1. NFT 구매
        TransactionReceipt purchaseReceipt = marketplaceContract.purchaseNFT(tokenId, price).send();
        String purchaseTxHash = purchaseReceipt.getTransactionHash();
        System.out.println("✅ NFT 구매 완료!");
        System.out.println("🧾 구매 트랜잭션 해시: " + purchaseTxHash);

        // ✅ 2. NFT를 구매자에게 전송
        TransactionReceipt transferReceipt =
                videoNftContract
                        .safeTransferFrom(credentials.getAddress(), buyerAddress, tokenId)
                        .send();
        String transferTxHash = transferReceipt.getTransactionHash();
        System.out.println("✅ NFT를 구매자 주소로 전송 완료!");
        System.out.println("🧾 전송 트랜잭션 해시: " + transferTxHash);

        // 📦 DB 업데이트
        Nft nft =
                nftRepository
                        .findByTokenId(tokenId)
                        .orElseThrow(() -> new IllegalArgumentException("NFT를 찾을 수 없습니다."));

        nft.setIsListed(false);
        nft.setCurrentPrice(null);

        User buyer =
                userRepository
                        .findByWalletAddress(buyerAddress)
                        .orElseThrow(() -> new IllegalArgumentException("해당 지갑의 유저가 존재하지 않습니다."));
        nft.setOwner(buyer);
        nftRepository.save(nft);

        // 🧾 거래 트랜잭션 저장
        Transaction tradeTx =
                Transaction.builder()
                        .txHash(purchaseTxHash)
                        .type(TransactionType.TRADE)
                        .seller(originalOwner)
                        .buyer(buyer)
                        .tradePrice(price)
                        .nft(nft)
                        .build();
        transactionRepository.save(tradeTx);

        // 🧾 전송 트랜잭션 저장
        Transaction transferTx =
                Transaction.builder()
                        .txHash(transferTxHash)
                        .type(TransactionType.TRANSFER)
                        .seller(null)
                        .buyer(buyer)
                        .tradePrice(BigInteger.ZERO)
                        .nft(nft)
                        .build();
        transactionRepository.save(transferTx);

        return NftResponseDTO.NFTPurchaseResultDTO.builder()
                .purchaseTxHash(purchaseTxHash)
                .transferTxHash(transferTxHash)
                .tokenId(tokenId)
                .nftName(nft.getNftName())
                .nftSymbol(nft.getNftSymbol())
                .buyerAddress(buyerAddress)
                .tradePrice(price)
                .build();
    }

    //    public NftResponseDTO.NFTPurchaseResultDTO purchaseNFT(
    //            NftRequestDTO.NFTPurchaseRequestDTO request, Long loginUserId) throws Exception {
    //        BigInteger tokenId = request.getTokenId();
    //        BigInteger price = nftRepository.findByTokenId(tokenId).get().getCurrentPrice();
    //        User originalOwner = nftRepository.findByTokenId(tokenId).get().getOwner();
    //
    //        // 🔎 구매자 지갑 주소
    //        String buyerAddress =
    //                userRepository
    //                        .findById(loginUserId)
    //                        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."))
    //                        .getWalletAddress();
    //
    //        Tuple4<BigInteger, String, BigInteger, Boolean> listing =
    //                marketplaceContract.listings(tokenId).send();
    //
    //        BigInteger onChainPrice = listing.getValue3(); // 2번째 값: 가격 (wei)
    //        System.out.println("📦 체인 상 가격 (wei): " + onChainPrice);
    //        System.out.println(
    //                "💰 체인 상 가격 (MATIC): "
    //                        + Convert.fromWei(new BigDecimal(onChainPrice), Convert.Unit.ETHER)
    //                        + " MATIC");
    //        System.out.println("📦 컨트랙트로 전달하는 가격(wei): " + price);
    //        System.out.println("🧾 tx.sender: " + credentials.getAddress());
    //        System.out.println("🧾 chain approved: " +
    // videoNftContract.getApproved(tokenId).send());
    //        System.out.println("🧾 ownerOf: " + videoNftContract.ownerOf(tokenId).send());
    //        // 🔄 스마트 컨트랙트에 구매 요청 (ETH 포함)
    //        TransactionReceipt receipt = marketplaceContract.purchaseNFT(tokenId, price).send();
    //
    //        String txHash = receipt.getTransactionHash();
    //
    //        // 📂 DB 업데이트
    //        Nft nft =
    //                nftRepository
    //                        .findByTokenId(tokenId)
    //                        .orElseThrow(() -> new IllegalArgumentException("NFT를 찾을 수 없습니다."));
    //
    //        nft.setIsListed(false);
    //        nft.setCurrentPrice(price);
    //
    //        // 🔁 소유자 업데이트
    //        User buyer =
    //                userRepository
    //                        .findByWalletAddress(buyerAddress)
    //                        .orElseThrow(() -> new IllegalArgumentException("해당 지갑의 유저가 존재하지
    // 않습니다."));
    //        nft.setOwner(buyer);
    //
    //        nftRepository.save(nft);
    //
    //        // 🧾 트랜잭션 저장
    //        Transaction transaction =
    //                Transaction.builder()
    //                        .txHash(txHash)
    //                        .type(TransactionType.TRADE)
    //                        .seller(originalOwner)
    //                        .buyer(buyer)
    //                        .tradePrice(price)
    //                        .nft(nft)
    //                        .build();
    //        transactionRepository.save(transaction);
    //
    //        return NftResponseDTO.NFTPurchaseResultDTO.builder()
    //                .transactionHash(txHash)
    //                .tokenId(tokenId)
    //                .nftName(nft.getNftName())
    //                .nftSymbol(nft.getNftSymbol())
    //                .buyerAddress(buyerAddress)
    //                .tradePrice(price)
    //                .build();
    //    }

    public NftResponseDTO.NFTPurchaseResultGetDTO purchaseNftResultGet(
            NftRequestDTO.NFTPurchaseResultGetDTO request) throws Exception {

        BigInteger tokenId = request.getTokenId();

        // 📦 DB 업데이트
        Nft nft =
                nftRepository
                        .findByTokenId(tokenId)
                        .orElseThrow(() -> new IllegalArgumentException("NFT를 찾을 수 없습니다."));

        nft.setIsListed(false);
        nft.setCurrentPrice(null);

        User buyer =
                userRepository
                        .findByWalletAddress(request.getBuyerAddress())
                        .orElseThrow(() -> new IllegalArgumentException("해당 지갑의 유저가 존재하지 않습니다."));
        nft.setOwner(buyer);
        nftRepository.save(nft);

        // 🧾 트랜잭션 저장
        Transaction transaction =
                Transaction.builder()
                        .txHash(request.getTxHash())
                        .type(TransactionType.TRADE)
                        .seller(nft.getOwner())
                        .buyer(buyer)
                        .tradePrice(request.getTradePrice())
                        .nft(nft)
                        .build();
        transactionRepository.save(transaction);

        return NftResponseDTO.NFTPurchaseResultGetDTO.builder()
                .txHash(request.getTxHash())
                .tokenId(tokenId)
                .nftName(nft.getNftName())
                .nftSymbol(nft.getNftSymbol())
                .buyerAddress(request.getBuyerAddress())
                .tradePrice(request.getTradePrice())
                .build();
    }

    public List<NftResponseDTO.NFTTradeHistoryDTO> getTradeHistoryByVideoId(Long videoId) {
        List<Transaction> transactions =
                transactionRepository.findByVideoIdAndTypeOrderByCreatedAtDesc(
                        videoId, TransactionType.TRADE);
        return nftConverter.toTradeHistoryDTOList(transactions);
    }

    public List<NftResponseDTO.listedNFTInfoDTO> getListedNftsByVideoId(
            Long videoId, Long loginUserId) throws Exception {
        // 1. 컨트랙트에서 판매중인 NFT 목록 호출
        Tuple3<List<BigInteger>, List<String>, List<BigInteger>> result =
                marketplaceContract.getListedNFTs().send();

        List<BigInteger> tokenIds = result.component1();
        List<String> sellerAddresses = result.component2();

        System.out.println("[SERVICE] 가져온 tokenIds 수: " + tokenIds.size());
        System.out.println("[SERVICE] 가져온 sellerAddresses 수: " + sellerAddresses.size());

        List<NftResponseDTO.listedNFTInfoDTO> listedNfts = new ArrayList<>();

        // 로그인한 사용자 정보 가져오기
        Optional<User> loginUser =
                loginUserId != null ? userRepository.findById(loginUserId) : Optional.empty();

        BigInteger floorPrice = null; // floorPrice 초기화

        for (int i = 0; i < tokenIds.size(); i++) {
            BigInteger tokenId = tokenIds.get(i);

            Optional<Nft> optionalNft = nftRepository.findByTokenId(tokenId);

            if (optionalNft.isPresent()) {
                Nft nft = optionalNft.get();
                Long nftVideoId = nft.getVideo().getId();

                System.out.println(
                        "[SERVICE] DB에서 찾은 NFT ID: "
                                + nft.getId()
                                + ", videoId: "
                                + nftVideoId
                                + ", isListed: "
                                + nft.getIsListed());

                // 해당 비디오의 NFT만 필터링
                if (!nft.getVideo().getId().equals(videoId)) {

                    continue;
                }

                // listed 값이 true인 경우 floorPrice 계산
                if (nft.getIsListed()) {
                    if (floorPrice == null || nft.getCurrentPrice().compareTo(floorPrice) < 0) {
                        floorPrice = nft.getCurrentPrice();
                    }
                }

                // DTO 변환
                NftResponseDTO.listedNFTInfoDTO dto =
                        nftConverter.toListedNFTInfoDTO(nft, sellerAddresses.get(i), floorPrice);
                listedNfts.add(dto);
            }
        }

        System.out.println("[SERVICE] 최종 반환할 listedNfts 크기: " + listedNfts.size());
        return listedNfts;
    }

    public List<NftResponseDTO.MyNftDTO> getMyNFTs(Long userId) {
        List<Nft> myNFTs = nftRepository.findByOwnerId(userId);
        return myNFTs.stream()
                .map(
                        nft -> {
                            // 해당 비디오의 현재 판매 중인 NFT들 중 가장 낮은 가격 조회
                            BigInteger floorPrice =
                                    nftRepository
                                            .findMinCurrentPriceByVideoIdAndIsListed(
                                                    nft.getVideo().getId())
                                            .orElse(null); // 판매 중인 NFT가 없는 경우 null로 설정

                            return NftResponseDTO.MyNftDTO.builder()
                                    .tokenId(nft.getTokenId())
                                    .isListed(nft.getIsListed())
                                    .nftName(nft.getNftName())
                                    .nftSymbol(nft.getNftSymbol())
                                    .videoId(nft.getVideo().getId())
                                    .videoThumbnailUrl(nft.getVideo().getThumbnailUrl())
                                    .videoTitle(nft.getVideo().getTitle())
                                    .videoUrl(nft.getVideo().getFileUrl())
                                    .creatorId(nft.getVideo().getCreator().getId())
                                    .creatorNickname(nft.getVideo().getCreator().getNickname())
                                    .creatorProfileUrl(
                                            nft.getVideo().getCreator().getProfile_image())
                                    .purchasedPrice(nft.getCurrentPrice())
                                    .currentPrice(nft.getCurrentPrice())
                                    .floorPrice(floorPrice)
                                    .mintPrice(nft.getMintPrice())
                                    .build();
                        })
                .toList();
    }

    @Transactional
    public VideoInterest registerVideoInterest(Long userId, Long videoId) {
        // 사용자와 비디오 존재 여부 확인
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Video video =
                videoRepository
                        .findById(videoId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.VIDEO_NOT_EXIST));

        // 이미 등록된 관심이 있는지 확인
        videoInterestRepository
                .findByUserIdAndVideoId(userId, videoId)
                .ifPresent(
                        interest -> {
                            throw new GeneralException(ErrorStatus._BAD_REQUEST);
                        });

        // 새로운 관심 등록
        VideoInterest videoInterest = VideoInterest.builder().user(user).video(video).build();

        return videoInterestRepository.save(videoInterest);
    }

    @Transactional
    public void deleteVideoInterest(Long userId, Long videoId) {
        // 사용자와 비디오 존재 여부 확인
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Video video =
                videoRepository
                        .findById(videoId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.VIDEO_NOT_EXIST));

        // 관심이 있는지 확인
        VideoInterest videoInterest =
                videoInterestRepository
                        .findByUserIdAndVideoId(userId, videoId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));

        // 관심 삭제
        videoInterestRepository.delete(videoInterest);
    }
}
