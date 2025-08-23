package com.jasmin.housingaffordability;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry r) {
    r.addMapping("/**")
     // allows local frontend
     .allowedOrigins("http://localhost:5173", "https://housingdata.netlify.app")
     // only GET for your public API
     .allowedMethods("GET", "HEAD", "OPTIONS")
     // no cookies/auth needed, so keep credentials off
     .allowCredentials(false)
     // allow common headers
     .allowedHeaders("Content-Type", "Accept")
     // cache preflight for a bit
     .maxAge(3600);
  }
}
