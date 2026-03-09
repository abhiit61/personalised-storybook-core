package com.fusion.psb.service;

import com.fusion.psb.dto.StorybookRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class StorybookService {

  public byte[] generateStorybook(StorybookRequest request) throws Exception {

    // Create prompt for AI
    String prompt = String.format(
        "Create a personalized storybook for a %d-year-old %s named %s with body tone %s. " +
            "The story is set in %s during %s. The theme is %s, mood is %s, and the companion is %s. " +
            "Include moral attributes: %s.",
        request.getAge(), request.getGender(), request.getName(), request.getBodyTone(),
        request.getLocation(), request.getEvent(), request.getTheme(), request.getMood(),
        request.getCompanion(), request.getMoralAttributes()
    );

    // Call AI service (placeholder)
    String storyContent = callAIService(prompt);
    String imageUrl = "https://example.com/image.jpg"; // Placeholder for AI-generated image URL

    // Generate PDF
    return createPDF(request.getName(), storyContent, imageUrl);
  }

  private String callAIService(String prompt) {
    // Placeholder for AI service integration
    return "Once upon a time, in a magical land...";
  }

  private byte[] createPDF(String name, String content, String imageUrl) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document();
    PdfWriter.getInstance(document, outputStream);

    document.open();
    document.add(new Paragraph("Personalized Storybook for " + name));
    document.add(new Paragraph(content));
    document.add(Image.getInstance(imageUrl)); // Add image (URL must be accessible)
    document.close();

    return outputStream.toByteArray();
  }
}
