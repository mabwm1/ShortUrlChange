package cn.bdqn.shorturlchange.config;

import cn.bdqn.shorturlchange.interceptor.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Description: 配置CORS跨域支持、拦截器
 * @Author: Naccl
 * @Date: 2023-06-16
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    AccessLimitInterceptor accessLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);
    }
}
