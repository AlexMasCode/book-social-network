package com.ukma.stats.service.download.records;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadRecordRepository extends JpaRepository<DownloadRecord, Long>, JpaSpecificationExecutor<DownloadRecord> {
}
