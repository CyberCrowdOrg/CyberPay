package org.cyberpay.crypto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class CryptoPreOrderRes extends BaseResponse implements Serializable {

    private String bizFlowNo;
    //前端链接
    private String link;

}
