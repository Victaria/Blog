package com.victory.blog.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // registry.addViewController("/login").setViewName("authorisation/login");
        registry.addViewController("/registerProcess").setViewName("info/sentToEmail");
        registry.addViewController("/blog/articles").setViewName("articles/main");
        registry.addViewController("/blog/articles/{post_id}/comments").setViewName("articles/comments");
        registry.addViewController("/blog/my").setViewName("afterAuth/profile");
        registry.addViewController("/blog/articles").setViewName("afterAuth/main");
    }

    @Bean
    public ViewResolver viewResolver(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources/templates");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
