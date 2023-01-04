package org.cyberpay.crypto.schedule;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.cyberpay.crypto.service.CryptoOrderService;
import org.springframework.beans.factory.annotation.Autowired;

//@Component
public class CryptoOrderJobHandler {

    @Autowired
    CryptoOrderService cryptoOrderService;

    /**
     * 关闭订单
     * @throws Exception
     */
    @XxlJob(value = "closeOrder")
    public void closeOrder() throws Exception {
        String jobParam = XxlJobHelper.getJobParam();
        cryptoOrderService.closeOrder(jobParam);
    }

    /**
     * 确认订单
     * @throws Exception
     */
    @XxlJob(value = "confirmOrder")
    public void confirmOrder() throws Exception {
        String jobParam = XxlJobHelper.getJobParam();
        cryptoOrderService.confirmOrder(jobParam);
    }
}
