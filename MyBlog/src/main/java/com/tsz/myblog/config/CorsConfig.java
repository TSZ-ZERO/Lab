package com.tsz.myblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        config.addAllowedOriginPattern("*");  // 允许所有来源
        config.addAllowedMethod("*");          // 允许所有 HTTP 方法
        config.addAllowedHeader("*");          // 允许所有请求头
        config.setAllowCredentials(true);      // 允许携带凭证
        config.setMaxAge(3600L);               // 预检请求缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 应用到所有路径

        return new CorsFilter(source);
    }
}
