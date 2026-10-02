package com.magedalaa.optlink.repository;

import com.magedalaa.optlink.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    List<AccessLog> findByCompanyIdOrderByAccessedAtDesc(Long companyId);

    List<AccessLog> findByConsumerIdOrderByAccessedAtDesc(Long consumerId);
}
