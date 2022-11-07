package org.cyberpay.trade.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.cyberpay.trade.enums.TradeReturnCodeEnum;

import java.io.Serializable;

@Data
public class BaseResponse implements Serializable {

    @ApiModelProperty(value = "返回码",required = true)
    private String code = "SUCCESS";
    @ApiModelProperty(value = "返回码消息描述",required = true)
    private String message = "success";

    public void setReturnEnum(TradeReturnCodeEnum tradeReturnCodeEnum){
        this.code = tradeReturnCodeEnum.getCode();
        this.message = tradeReturnCodeEnum.getName();
    }
}
