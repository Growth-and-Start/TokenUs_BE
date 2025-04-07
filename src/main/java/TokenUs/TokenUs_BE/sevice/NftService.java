package TokenUs.TokenUs_BE.sevice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import TokenUs.TokenUs_BE.converter.NftConverter;
import TokenUs.TokenUs_BE.domain.Nft;
import TokenUs.TokenUs_BE.domain.Transaction;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.enums.TransactionType;
import TokenUs.TokenUs_BE.dto.NftRequestDTO;
import TokenUs.TokenUs_BE.dto.NftResponseDTO;
import TokenUs.TokenUs_BE.repository.NftRepository;
import TokenUs.TokenUs_BE.repository.TransactionRepository;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.web3.contract.VideoNftMarketplace_ABI;
import TokenUs.TokenUs_BE.web3.contract.VideoNft_ABI;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

@Service
public class NftService {

    private final Web3j web3j;
    private final Credentials credentials;

    private final VideoNft_ABI videoNftContract;
    private final VideoNftMarketplace_ABI marketplaceContract;

    private final NftConverter nftConverter;
    private final NftRepository nftRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

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
            TransactionRepository transactionRepository)
            throws Exception {
        this.web3j = web3j;

        this.credentials = Credentials.create(privateKey); // Private Key 불러오기

        // ✅ Chain ID를 포함한 트랜잭션 매니저 사용
        RawTransactionManager txManager = new RawTransactionManager(web3j, credentials, chainId);

        // ✅ 트랜잭션 매니저 + gas provider와 함께 계약 로드
        this.videoNftContract =
                VideoNft_ABI.load(
                        videoNftcontractAddress,
                        web3j,
                        txManager,
                        new StaticGasProvider(
                                BigInteger.valueOf(30_000_000_000L), // gas price (30 Gwei)
                                BigInteger.valueOf(6_500_000) // gas limit
                                ));

        this.marketplaceContract =
                VideoNftMarketplace_ABI.load(
                        marketplaceContractAddress,
                        web3j,
                        txManager,
                        new StaticGasProvider(
                                BigInteger.valueOf(30_000_000_000L),
                                BigInteger.valueOf(6_500_000)));

        this.nftConverter = nftConverter;
        this.nftRepository = nftRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
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
                            request, tokenId, getUserIdFromAddress(request.getCreatorAddress()));

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

    private List<BigInteger> extractTokenIdsFromLogs(TransactionReceipt receipt) {
        List<BigInteger> tokenIds = new ArrayList<>();

        for (Log log : receipt.getLogs()) {
            if (log.getTopics().size() == 4 && log.getTopics().get(0).equals(TRANSFER_EVENT_HASH)) {

                BigInteger tokenId = new BigInteger(log.getTopics().get(3).substring(2), 16);
                tokenIds.add(tokenId);
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
                .transactionHash(receipt.getTransactionHash())
                .tokenId(request.getTokenId())
                .price(request.getPrice())
                .sellerAddress(walletAddress)
                .isListed(nft.getIsListed())
                .build();
    }

    public List<NftResponseDTO.listedNFTInfoDTO> getListedNfts() throws Exception {
        // 1. 컨트랙트에서 판매중인 NFT 목록 호출
        Tuple3<List<BigInteger>, List<String>, List<BigInteger>> result =
                marketplaceContract.getListedNFTs().send();

        List<BigInteger> tokenIds = result.component1();
        List<String> sellerAddresses = result.component2();
        List<BigInteger> prices = result.component3();

        List<NftResponseDTO.listedNFTInfoDTO> listedNfts = new ArrayList<>();

        for (int i = 0; i < tokenIds.size(); i++) {
            BigInteger tokenId = tokenIds.get(i);

            Optional<Nft> optionalNft = nftRepository.findByTokenId(tokenId);

            if (optionalNft.isPresent()) {
                Nft nft = optionalNft.get();

                // DTO 변환
                NftResponseDTO.listedNFTInfoDTO dto =
                        NftResponseDTO.listedNFTInfoDTO
                                .builder()
                                .tokenId(tokenId)
                                .currentPrice(nft.getCurrentPrice())
                                .videoId(nft.getVideo().getId())
                                .isListed(nft.getIsListed())
                                .creatorId(nft.getVideo().getCreator().getId())
                                .sellerWallet(sellerAddresses.get(i))
                                .build();

                listedNfts.add(dto);
            }
        }

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
                .transactionHash(receipt.getTransactionHash())
                .tokenId(request.getTokenId())
                .sellerAddress(walletAddress)
                .isListed(nft.getIsListed())
                .build();
    }

    public NftResponseDTO.NFTPurchaseResultDTO purchaseNFT(
            NftRequestDTO.NFTPurchaseRequestDTO request, Long loginUserId) throws Exception {
        BigInteger tokenId = request.getTokenId();
        BigInteger price = nftRepository.findByTokenId(tokenId).get().getCurrentPrice();
        User originalOwner = nftRepository.findByTokenId(tokenId).get().getOwner();

        // 🔎 구매자 지갑 주소
        String buyerAddress =
                userRepository
                        .findById(loginUserId)
                        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."))
                        .getWalletAddress();

        Tuple4<BigInteger, String, BigInteger, Boolean> listing =
                marketplaceContract.listings(tokenId).send();

        BigInteger onChainPrice = listing.getValue3(); // 2번째 값: 가격 (wei)
        System.out.println("📦 체인 상 가격 (wei): " + onChainPrice);
        System.out.println(
                "💰 체인 상 가격 (MATIC): "
                        + Convert.fromWei(new BigDecimal(onChainPrice), Convert.Unit.ETHER)
                        + " MATIC");
        System.out.println("📦 컨트랙트로 전달하는 가격(wei): " + price);
        System.out.println("🧾 tx.sender: " + credentials.getAddress());
        System.out.println("🧾 chain approved: " + videoNftContract.getApproved(tokenId).send());
        System.out.println("🧾 ownerOf: " + videoNftContract.ownerOf(tokenId).send());
        // 🔄 스마트 컨트랙트에 구매 요청 (ETH 포함)
        TransactionReceipt receipt = marketplaceContract.purchaseNFT(tokenId, price).send();

        String txHash = receipt.getTransactionHash();

        // 📂 DB 업데이트
        Nft nft =
                nftRepository
                        .findByTokenId(tokenId)
                        .orElseThrow(() -> new IllegalArgumentException("NFT를 찾을 수 없습니다."));

        nft.setIsListed(false);
        nft.setCurrentPrice(price);

        // 🔁 소유자 업데이트
        User buyer =
                userRepository
                        .findByWalletAddress(buyerAddress)
                        .orElseThrow(() -> new IllegalArgumentException("해당 지갑의 유저가 존재하지 않습니다."));
        nft.setOwner(buyer);

        nftRepository.save(nft);

        // 🧾 트랜잭션 저장
        Transaction transaction =
                Transaction.builder()
                        .txHash(txHash)
                        .type(TransactionType.TRADE)
                        .seller(originalOwner)
                        .buyer(buyer)
                        .tradePrice(price)
                        .nft(nft)
                        .build();
        transactionRepository.save(transaction);

        return NftResponseDTO.NFTPurchaseResultDTO.builder()
                .transactionHash(txHash)
                .tokenId(tokenId)
                .buyerAddress(buyerAddress)
                .tradePrice(price)
                .build();
    }
}
