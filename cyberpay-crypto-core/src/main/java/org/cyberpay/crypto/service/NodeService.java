package org.cyberpay.crypto.service;

import org.cyberpay.crypto.enums.CryptoNodeMethodEnum;
import org.cyberpay.crypto.enums.ReturnCodeEnum;
import org.cyberpay.crypto.mapper.CryptoAddressInfoMapper;
import org.cyberpay.crypto.model.CryptoAddressInfo;
import org.cyberpay.crypto.model.CryptoNodeMapping;
import org.cyberpay.crypto.node.NodeInterface;
import org.cyberpay.crypto.node.NodeRequestDto;
import org.cyberpay.crypto.node.NodeResponseDto;
import org.cyberpay.crypto.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("nodeService")
public class NodeService {

    @Autowired
    AddressPoolService addressPoolService;

    @Autowired
    CryptoAddressInfoMapper cryptoAddressInfoMapper;

    public NodeResponseDto callNode(NodeRequestDto nodeRequestDto,
                                    CryptoNodeMethodEnum cryptoNodeMethodEnum){

        CryptoNodeMapping cryptoNodeMapping = nodeRequestDto.getCryptoNodeMapping();

        if(CryptoNodeMethodEnum.generateAddress == cryptoNodeMethodEnum){
            //分配可用地址
            return assignAvailableAddresses(nodeRequestDto,cryptoNodeMapping);
        }else if(CryptoNodeMethodEnum.sendTransaction == cryptoNodeMethodEnum){
            NodeInterface nodeInterface = (NodeInterface) SpringContextUtil.getBean(cryptoNodeMapping.getNodeInterfaceName());
            return nodeInterface.sendTransaction(nodeRequestDto);
        }else if(CryptoNodeMethodEnum.confirmTransaction == cryptoNodeMethodEnum){
            NodeInterface nodeInterface = (NodeInterface) SpringContextUtil.getBean(cryptoNodeMapping.getNodeInterfaceName());
            return nodeInterface.confirmTransaction(nodeRequestDto);
        }else if(CryptoNodeMethodEnum.queryBalance == cryptoNodeMethodEnum){
            NodeInterface nodeInterface = (NodeInterface) SpringContextUtil.getBean(cryptoNodeMapping.getNodeInterfaceName());
            return nodeInterface.queryBalance(nodeRequestDto);
        }
        return null;
    }

    /**
     * 分配地址
     * @param nodeRequestDto
     * @param cryptoNodeMapping
     * @return
     */
    private NodeResponseDto assignAvailableAddresses(NodeRequestDto nodeRequestDto,
                                                     CryptoNodeMapping cryptoNodeMapping) {
        List<String> newAddressList = new ArrayList<>();
        String networkCode = cryptoNodeMapping.getNetworkCode();
        String cryptoSymbol = cryptoNodeMapping.getCryptoSymbol();
        //从地址池中获取地址
        String address_p = addressPoolService.getAddressFromAddressPoolCache(networkCode);
        if(!StringUtils.hasText(address_p)) {
            //地址池没有地址可用,从节点接口中生成
            NodeInterface nodeInterface = (NodeInterface) SpringContextUtil.getBean(cryptoNodeMapping.getNodeInterfaceName());
            int generateAddressNumber = cryptoNodeMapping.getGenerateAddressNumber().intValue();

            for (int i = 0; i < generateAddressNumber; i++) {
                NodeResponseDto nodeResponseDto = nodeInterface.generateAddress(nodeRequestDto);
                if(ReturnCodeEnum.SUCCESS.getCode().equals(nodeResponseDto.getReturnCode())) {
                    //保存到数据库
                    saveCryptoAddress(nodeResponseDto, cryptoSymbol, networkCode);
                    //最后一个分配给当前请求,添加到占用记录和
                    String address = nodeResponseDto.getAddress();
                    //保存到所有收款地址缓存中
                    addressPoolService.addAddressToAllAddressPoolCache(networkCode, address);
                    if (i == generateAddressNumber - 1) {
                        //保存到不可用地址池缓存中
                        addressPoolService.addAddressToUnAvailableAddressPool(networkCode, address, 0);
                        return nodeResponseDto;
                    } else {
                        //添加到可用地址池缓存中
                        addressPoolService.addAddressToAvailableAddressPool(networkCode, address, 0);
                        newAddressList.add(address);
                    }
                }
            }
        }else {
            NodeResponseDto nodeResponseDto = new NodeResponseDto();
            nodeResponseDto.setAddress(address_p);
            return nodeResponseDto;
        }
        return null;
    }

    private void saveCryptoAddress(NodeResponseDto nodeResponseDto,String cryptoSymbol,String networkCode) {

        CryptoAddressInfo cryptoAddressInfo = new CryptoAddressInfo();
        cryptoAddressInfo.setCryptoSymbol(cryptoSymbol);
        cryptoAddressInfo.setNetworkCode(networkCode);
        cryptoAddressInfo.setAddressType("trade");
        cryptoAddressInfo.setAddress(nodeResponseDto.getAddress());
        cryptoAddressInfo.setPrivateKey(nodeResponseDto.getPrivateKey());
        cryptoAddressInfo.setPublicKey(nodeResponseDto.getPublicKey());
        cryptoAddressInfo.setMnemonics(nodeResponseDto.getMnemonics());
        cryptoAddressInfo.setWalletFilePath(nodeResponseDto.getWalletFilePath());
        cryptoAddressInfo.setScore(0l);
        cryptoAddressInfo.setCreateTime(new Date());
        cryptoAddressInfo.setUpdateTime(new Date());
        cryptoAddressInfoMapper.insertSelective(cryptoAddressInfo);
    }
}
