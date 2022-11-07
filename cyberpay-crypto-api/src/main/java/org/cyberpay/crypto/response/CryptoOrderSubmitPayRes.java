package org.cyberpay.crypto.response;

import lombok.Data;
import org.cyberpay.crypto.dto.CryptoAppProtocolDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CryptoOrderSubmitPayRes extends BaseResponse implements Serializable {

    private String bizFlowNo;
    private String receiveCoin;
    private BigDecimal receiveAmount;
    private String receiveAddress;
    private String link;

    //加密币应用协议适配
    private List<CryptoAppProtocolDto> cryptoAppProtocolList;

}
