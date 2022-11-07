package org.cyberpay.crypto.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "加密币退款订单创建响应参数")
public class CryptoRefundOrderRes extends BaseResponse {

    private String bizFlowNo;
    private String transHash;

}
