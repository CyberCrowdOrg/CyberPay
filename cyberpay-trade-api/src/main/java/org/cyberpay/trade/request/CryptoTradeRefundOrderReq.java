package org.cyberpay.trade.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "加密币退款订单接口请求参数")
public class CryptoTradeRefundOrderReq extends BaseRequest implements Serializable {

    @ApiModelProperty(value = "原订单号",required = true)
    private String orgOrderNo;

    @ApiModelProperty(value = "退款订单号",required = true)
    private String refundOrderNo;

    public String getOrgOrderNo() {
        return orgOrderNo;
    }

    public void setOrgOrderNo(String orgOrderNo) {
        this.orgOrderNo = orgOrderNo;
    }

    public String getRefundOrderNo() {
        return refundOrderNo;
    }

    public void setRefundOrderNo(String refundOrderNo) {
        this.refundOrderNo = refundOrderNo;
    }
}
