package com.victory.Blog.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
       // registry.addViewController("/login").setViewName("authorisation/login");
        registry.addViewController("/registerProcess").setViewName("info/sentToEmail");
        registry.addViewController("/blog/articles").setViewName("articles/main");
        registry.addViewController("/blog/articles/{post_id}/comments").setViewName("articles/comments");
    }
}
