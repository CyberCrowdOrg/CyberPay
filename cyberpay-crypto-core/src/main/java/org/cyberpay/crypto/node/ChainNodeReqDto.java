package org.cyberpay.crypto.node;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChainNodeReqDto implements Serializable {

    private String id = "getblock.io";
    private String jsonrpc = "2.0";

    private String method;
    private Object [] params;
}
