package org.cyberpay.crypto.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CryptoAppProtocolDto implements Serializable {

    private String appName;

    private String scanProtocol;

    private String schemeProtocol;

    private String pluginProtocol;
}
