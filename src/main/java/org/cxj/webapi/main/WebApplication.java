package org.cxj.webapi.main;


import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.cxj.webapi.interceptor.SessionInterceptor;
import org.cxj.webapi.interceptor.SignatureInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 180,redisNamespace = "API:SESSION")
@ComponentScan(basePackages = {"org.cxj.webapi"})
public class WebApplication  implements WebMvcConfigurer {

    public static void main(String[] args){
        SpringApplication.run(WebApplication.class,args);
    }


//    @Bean
//    public LettuceConnectionFactory connectionFactory() {
//        return new LettuceConnectionFactory();
//    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new SignatureInterceptor()).addPathPatterns("/webapi/**");
        registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/webapi/work/**");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //1.需要定义一个convert转换消息的对象;
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        //3处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        //4.在convert中添加配置信息.
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        //5.将convert添加到converters当中.
        converters.add(fastJsonHttpMessageConverter);
    }

    @Bean
    public CookieHttpSessionIdResolver cookieHttpSessionIdResolver(){
        CookieHttpSessionIdResolver idResolver = new CookieHttpSessionIdResolver();
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        //DefaultCookieSerializer 默认会将后台生产的sessionID进行base64编码后再传给前端，如果快系统session共享，可以考虑让其不进行base64编码
        cookieSerializer.setUseBase64Encoding(false);
        idResolver.setCookieSerializer(cookieSerializer);
        return idResolver;
    }
}
