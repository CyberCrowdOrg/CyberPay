package org.cyberpay.crypto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 加密币钱包协议
 * </p>
 *
 * @author gengchaonan
 * @since 2022-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="CryptoWalletProtocol对象", description="加密币钱包协议")
public class CryptoWalletProtocol implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "加密币代码")
    private String cryptoSymbol;

    @ApiModelProperty(value = "扫码协议")
    private String scanProtocol;

    @ApiModelProperty(value = "应用调起协议")
    private String schemeProtocol;

    @ApiModelProperty(value = "插件协议")
    private String pluginProtocol;

    private String extend;

    private String extend2;

    private Date createTime;

    private Date updateTime;


}
