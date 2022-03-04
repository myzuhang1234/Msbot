package com.badeling.msbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //获取文件的真实路径
//        String path = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\upload\\";
		String path = MsbotConst.imageUrl + "/";
        //static/upload/**是对应resource下工程目录
        registry.addResourceHandler("/image/**").addResourceLocations("file:"+path);
     }
}
