package org.cyberpay.crypto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.cyberpay.crypto.dto.CryptoSupportDto;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "加密币种支持列表响应数据")
public class CryptoSupportListRes implements Serializable {

    @ApiModelProperty(value = "加密币支持列表")
    private List<CryptoSupportDto> cryptoSupportList;

}
