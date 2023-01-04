package org.cyberpay.crypto.node;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class Web3JGetBalanceRes implements Serializable {

    private BigInteger ethBalance;

    private BigInteger tokenBalance;
}
