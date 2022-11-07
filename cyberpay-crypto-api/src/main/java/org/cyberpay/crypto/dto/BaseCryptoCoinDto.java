package org.cyberpay.crypto.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "基础加密币信息")
public class BaseCryptoCoinDto implements Serializable {

    /**
     * 加密币ID
     */
    private String cryptoId;

    private String fullName;

    /**
     * 加密币代码
     */
    private String symbol;

    /**
     * 加密币图片
     */
    private String coinImage;

    /**
     * 加密币icon图片,移动端使用
     */
    private String coinIconImage;

    /**
     * 是否默认币种
     */
    private String isDefault;

    private String extend;

    private String extend2;

    private Date createTime;

    private Date updateTime;
}
