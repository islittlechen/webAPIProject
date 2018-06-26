package org.cxj.webapi.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;


public class SignUtil {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SignUtil.class);
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
		"e", "f" };
	private static final String SIGN_KEY = "kihxzuil";
	
	public static String makeVeriSignJson(JSONObject params) {
		Iterator<String> iter = params.keySet().iterator();
		List<String> keys = new ArrayList<String>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value;
			try {
				value = params.get(key);
				if (!(value instanceof JSONObject || value instanceof JSONArray)) {
					keys.add(key);
				}
			} catch (JSONException e) {
				LOGGER.error(e.getMessage());
			}
		}

		Collections.sort(keys);
		StringBuilder prestr = new StringBuilder();
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = "";
			Object ob;
			try {
				ob = params.get(key);
			} catch (JSONException e) {
				LOGGER.error(e.getMessage());
				continue;
			}
			if (ob != null) {
				value = ob.toString();
			}

			if (i == keys.size() - 1) {
				prestr.append(key).append("=").append(value);
			} else {
				prestr.append(key).append("=").append(value).append("&");
			}
		}
		LOGGER.info("request params:"+prestr);
		return getMD5(prestr.append("&").append(SIGN_KEY)
				.toString());
	}
	
	public static String GetMD5Code(byte[] bByte) {
		String resultString = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			resultString = byteToString(md.digest(bByte));

		} catch (NoSuchAlgorithmException ex) {
			LOGGER.error("GetMD5Code error!",ex);
		}
		return resultString;
	}
	
	private static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}
	
	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		// System.out.println("iRet="+iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}
	
	public static String getMD5(String src) {
		MessageDigest digester = null;
		try {
			digester = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("getMD5 error NoSuchAlgorithmException!", e);
		}
		byte[] srcBytes = null;
		try {
			srcBytes = src.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			LOGGER.error("getMD5 error UnsupportedEncodingException!", e);
		}
		digester.update(srcBytes);
		byte[] digest = digester.digest();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < digest.length; i++) {
			StringBuilder tem = new StringBuilder(
					Integer.toHexString(0xFF & digest[i]));
			sb.append(tem.length() > 1 ? tem : tem.insert(0, "0"));
		}

		return sb.toString().toLowerCase();
	}

}
