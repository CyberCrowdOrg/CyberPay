package org.cyberpay.crypto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(description="基础法币")
public class BaseFiatCoinDto implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "是否可用")
    private String isAvailable;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作备注")
    private String operatorNote;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
