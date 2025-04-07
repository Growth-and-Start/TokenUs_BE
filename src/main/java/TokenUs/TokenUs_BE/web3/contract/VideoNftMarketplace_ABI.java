package TokenUs.TokenUs_BE.web3.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * Auto generated code.
 *
 * <p><strong>Do not modify!</strong>
 *
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the <a
 * href="https://github.com/hyperledger-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.3.
 */
@SuppressWarnings("rawtypes")
public class VideoNftMarketplace_ABI extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_APPROVEDOPERATORS = "approvedOperators";

    public static final String FUNC_LISTINGS = "listings";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_APPROVEOPERATOR = "approveOperator";

    public static final String FUNC_LISTNFT = "listNFT";

    public static final String FUNC_DELISTNFT = "delistNFT";

    public static final String FUNC_PURCHASENFT = "purchaseNFT";

    public static final String FUNC_GETLISTEDNFTS = "getListedNFTs";

    public static final Event NFTDELISTED_EVENT =
            new Event(
                    "NFTDelisted",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Uint256>(true) {},
                            new TypeReference<Address>(true) {}));
    ;

    public static final Event NFTLISTED_EVENT =
            new Event(
                    "NFTListed",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Uint256>(true) {},
                            new TypeReference<Address>(true) {},
                            new TypeReference<Uint256>() {}));
    ;

    public static final Event NFTPURCHASED_EVENT =
            new Event(
                    "NFTPurchased",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Uint256>(true) {},
                            new TypeReference<Address>(true) {},
                            new TypeReference<Uint256>() {}));
    ;

    public static final Event OPERATORAPPROVED_EVENT =
            new Event(
                    "OperatorApproved",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT =
            new Event(
                    "OwnershipTransferred",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {},
                            new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected VideoNftMarketplace_ABI(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected VideoNftMarketplace_ABI(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected VideoNftMarketplace_ABI(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected VideoNftMarketplace_ABI(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<NFTDelistedEventResponse> getNFTDelistedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                staticExtractEventParametersWithLog(NFTDELISTED_EVENT, transactionReceipt);
        ArrayList<NFTDelistedEventResponse> responses =
                new ArrayList<NFTDelistedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NFTDelistedEventResponse typedResponse = new NFTDelistedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.seller = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static NFTDelistedEventResponse getNFTDelistedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues =
                staticExtractEventParametersWithLog(NFTDELISTED_EVENT, log);
        NFTDelistedEventResponse typedResponse = new NFTDelistedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.seller = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<NFTDelistedEventResponse> nFTDelistedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getNFTDelistedEventFromLog(log));
    }

    public Flowable<NFTDelistedEventResponse> nFTDelistedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NFTDELISTED_EVENT));
        return nFTDelistedEventFlowable(filter);
    }

    public static List<NFTListedEventResponse> getNFTListedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                staticExtractEventParametersWithLog(NFTLISTED_EVENT, transactionReceipt);
        ArrayList<NFTListedEventResponse> responses =
                new ArrayList<NFTListedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NFTListedEventResponse typedResponse = new NFTListedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.seller = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.price = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static NFTListedEventResponse getNFTListedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues =
                staticExtractEventParametersWithLog(NFTLISTED_EVENT, log);
        NFTListedEventResponse typedResponse = new NFTListedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.seller = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.price = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<NFTListedEventResponse> nFTListedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getNFTListedEventFromLog(log));
    }

    public Flowable<NFTListedEventResponse> nFTListedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NFTLISTED_EVENT));
        return nFTListedEventFlowable(filter);
    }

    public static List<NFTPurchasedEventResponse> getNFTPurchasedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                staticExtractEventParametersWithLog(NFTPURCHASED_EVENT, transactionReceipt);
        ArrayList<NFTPurchasedEventResponse> responses =
                new ArrayList<NFTPurchasedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NFTPurchasedEventResponse typedResponse = new NFTPurchasedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.price = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static NFTPurchasedEventResponse getNFTPurchasedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues =
                staticExtractEventParametersWithLog(NFTPURCHASED_EVENT, log);
        NFTPurchasedEventResponse typedResponse = new NFTPurchasedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.buyer = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.price = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<NFTPurchasedEventResponse> nFTPurchasedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getNFTPurchasedEventFromLog(log));
    }

    public Flowable<NFTPurchasedEventResponse> nFTPurchasedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NFTPURCHASED_EVENT));
        return nFTPurchasedEventFlowable(filter);
    }

    public static List<OperatorApprovedEventResponse> getOperatorApprovedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                staticExtractEventParametersWithLog(OPERATORAPPROVED_EVENT, transactionReceipt);
        ArrayList<OperatorApprovedEventResponse> responses =
                new ArrayList<OperatorApprovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OperatorApprovedEventResponse typedResponse = new OperatorApprovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.operator = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.approved = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OperatorApprovedEventResponse getOperatorApprovedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues =
                staticExtractEventParametersWithLog(OPERATORAPPROVED_EVENT, log);
        OperatorApprovedEventResponse typedResponse = new OperatorApprovedEventResponse();
        typedResponse.log = log;
        typedResponse.operator = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.approved = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<OperatorApprovedEventResponse> operatorApprovedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOperatorApprovedEventFromLog(log));
    }

    public Flowable<OperatorApprovedEventResponse> operatorApprovedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OPERATORAPPROVED_EVENT));
        return operatorApprovedEventFlowable(filter);
    }

    public static List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList =
                staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses =
                new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse =
                    new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnershipTransferredEventResponse getOwnershipTransferredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues =
                staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
        OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
        typedResponse.log = log;
        typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnershipTransferredEventFromLog(log));
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public RemoteFunctionCall<Boolean> approvedOperators(String param0) {
        final Function function =
                new Function(
                        FUNC_APPROVEDOPERATORS,
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Tuple4<BigInteger, String, BigInteger, Boolean>> listings(
            BigInteger param0) {
        final Function function =
                new Function(
                        FUNC_LISTINGS,
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint256>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Uint256>() {},
                                new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple4<BigInteger, String, BigInteger, Boolean>>(
                function,
                new Callable<Tuple4<BigInteger, String, BigInteger, Boolean>>() {
                    @Override
                    public Tuple4<BigInteger, String, BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, String, BigInteger, Boolean>(
                                (BigInteger) results.get(0).getValue(),
                                (String) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (Boolean) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> owner() {
        final Function function =
                new Function(
                        FUNC_OWNER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function =
                new Function(
                        FUNC_RENOUNCEOWNERSHIP,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function =
                new Function(
                        FUNC_TRANSFEROWNERSHIP,
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> approveOperator(String operator) {
        final Function function =
                new Function(
                        FUNC_APPROVEOPERATOR,
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, operator)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> listNFT(BigInteger tokenId, BigInteger price) {
        final Function function =
                new Function(
                        FUNC_LISTNFT,
                        Arrays.<Type>asList(
                                new org.web3j.abi.datatypes.generated.Uint256(tokenId),
                                new org.web3j.abi.datatypes.generated.Uint256(price)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> delistNFT(BigInteger tokenId) {
        final Function function =
                new Function(
                        FUNC_DELISTNFT,
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> purchaseNFT(
            BigInteger tokenId, BigInteger weiValue) {
        final Function function =
                new Function(
                        FUNC_PURCHASENFT,
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<Tuple3<List<BigInteger>, List<String>, List<BigInteger>>>
            getListedNFTs() {
        final Function function =
                new Function(
                        FUNC_GETLISTEDNFTS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<DynamicArray<Uint256>>() {},
                                new TypeReference<DynamicArray<Address>>() {},
                                new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<Tuple3<List<BigInteger>, List<String>, List<BigInteger>>>(
                function,
                new Callable<Tuple3<List<BigInteger>, List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple3<List<BigInteger>, List<String>, List<BigInteger>> call()
                            throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<List<BigInteger>, List<String>, List<BigInteger>>(
                                convertToNative((List<Uint256>) results.get(0).getValue()),
                                convertToNative((List<Address>) results.get(1).getValue()),
                                convertToNative((List<Uint256>) results.get(2).getValue()));
                    }
                });
    }

    @Deprecated
    public static VideoNftMarketplace_ABI load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new VideoNftMarketplace_ABI(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static VideoNftMarketplace_ABI load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new VideoNftMarketplace_ABI(
                contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static VideoNftMarketplace_ABI load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new VideoNftMarketplace_ABI(
                contractAddress, web3j, credentials, contractGasProvider);
    }

    public static VideoNftMarketplace_ABI load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return new VideoNftMarketplace_ABI(
                contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class NFTDelistedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public String seller;
    }

    public static class NFTListedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public String seller;

        public BigInteger price;
    }

    public static class NFTPurchasedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public String buyer;

        public BigInteger price;
    }

    public static class OperatorApprovedEventResponse extends BaseEventResponse {
        public String operator;

        public Boolean approved;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }
}
