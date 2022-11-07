package org.cyberpay.crypto.node;

public interface NodeInterface {

    /**
     * 分配收款地址
     * @param nodeRequestDto
     * @return
     */
    NodeResponseDto generateAddress(NodeRequestDto nodeRequestDto);

    /**
     * 余额查询
     * @param nodeRequestDto
     * @return
     */
    NodeResponseDto queryBalance(NodeRequestDto nodeRequestDto);

    /**
     * 发送交易
     * @param nodeRequestDto
     * @return
     */
    NodeResponseDto sendTransaction(NodeRequestDto nodeRequestDto);

    /**
     * 确认交易
     * 根据交易hash查询交易
     * @param nodeRequestDto
     * @return
     */
    NodeResponseDto confirmTransaction(NodeRequestDto nodeRequestDto);

}
