package org.cyberpay.crypto.node;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChainNodeResDto implements Serializable {

    private Object result;

    private ErrorBean error;

    private String id;

    @Data
    public class ErrorBean{
        private int code;
        private String message;
    }
}
