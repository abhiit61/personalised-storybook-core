package com.fusion.psb.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class StoryBookAppConfig {

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

}
