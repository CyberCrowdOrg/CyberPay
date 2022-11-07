package org.cyberpay.crypto.utils;


import com.alibaba.fastjson2.JSON;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @program: transferwallet-parent
 * @description: 将对象参数转为json并编码
 */
public class HttpURLEncoder {
    private static final String CHARSET = "utf-8";

    public static String encode(Object object){
        String jsonStr = JSON.toJSONString(object);
        String param = null;
        try {
            param = URLEncoder.encode(jsonStr, CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param;
    }
}
