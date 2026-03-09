package com.fusion.psb.controller;

import com.fusion.psb.dto.StorybookRequest;
import com.fusion.psb.service.StorybookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/storybook")
public class StorybookController {

  @Autowired
  private StorybookService storybookService;

  @PostMapping("/generate")
  public ResponseEntity<byte[]> generateStorybook(@RequestBody StorybookRequest request) {
    try {
      byte[] pdfData = storybookService.generateStorybook(request);

      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Type", "application/pdf");

      return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
