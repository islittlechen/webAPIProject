package org.cxj.webapi.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.catalina.connector.InputBuffer;
import org.apache.commons.io.IOUtils;
import org.cxj.webapi.common.Constants;
import org.cxj.webapi.util.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Enumeration;

public class SignatureInterceptor implements HandlerInterceptor {
	
	private static final Logger LOG = LoggerFactory.getLogger(SignatureInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		JSONObject signParam = new JSONObject();
		String sign = null;
		String contentType = request.getContentType();
		if(MediaType.APPLICATION_JSON_UTF8_VALUE.equals(contentType) ||
				MediaType.APPLICATION_JSON_VALUE.equals(contentType)){
			InputStream in = request.getInputStream();
			StringBuilder builder = new StringBuilder();
			int k ;
			while((k=in.read())!= -1){
				builder.append((char)k);
			}
			signParam = JSONObject.parseObject(builder.toString());
			sign = signParam.getString("sign");
			signParam.remove("sign");
		}else{
			Enumeration<String> names = request.getParameterNames();
			while(names.hasMoreElements()) {
				String name = names.nextElement();
				String value = new String(request.getParameter(name).getBytes("iso-8859-1"),"utf-8");
				if(name.equals("sign")){
					sign = value;
				}else{
					signParam.put(name,value);
				}

			}

		}
		String bsign = SignUtil.makeVeriSignJson(signParam);
		if(sign == null){
			LOG.warn("签名字段不存在，验证不通过");
			response.sendError(401);
			return false;
		}
		if(!sign.equals(bsign)){
			LOG.warn("签名不正确，验证不通过");
			response.sendError(401);
			return false;
		}
		request.setAttribute(Constants.REQ_PARAMS,signParam);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
