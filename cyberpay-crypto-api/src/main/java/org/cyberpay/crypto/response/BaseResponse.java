package org.cyberpay.crypto.response;

import lombok.Data;
import org.cyberpay.crypto.enums.ReturnCodeEnum;

import java.io.Serializable;

@Data
public class BaseResponse implements Serializable {

    private String code = "SUCCESS";
    private String message = "success";

    public void setReturnEnum(ReturnCodeEnum returnCodeEnum){
        this.code = returnCodeEnum.getCode();
        this.message = returnCodeEnum.getName();
    }

}
