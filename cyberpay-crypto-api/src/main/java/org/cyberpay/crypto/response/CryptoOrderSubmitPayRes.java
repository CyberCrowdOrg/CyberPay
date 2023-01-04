package org.cyberpay.crypto.response;

import lombok.Data;
import org.cyberpay.crypto.dto.CryptoAppProtocolDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class CryptoOrderSubmitPayRes extends BaseResponse implements Serializable {

    private String bizFlowNo;
    private String receiveCoin;
    private BigDecimal receiveAmount;
    private String receiveAddress;
    private String link;

    private Date closeTime;
    //加密币应用协议适配
    private List<CryptoAppProtocolDto> cryptoAppProtocolList;

}
