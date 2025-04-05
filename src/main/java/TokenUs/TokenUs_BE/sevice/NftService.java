package TokenUs.TokenUs_BE.sevice;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import TokenUs.TokenUs_BE.converter.NftConverter;
import TokenUs.TokenUs_BE.domain.Nft;
import TokenUs.TokenUs_BE.dto.NftRequestDTO;
import TokenUs.TokenUs_BE.dto.NftResponseDTO;
import TokenUs.TokenUs_BE.repository.NftRepository;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.web3.contract.VideoNFT_ABI;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

@Service
public class NftService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final VideoNFT_ABI contract;
    private final NftConverter nftConverter;
    private final NftRepository nftRepository;
    private final UserRepository userRepository;
    private static final String TRANSFER_EVENT_HASH =
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

    public NftService(
            Web3j web3j,
            @Value("${web3.private-key}") String privateKey,
            @Value("${web3.conrtact-address}") String contractAddress,
            @Value(("${CHAIN_ID}")) long chainId,
            NftConverter nftConverter,
            NftRepository nftRepository,
            UserRepository userRepository)
            throws Exception {
        this.web3j = web3j;

        this.credentials = Credentials.create(privateKey); // Private Key 불러오기

        // ✅ Chain ID를 포함한 트랜잭션 매니저 사용
        RawTransactionManager txManager = new RawTransactionManager(web3j, credentials, chainId);

        // ✅ 트랜잭션 매니저 + gas provider와 함께 계약 로드
        this.contract =
                VideoNFT_ABI.load(
                        contractAddress,
                        web3j,
                        txManager,
                        new StaticGasProvider(
                                BigInteger.valueOf(30_000_000_000L), // gas price (30 Gwei)
                                BigInteger.valueOf(6_500_000) // gas limit
                                ));
        this.nftConverter = nftConverter;
        this.nftRepository = nftRepository;
        this.userRepository = userRepository;
    }

    public NftResponseDTO.NFTMintResultDTO mintVideoNFT(NftRequestDTO.NFTMintRequestDTO request)
            throws Exception {
        // 1. 스마트 컨트랙트 호출
        TransactionReceipt receipt =
                contract.mintVideoNFT(
                                request.getVideoId(),
                                request.getNftName(),
                                request.getNftSymbol(),
                                request.getMetadataUri(),
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
                            txHash,
                            getUserIdFromAddress(request.getCreatorAddress()));

            savedNfts.add(nftRepository.save(nft));
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
        return contract.safeTransferFrom(from, to, tokenId).send().getTransactionHash();
    }
}
