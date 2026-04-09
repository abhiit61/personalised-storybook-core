package com.fusion.psb.controller;

import com.fusion.psb.dto.StorybookRequest;
import com.fusion.psb.entity.StorybookAuditLog;
import com.fusion.psb.repository.StorybookAuditLogRepository;
import com.fusion.psb.service.StorybookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storybook")
public class StorybookController {

  @Autowired
  private StorybookService storybookService;

  @Autowired
  private StorybookAuditLogRepository auditLogRepository;

  @Value("${admin.audit.password}")
  private String adminPassword;

  @Value("${admin.audit.allowed-ip}")
  private String allowedIp;

  @PostMapping("/generate")
  public ResponseEntity<byte[]> generateStorybook(@RequestBody StorybookRequest request) throws Exception {
      byte[] pdfData = storybookService.generateStorybook(request);
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Type", "application/pdf");
      return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
  }

  @GetMapping("/admin/audit-logs")
  public ResponseEntity<?> getAuditLogs(
      @RequestHeader("X-Admin-Password") String password,
      HttpServletRequest httpRequest) {

    String remoteAddr = httpRequest.getRemoteAddr();
    if (!"all".equalsIgnoreCase(allowedIp) && !allowedIp.equals(remoteAddr)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: IP "+remoteAddr+" not allowed");
    }

    if (!adminPassword.equals(password)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
    }

    List<StorybookAuditLog> logs = auditLogRepository.findAll();
    return ResponseEntity.ok(logs);
  }
}
