package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.QrCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {
    @Query("SELECT a FROM QrCode a WHERE a.status <> 0 ORDER BY a.createdDate DESC")
    Page<QrCode> findAllByStatus(Integer status, Pageable pageable);

    @Query("SELECT a FROM QrCode a WHERE a.status <> 0 AND a.qrCode = :qrCode ORDER BY a.createdDate DESC")
    Optional<QrCode> findQRCodeByQrCode(String qrCode);

    @Query("SELECT a FROM QrCode a WHERE a.status = :status AND a.accountId = :accountId ORDER BY a.createdDate DESC")
    Page<QrCode> findAllByStatusAndAccountId(Integer status, UUID accountId, Pageable pageable);

    @Query("SELECT a FROM QrCode a WHERE a.accountId = :accountId AND a.status <> 0 ORDER BY a.createdDate DESC")
    Page<QrCode> findAllByAccountId(UUID accountId, Pageable pageable);
}
