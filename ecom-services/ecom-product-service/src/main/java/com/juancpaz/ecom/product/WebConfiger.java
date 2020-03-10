package com.juancpaz.ecom.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SuppressWarnings("deprecation")
@Component
public class WebConfiger extends WebMvcConfigurerAdapter {

	@Value("${ecom.product.img.location}")
	private String imgLocation;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/productImg/**").addResourceLocations("file:////" + imgLocation);
	}

}
