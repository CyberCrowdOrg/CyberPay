package org.cyberpay.trade.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 
 * @Title: ID工具类
 */
@Component
public class IDUtils {

	private static Snowflake snowflake = IdUtil.getSnowflake(1, 1);

	private static IDUtils idUtils;

	@PostConstruct
	private void init() {
		idUtils = this;
	}

	/**
	 * Twitter的Snowflake 算法
	 * 
	 * @return
	 */
	public static String getIdBySnowflake() {
		return snowflake.nextIdStr();
	}

	/**
	 * 交易流水号
	 * @return
	 */
	public static String generateTradeFlowNo(){
		String idBySnowflake = getIdBySnowflake();
		idBySnowflake = idBySnowflake.subSequence(0, idBySnowflake.length()-1).toString();
		return "T" + idBySnowflake;
	}

}
