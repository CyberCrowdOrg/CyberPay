package org.cyberpay.trade.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseRequest implements Serializable {

    @ApiModelProperty(value = "签名")
    private String sign;
    @ApiModelProperty(value = "商户ID")
    private String merchantId;

}
