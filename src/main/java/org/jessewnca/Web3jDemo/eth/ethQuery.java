package org.jessewnca.Web3jDemo.eth;

import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Async;

import java.math.BigInteger;

/**
 * @author jesse.huang
 */

@Slf4j
public class ethQuery {

    private final String ETH_MAINNET = "https://mainnet.infura.io/v3/f3c4801ceac2450c8b1a19684e59d67a";

    private final String TRON_NODE = "http://47.95.206.44:50545/jsonrpc";

    private final String USDT_CONTRACT = "0xdAC17F958D2ee523a2206206994597C13D831ec7";

    private final String TOPIC = "0x8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925";


    public void getEthEvent() {
        //链接主网节点
        Web3j ethWeb3j = Web3j.build(new HttpService(ETH_MAINNET));
        //实例化过滤器
        EthFilter eventFilter = new EthFilter(DefaultBlockParameter.valueOf(BigInteger.valueOf(20680563)), DefaultBlockParameterName.LATEST, USDT_CONTRACT);
        //添加事件主题过滤
        eventFilter.addSingleTopic(TOPIC);
        //开始事件订阅
        Disposable subscribe = ethWeb3j.ethLogFlowable(eventFilter).subscribe(eventLog -> {
            log.info(eventLog.toString());
        });
    }

    public void getEthGasPrice() {
        //链接主网节点
        Web3j ethWeb3j = Web3j.build(new HttpService(ETH_MAINNET));
        try {
            Response<String> ethNetVersionResponse = ethWeb3j.ethGasPrice().send();
            String ethNetVersion = ethNetVersionResponse.getResult();
            BigInteger gasPrice =  new BigInteger(ethNetVersion.substring(2),16);
            log.info(gasPrice.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void getEthEventModifiedInterval() {
        //链接主网节点
        Web3j ethWeb3j = Web3j.build(new HttpService(ETH_MAINNET), 20 * 1000, Async.defaultExecutorService());
        //实例化过滤器
        EthFilter eventFilter = new EthFilter(DefaultBlockParameter.valueOf(BigInteger.valueOf(20680563)), DefaultBlockParameterName.LATEST, USDT_CONTRACT);
        //添加事件主题过滤
        eventFilter.addSingleTopic(TOPIC);
        //开始事件订阅
        Disposable subscribe = ethWeb3j.ethLogFlowable(eventFilter).subscribe(eventLog -> {
            log.info(eventLog.toString());
        });

    }

    public static void main(String[] args) {

    }


}
