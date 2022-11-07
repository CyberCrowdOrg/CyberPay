package org.cyberpay.crypto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "法币支持列表")
public class FiatSupportDto implements Serializable {

    @ApiModelProperty(value = "法币ID")
    private String fiatCoinId;

    @ApiModelProperty(value = "法币名称")
    private String fiatCoinName;

    @ApiModelProperty(value = "法币单位")
    private String fiatCoinUint;

    @ApiModelProperty(value = "法币代码")
    private String symbol;

    @ApiModelProperty(value = "法币图片")
    private String fiatCoinImage;

}
