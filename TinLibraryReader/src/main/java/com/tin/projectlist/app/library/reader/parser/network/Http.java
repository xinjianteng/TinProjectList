package com.core.network;

import com.core.log.L;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 请求服务器端数据工具类
 *
 * @author 韩少杰
 * @date 2011-07-14
 * @修改者： 符晨
 * @日期： 2012-5-7
 * @修改者：符晨
 * @日期： 2012-11-28
 * @修改者：符晨
 * @日期： 2012-12-5
 */
public class Http {

	/**
	 * 编码
	 */
	public static String encode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 封装请求返回数据的实体
	 */
	public static class Entry {
		public boolean success;
		public int code;// FIXME int
		public JSONObject data;

		public String getMessage() {
			return "" + code;// FIXME 根据code得到String的值
		}
	}

	/**
	 * 需要用户校验的请求
	 */
	public static Entry postWithAccount(String path, String[] keys,
										Object[] values) {

		Map<String, Object> params = new HashMap<String, Object>();
		if (keys != null && values != null) {
			for (int i = 0; i < keys.length; i++) {
				params.put(keys[i], values[i]);
			}
		}
		/*
		 * params.put("accountId", Account.Instance().getAccountId());
		 * params.put("email", Account.Instance().getEmail());
		 * params.put("password", Account.Instance().getPassword());
		 * params.put("terminalType", GBConfig.TERMINAL_TYPE);
		 */

		return postAsEntry(path, params);// FIXME 自己实现参数封装，可以优化性能，减少map对象的使用
	}

	/**
	 * 需要用户校验的请求
	 */
	public static Entry postWithAccount(String path) {
		Map<String, Object> params = new HashMap<String, Object>();
		/*
		 * params.put("accountId", Account.Instance().getAccountId());
		 * params.put("email", Account.Instance().getEmail());
		 * params.put("password", Account.Instance().getPassword());
		 * params.put("terminalType", GBConfig.TERMINAL_TYPE);
		 */
		return postAsEntry(path, params);// FIXME 自己实现参数封装，可以优化性能，减少map对象的使用
	}

	/**
	 * 发送带参请求，简便方法
	 */
	public static Entry postAsEntry(String path, String[] keys, Object[] values) {
		if (keys == null || keys.length == 0 || values == null
				|| values.length == 0) {
			return postAsEntry(path);
		}

		Map<String, Object> params = new HashMap<String, Object>();
		for (int i = 0; i < keys.length; i++) {
			params.put(keys[i], values[i]);
		}
		return postAsEntry(path, params);// FIXME 自己实现参数封装，可以优化性能，减少map对象的使用
	}

	/**
	 * 发送无参请求
	 */
	public static Entry postAsEntry(String path) {
		return postAsEntry(path, null);
	}

	/**
	 * 发送请求
	 */
	public static Entry postAsEntry(String path, Map<String, Object> params) {
		String res = post(path, params);
		if (res == null || "".equals(res)) {
			Entry entry = new Entry();
			entry.code = -1;
			return entry;
		}

		try {
			JSONObject jsonObject = new JSONObject(res);
			int flag = jsonObject.getInt("flag");
			if (flag == 1) {
				Entry entry = new Entry();
				entry.success = true;
				if (!jsonObject.isNull("message")) {
					entry.code = jsonObject.getInt("message");
				}
				if (!jsonObject.isNull("data")) {
					entry.data = jsonObject.getJSONObject("data");
				}
				return entry;
			} else {
				Entry entry = new Entry();
				if (!jsonObject.isNull("message")) {
					entry.code = jsonObject.getInt("message");
				}
				return entry;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Entry entry = new Entry();
			entry.code = -1;
			return entry;
		}
	}

	/**
	 * 此请求已过气，请使用postAsEntry方法
	 */
	@Deprecated
	public static String post(String path) {
		return post(path, null);
	}

	/**
	 * 此请求已过气，请使用postAsEntry方法
	 */
	@Deprecated
	public static String post(String path, Map<String, Object> map) {
		L.i("HTTP", "post: url=" + path + " params= " + map);
		long currentTimeMillis = System.currentTimeMillis();

		String returnStr = "";
		// 清空流信息
		HttpPost httpPost = new HttpPost(path);
		HttpResponse response = null;
		// 封装参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (map != null) {
			Set<String> keys = map.keySet();
			Iterator<String> iter = keys.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				params.add(new BasicNameValuePair(key,
						map.get(key) == null ? "" : map.get(key).toString()));
			}
		}

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			response = HttpManager.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				returnStr = convertString(response.getEntity().getContent());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (httpPost != null)
				httpPost.abort();
		}

		L.i("HTTP", "time:" + (System.currentTimeMillis() - currentTimeMillis));
		L.i("HTTP", "response:" + returnStr);
		return returnStr;
	}

	/**
	 * 将返回的数据流转换成字符串
	 *
	 * @param in
	 *            返回的数据流
	 * @return 获得的字符串
	 */
	public static String convertString(InputStream in) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"GBK"), 512 * 1024);
			try {
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String returnStr = sb.toString();

		if (returnStr != null && !returnStr.equals("")) {
			returnStr = returnStr.replaceAll("\\\\\"", "\\\"").substring(1);
			returnStr = returnStr.replaceAll("\\\\\\\\\"", "\\\\\\\"");
			returnStr = returnStr.substring(0, returnStr.length() - 1);
		}

		return returnStr;
	}

	public static String execPost(String url, List<NameValuePair> params) throws ClientProtocolException, IOException {// URLEncoder.encode(url,"utf-8");
		String resultStr = null;
		HttpPost httpPost = new HttpPost(url);
		DefaultHttpClient httpClient = null;

		if (params != null) {
			httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
		}
		// location.httpPost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE,
		// false);
		// location.httpPost.addHeader("Content-Type",
		// "application/x-www-form-urlencoded");
		httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpPost);
		if (response != null) {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				resultStr = EntityUtils.toString(response.getEntity());
				// location.resultStr=EntityUtils.toString(location.response.getEntity());
			}
		} else {
			L.d("http", "服务器无响应");
		}

		httpClient.getConnectionManager().shutdown();

		return resultStr;
	}

	public static String get(String url) throws Exception {
		HttpParams params = null;
		return get(url, params);
	}

	public static String get(String url, HttpParams params) throws IOException,Exception {
		String result = "";// 杩斿洖缁撴灉
		DefaultHttpClient httpClient;
		HttpGet httpGet = new HttpGet(url);
		if (params != null) {
			httpGet.setParams(params);
		}
		httpClient = new DefaultHttpClient();
		HttpResponse response;

		response = httpClient.execute(httpGet);
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
			}
		}

		httpClient.getConnectionManager().shutdown();
		return result;
	}
}
