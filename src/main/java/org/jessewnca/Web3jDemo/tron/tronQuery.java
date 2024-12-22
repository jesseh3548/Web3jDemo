package org.jessewnca.Web3jDemo.tron;

import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static org.jessewnca.Web3jDemo.utils.AddressUtil.evmToTronAddress;
import static org.jessewnca.Web3jDemo.utils.AddressUtil.tronToEvmAddress;

/**
 * @author jesse.huang
 */

@Slf4j
public class tronQuery {
    private final String TRON_GRID = "https://api.trongrid.io/jsonrpc";

    private final String TRON_ANKR_MAINNET = "https://rpc.ankr.com/tron_jsonrpc";

    private final String TRON_USDT_CONTRACT = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";

    private final String TRON_TUSD_CONTRACT = "TUpMhErZL2fhh4sVNULAbNKLokS4GjC1F4";

    private final String TRON_TOPIC = "8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925";


    public void getTronGasPrice() {
        Web3j tronWeb3j = Web3j.build(new HttpService(TRON_GRID));
        try {
            Response<String> tronGasPriceResponse = tronWeb3j.ethGasPrice().send();
            String tronGasPrice = tronGasPriceResponse.getResult();
            BigInteger gasPrice = new BigInteger(tronGasPrice.substring(2), 16);
            log.info(gasPrice.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void getTronBlockHeight() {
        Web3j tronWeb3j = Web3j.build(new HttpService(TRON_GRID));
        try {
            Response<String> tronBlockHeightResponse = tronWeb3j.ethBlockNumber().send();
            String tronBlockHeight = tronBlockHeightResponse.getResult();
            BigInteger blockHeight = new BigInteger(tronBlockHeight.substring(2), 16);
            log.info(blockHeight.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void getTronEvent() throws NoSuchAlgorithmException {
        //链接主网节点
        Web3j tronWeb3j = Web3j.build(new HttpService(TRON_GRID));
        //实例化过滤器
        EthFilter eventFilter = new EthFilter(DefaultBlockParameter.valueOf(BigInteger.valueOf(65734000)), DefaultBlockParameterName.LATEST, "0x" + tronToEvmAddress(TRON_USDT_CONTRACT));
        //添加事件主题过滤
        eventFilter.addSingleTopic(TRON_TOPIC);
        //开始事件订阅
        Disposable subscribe = tronWeb3j.ethLogFlowable(eventFilter).subscribe(eventLog -> {

            log.info(eventLog.getTransactionHash().substring(2));
            log.info(eventLog.getBlockHash().substring(2));
            String blockNumberStr = eventLog.getBlockNumberRaw().substring(2);
            log.info(new BigInteger(blockNumberStr, 16).toString());
            String topicStr = eventLog.getTopics().get(1).substring(26);
            log.info(evmToTronAddress("0x" + topicStr));

            log.info(eventLog.toString());
        }, eventError -> {
            log.error(eventError.toString());
        });
    }

    public void getTronClientVersion() {
        Web3j tronWeb3j = Web3j.build(new HttpService(TRON_GRID));
        try {
            Response<String> tronClientVersionResponse = tronWeb3j.web3ClientVersion().send();
            String tronClientVersion = tronClientVersionResponse.getResult();
            log.info(tronClientVersion);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) {

    }

}
