package org.cyberpay.crypto.utils;

import com.alibaba.fastjson2.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program:
 * @description: 发送http请求工具类
 */
public class HttpClientUtil {


	public static int connectionTimeout = 300000;

	public static int timeout = 300000;
	public static String charset = "UTF-8";
	private static final String APPLICATION_JSON = "application/json";

	/**
	 * httpclient使用步骤 1、创建一个HttpClient对象;
	 * 2、创建一个Http请求对象并设置请求的URL，比如GET请求就创建一个HttpGet对象，POST请求就创建一个HttpPost对象;
	 * 3、如果需要可以设置请求对象的请求头参数，也可以往请求对象中添加请求参数; 4、调用HttpClient对象的execute方法执行请求;
	 * 5、获取请求响应对象和响应Entity; 6、从响应对象中获取响应状态，从响应Entity中获取响应内容; 7、关闭响应对象;
	 * 8、关闭HttpClient.
	 */
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	private static RequestConfig requestConfig = RequestConfig.custom()
			// 从连接池中获取连接的超时时间
			// 要用连接时尝试从连接池中获取，若是在等待了一定的时间后还没有获取到可用连接（比如连接池中没有空闲连接了）则会抛出获取连接超时异常。
			.setConnectionRequestTimeout(15000)
			// 与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间
			// 连接目标url的连接超时时间，即客服端发送请求到与目标url建立起连接的最大时间。超时时间3000ms过后，系统报出异常
			.setConnectTimeout(100000)
			// socket读数据超时时间：从服务器获取响应数据的超时时间
			// 连接上一个url后，获取response的返回等待时间
			// ，即在与目标url建立连接后，等待放回response的最大时间，在规定时间内没有返回响应的话就抛出SocketTimeout。
			.setSocketTimeout(100000).build();

	/**
	 * 发送http请求
	 *
	 * @param requestMethod 请求方式（HttpGet、HttpPost、HttpPut、HttpDelete）
	 * @param url           请求路径
	 * @param params        post请求参数
	 * @param header        请求头
	 * @return 响应文本
	 */
	public static String sendHttp(HttpRequestMethod.HttpRequestMethodEnum requestMethod, String url, Map<String, Object> params,
                                  Map<String, String> header) {
		// 1、创建一个HttpClient对象;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		String responseContent = null;
		// 2、创建一个Http请求对象并设置请求的URL，比如GET请求就创建一个HttpGet对象，POST请求就创建一个HttpPost对象;
		HttpRequestBase request = requestMethod.createRequest(url);
		request.setConfig(requestConfig);
		// 3、如果需要可以设置请求对象的请求头参数，也可以往请求对象中添加请求参数;
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				request.setHeader(entry.getKey(), entry.getValue());
			}
		}
		// 往对象中添加相关参数
		try {
			if (params != null) {
				((HttpEntityEnclosingRequest) request).setEntity(
						new StringEntity(JSON.toJSONString(params), ContentType.create("application/json", "UTF-8")));
			}
			// 4、调用HttpClient对象的execute方法执行请求;
			httpResponse = httpClient.execute(request);
			// 5、获取请求响应对象和响应Entity;
			HttpEntity httpEntity = httpResponse.getEntity();
			// 6、从响应对象中获取响应状态，从响应Entity中获取响应内容;
			if (httpEntity != null) {
				responseContent = EntityUtils.toString(httpEntity, "UTF-8");
				logger.info("Http请求响应结果:{}",responseContent);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Http请求异常:{}",e.getMessage());
			return null;
		} finally {
			try {
				// 7、关闭响应对象;
				if (httpResponse != null) {
					httpResponse.close();
				}
				// 8、关闭HttpClient.
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.info("Http请求异常:{}",e.getMessage());
				return null;
			}
		}
		return responseContent;
	}

	public static String postWithParamsForString(String url, Map<String,String> paramsMap,Map<String,String> headerMap){
		HttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		String result = "";
		try {
			List<NameValuePair> params = new ArrayList<>();
			if(null !=paramsMap){
				for(String key:paramsMap.keySet()){
					params.add(new BasicNameValuePair(key,paramsMap.get(key)));
				}
			}

			if(null != headerMap && headerMap.size() >0){
				for(String key:headerMap.keySet()){
					httpPost.setHeader(key, headerMap.get(key));
				}
			}
			httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			HttpResponse response = client.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();

			if(statusCode == 200){
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);
				logger.info("Http请求响应结果:{}",result);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Http请求异常:{}",e.getMessage());
		}
		return result;
	}

	/**
	 * 发送 json 请求
	 *
	 * @param url
	 * @param str
	 * @return
	 */
	@SuppressWarnings({"deprecation", "resource", "finally"})
	public static String postJson(String url, String str, Map<String,String> heades) {
		logger.info("URL={},req={}", url, str);
		long start = System.currentTimeMillis();
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
		HttpPost post = new HttpPost(url);
		StringBuffer ret = new StringBuffer();
		try {
			StringEntity sentity = new StringEntity(str, "UTF-8");
			sentity.setContentEncoding(charset);
			sentity.setContentType(APPLICATION_JSON);
			post.setEntity(sentity);

			//处理头信息
			if(null != heades && heades.size() >0){
				for(String key:heades.keySet()){
					post.setHeader(key,heades.get(key));
				}
			}

			HttpResponse res = client.execute(post);

			int statusCode = res.getStatusLine().getStatusCode();
			logger.debug("visit url :{}, request status : {}", url.toString(), statusCode);

			HttpEntity entity = res.getEntity();
			String returnCharSet = EntityUtils.getContentCharSet(entity);
			if (StringUtils.isEmpty(returnCharSet)) {
				returnCharSet = "UTF-8";
			}
			InputStreamReader read = new InputStreamReader(entity.getContent(), returnCharSet);
			BufferedReader bufr = new BufferedReader(read);// 缓冲
			String line = null;
			while ((line = bufr.readLine()) != null) {
				ret.append(line);
			}
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
			long end = System.currentTimeMillis();
			logger.info("Cost:{},URL={},results={}", end - start, url, e.getMessage());
			throw new RuntimeException(e);
		} finally {
			long end = System.currentTimeMillis();
			logger.info("Cost:{},URL={},req={},results={}", end - start, url, str, ret.toString());
			return ret.toString();
		}
	}
}
