package org.cyberpay.crypto.node;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.script.Script;
import org.bouncycastle.util.encoders.Hex;
import org.cyberpay.crypto.utils.HttpClientUtil;
import org.cyberpay.crypto.utils.HttpRequestMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "baseNodeFunction")
public class BaseNodeFunction {

    @Value("${node.get-block.api-key}")
    private String getBlockNodeApiKey;

    @Value("${node.feerate-query.bitcoin}")
    private String bitcoinFeeRateQueryUrl;
    @Value("${node.feerate-query.bitcoin-cash}")
    private String bitcoinCashFeeRateQueryUrl;
    @Value("${node.feerate-query.litecoin}")
    private String litecoinFeeRateQueryUrl;

    @Value("${unspent.bitcoin}")
    private String bitcoinUnspenQueryUrl;
    @Value("${unspent.bitcoin-cash}")
    private String bitcoinCashUnspenQueryUrl;
    @Value("${unspent.litecoin}")
    private String litecoinUnspenQueryUrl;




    public Map<String, String> headers(){
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        headers.put("x-api-key", getBlockNodeApiKey);
        return headers;
    }

    /**
     * 获取费率
     * BTC LTC BCH
     * @return
     */
    public BigDecimal queryFeeRate(String coinName) {
        try {
            String url = bitcoinFeeRateQueryUrl;
            if("BTC".equals(coinName)){
            }else if("BCH".equals(coinName)){
                url = bitcoinCashFeeRateQueryUrl;
            }else if("LTC".equals(coinName)){
                url = litecoinFeeRateQueryUrl;
            }
            String result = HttpClientUtil.sendHttp(HttpRequestMethod.HttpRequestMethodEnum.HttpGet,
                    url,null,null);
            JSONObject jsonObject = JSON.parseObject(result, JSONObject.class);
            BigDecimal fee = new BigDecimal(jsonObject.get("feerate").toString()).divide(new BigDecimal(100),8,BigDecimal.ROUND_HALF_DOWN);
            if(fee.compareTo(new BigDecimal(0.00000141)) < 0){
                return new BigDecimal(0.00000141).setScale(8,BigDecimal.ROUND_HALF_DOWN);
            }else {
                return fee;
            }
        } catch (Exception e) {
            return new BigDecimal(0.00000141).setScale(8,BigDecimal.ROUND_HALF_DOWN);
        }
    }

    /**
     * 查询出款地址未花费列表
     * BTC LTC BCH
     * @param address
     * @return
     */
    public List<UTXO> getUnspentList(String address,String coinName){

//        String s = HttpClientUtil.sendHttp(HttpRequestMethod.HttpRequestMethodEnum.HttpGet,
//                "https://api.blockcypher.com/v1/btc/test3/addrs/"+address +"?includeScript=true", null, null);
//        List<JSONObject> unspentList = JSON.parseArray(s,JSONObject.class);
        List<UTXO> utxos = new ArrayList<>();
        String url = "";
        if("BTC".equals(coinName)){
            url = bitcoinUnspenQueryUrl;
        }else if("BCH".equals(coinName)){
            url = bitcoinCashUnspenQueryUrl;
        }else if("LTC".equals(coinName)){
            url = litecoinUnspenQueryUrl;
        }
        String result = HttpClientUtil.sendHttp(HttpRequestMethod.HttpRequestMethodEnum.HttpGet,
                MessageFormat.format(url,address), null, null);
        List<JSONObject> unspentList = JSON.parseArray(result,JSONObject.class);
        if(null != unspentList && unspentList.size() > 0){
            for(JSONObject jsonObject:unspentList){
                String txHash = (String) jsonObject.get("mintTxid");
                int txInputN = (int)jsonObject.get("mintIndex");
                Long balance = Long.valueOf((int)jsonObject.get("value"));
                int blockHeight = (int) jsonObject.get("mintHeight");
                String script = (String)jsonObject.get("script");
                UTXO utxo = new UTXO(Sha256Hash.wrap(txHash),txInputN, Coin.valueOf(balance),blockHeight,false,new Script(Hex.decode(script)));
                utxos.add(utxo);
            }
        }
        return  utxos;
    }
}
