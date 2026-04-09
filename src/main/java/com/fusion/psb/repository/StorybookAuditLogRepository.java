package com.fusion.psb.repository;

import com.fusion.psb.entity.StorybookAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorybookAuditLogRepository extends JpaRepository<StorybookAuditLog, Long> {
}
