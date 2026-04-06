package com.fusion.psb.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StoryBookAppConfig implements WebMvcConfigurer {

  @Value("${chat.model}")
  private String chatModel;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ChatClient chatClient(GoogleGenAiChatModel genAiChatModel) {
    return ChatClient.create(genAiChatModel);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**") // Allow all endpoints
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
        .allowedOriginPatterns("http://localhost:4200")
        .allowedOrigins("https://magictale.netlify.app")
        .allowedHeaders("*") // Allow all headers
        .allowCredentials(true); // Allow cookies
  }
}
