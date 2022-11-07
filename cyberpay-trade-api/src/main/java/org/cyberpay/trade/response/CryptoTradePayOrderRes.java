package org.cyberpay.trade.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.cyberpay.trade.dto.CryptoReceiveAdapterDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "加密币交易支付订单接口响应参数")
public class CryptoTradePayOrderRes extends BaseResponse implements Serializable {

    @ApiModelProperty(value = "交易系统流水号")
    private String tradeFlowNo;
    @ApiModelProperty(value = "收款币种")
    private String receiveCoin;
    @ApiModelProperty(value = "收款金额")
    private BigDecimal receiveAmount;
    @ApiModelProperty(value = "收款地址")
    private String receiveAddress;
    @ApiModelProperty(value = "系统收银台页面地址")
    private String link;

    @ApiModelProperty(value = "加密币应用收款地址适配")
    private List<CryptoReceiveAdapterDto> cryptoReceiveAdapters;

}
