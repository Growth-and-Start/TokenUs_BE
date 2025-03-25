package TokenUs.TokenUs_BE.sevice;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import TokenUs.TokenUs_BE.web3.contract.VideoNFT_ABI;
import org.hibernate.TransactionException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

@Service
public class NftService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final VideoNFT_ABI contract;

    public NftService(
            Web3j web3j,
            @Value("${web3.private-key}") String privateKey,
            @Value("${web3.conrtact-address}") String contractAddress,
            @Value(("${CHAIN_ID}")) long chainId)
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
    }

    public String mintVideoNFT(
            String metadataURI, BigInteger totalSupply, String NFTname, String NFTsymbol)
            throws Exception {

        System.out.println("▶ Sending mint request to contract...");
        System.out.println("▶ metadataURI = " + metadataURI);
        System.out.println("▶ totalSupply = " + totalSupply);
        System.out.println("▶ NFT name = " + NFTname);
        System.out.println("▶ NFT symbol = " + NFTsymbol);
        System.out.println("🟡 Credentials address: " + credentials.getAddress());

        try {
            String txHash =
                    contract.mintVideoNFT(metadataURI, totalSupply, NFTname, NFTsymbol)
                            .send()
                            .getTransactionHash();
            return txHash;
        } catch (TransactionException e) {
            throw new RuntimeException("💥 민팅 실패: " + e.getMessage());
        }
    }

    public String safeTransferNFT(String from, String to, BigInteger tokenId) throws Exception {
        return contract.safeTransferFrom(from, to, tokenId).send().getTransactionHash();
    }
}
