package com.fusion.psb.service;

import com.fusion.psb.config.StorybookConstants;
import com.fusion.psb.dto.StorybookRequest;
import com.fusion.psb.entity.StorybookAuditLog;
import com.fusion.psb.repository.StorybookAuditLogRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StorybookService {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StorybookService.class);

  private final RestTemplate restTemplate;
  private final ChatClient chatClient;
  private final StorybookAuditLogRepository auditLogRepository;

  @Autowired
  public StorybookService(RestTemplate restTemplate, ChatClient chatClient,
      StorybookAuditLogRepository auditLogRepository) {
    this.restTemplate = restTemplate;
    this.chatClient = chatClient;
    this.auditLogRepository = auditLogRepository;
  }

  public byte[] generateStorybook(StorybookRequest request) throws Exception {
    // Create userPrompt for AI
    String sysPromp = "You are a story creator who creates story as per the inputs given by user. Generate story for two pages. Short ones. "
        + "Use low level vocabulary so person in india can understand. Add images in form of text, so in next iteration we can use it to generate image."
        + "Generate content based on the age.";

    String userPrompt = String.format(
        "Create a personalized storybook for a %d-year-old %s named %s with body tone %s. " +
            "The story is set in %s during %s. The theme is %s, mood is %s, and the companion is %s. " +
            "Include moral attributes: %s. ",
        request.getAge(), request.getGender(), request.getName(), request.getBodyTone(),
        request.getLocation(), request.getEvent(), request.getTheme(), request.getMood(),
        request.getCompanion(), request.getMoralAttributes()
    );

    StorybookAuditLog auditLog = buildAuditLog(request);

    String storyContent;
    try {
      storyContent = callGeminiApi(userPrompt, sysPromp);
      auditLog.setAiResponse(storyContent);
      auditLog.setSuccess(true);
    } catch (Exception e) {
      auditLog.setSuccess(false);
      auditLog.setErrorMessage(e.getMessage());
      auditLogRepository.save(auditLog);
      throw e;
    }

    auditLogRepository.save(auditLog);

    String imageUrl = "https://example.com/image.jpg"; // Placeholder for AI-generated image URL
    return createPDF(request.getName(), storyContent, imageUrl);
  }

  private StorybookAuditLog buildAuditLog(StorybookRequest request) {
    StorybookAuditLog log = new StorybookAuditLog();
    log.setName(request.getName());
    log.setGender(request.getGender());
    log.setAge(request.getAge());
    log.setBodyTone(request.getBodyTone());
    log.setLocation(request.getLocation());
    log.setEvent(request.getEvent());
    log.setTheme(request.getTheme());
    log.setMood(request.getMood());
    log.setCompanion(request.getCompanion());
    log.setMoralAttributes(request.getMoralAttributes());
    log.setRequestTimestamp(LocalDateTime.now());
    return log;
  }

  private String callGeminiApi(String userPrompt, String systemPromp) {
    try {
      return chatClient.prompt()
          .system(systemPromp)
          .user(userPrompt)
          .call()
          .content();
    } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
      // Handle 4xx errors (e.g., invalid credentials, bad request)
      LOGGER.error("Error : ", e);
      throw new RuntimeException("Error: "+ e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Error : ", e);
      throw new RuntimeException("Error: "+ e.getMessage());
    }
  }

  private byte[] createPDF(String name, String content, String imageUrl) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document();
    try {
      PdfWriter.getInstance(document, outputStream);
      document.open();
      document.add(new Paragraph("Personalized Storybook for " + name));
      document.add(new Paragraph(content));
      // Uncomment the following line if the image URL is accessible and valid
      // document.add(Image.getInstance(imageUrl));
    } catch (Exception e) {
      LOGGER.error("Error while creating PDF: ", e);
      throw new RuntimeException("Failed to generate the PDF. Please try again later.");
    } finally {
      if (document.isOpen()) {
        document.close();
      }
    }
    return outputStream.toByteArray();
  }

}
