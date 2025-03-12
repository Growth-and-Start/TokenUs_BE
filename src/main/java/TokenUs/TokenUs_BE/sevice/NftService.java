package TokenUs.TokenUs_BE.sevice;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import TokenUs.TokenUs_BE.web3.contract.VideoNFT_ABI;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.StaticGasProvider;

@Service
public class NftService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final VideoNFT_ABI contract;

    public NftService(
            Web3j web3j,
            @Value("${web3.private-key}") String privateKey,
            @Value("${web3.conrtact-address}") String contractAddress)
            throws Exception {
        this.web3j = web3j;

        this.credentials = Credentials.create(privateKey); // Private Key 불러오기

        this.contract =
                VideoNFT_ABI.load(
                        contractAddress,
                        web3j,
                        credentials,
                        new StaticGasProvider(
                                BigInteger.valueOf(1000000000), BigInteger.valueOf(6721975)));
    }

    public String mintVideoNFT(
            String metadataURI, BigInteger totalSupply, String NFTname, String NFTsymbol)
            throws Exception {
        return contract.mintVideoNFT(metadataURI, totalSupply, NFTname, NFTsymbol)
                .send()
                .getTransactionHash();
    }

    public String safeTransferNFT(String from, String to, BigInteger tokenId) throws Exception {
        return contract.safeTransferFrom(from, to, tokenId).send().getTransactionHash();
    }
}
