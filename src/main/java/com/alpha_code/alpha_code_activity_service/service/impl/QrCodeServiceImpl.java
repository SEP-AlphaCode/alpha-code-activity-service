package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.ActivityDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.dto.QrCodeDto;
import com.alpha_code.alpha_code_activity_service.entity.QrCode;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.mapper.QrCodeMapper;
import com.alpha_code.alpha_code_activity_service.repository.QrCodeRepository;
import com.alpha_code.alpha_code_activity_service.service.ActivityService;
import com.alpha_code.alpha_code_activity_service.service.QrCodeService;
import com.alpha_code.alpha_code_activity_service.service.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeServiceImpl implements QrCodeService {

    private final QrCodeRepository repository;
    private final S3Service s3Service;
    private final ActivityService activityService;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "qr_codes_list", key = "{#page, #size, #status}")
    public PagedResult<QrCodeDto> getAll(int page, int size, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<QrCode> pageResult;

        if (status != null) {
            pageResult = repository.findAllByStatus(status, pageable);
        } else {
            pageResult = repository.findAll(pageable);
        }
        return new PagedResult<>(pageResult.map(QrCodeMapper::toDto));
    }

    @Override
    @Cacheable(value = "qr_codes", key = "#id")
    public QrCodeDto getById(UUID id) {
        var qrCode = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QRCode not found"));
        return QrCodeMapper.toDto(qrCode);
    }

    @Override
    public ActivityDto getByQrImage(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File ảnh không được null hoặc rỗng");
            }

            // Đọc ảnh từ MultipartFile
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                throw new IllegalArgumentException("File ảnh không hợp lệ hoặc không thể đọc");
            }

            // Chuyển BufferedImage sang ảnh grayscale GrayU8 của BoofCV
            GrayU8 gray = ConvertBufferedImage.convertFrom(bufferedImage, (GrayU8) null);

            // Tạo QR code detector
            QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode(null, GrayU8.class);

            // Phát hiện QR code
            detector.process(gray);

            List<boofcv.alg.fiducial.qrcode.QrCode> detections = detector.getDetections();

            if (detections.isEmpty()) {
                throw new ResourceNotFoundException("Không tìm thấy QR code trong ảnh (mờ, nghiêng, background...)");
            }

            // Lấy QR code đầu tiên
            String decodedText = detections.get(0).message;
            if (decodedText == null || decodedText.isBlank()) {
                throw new IllegalArgumentException("Không tìm thấy nội dung QR code trong ảnh");
            }

            log.info("Decoded QR code text: {}", decodedText);

            // Lấy thông tin từ DB
            QrCodeDto qrCodeDto = getByCode(decodedText);
            if (qrCodeDto == null) throw new ResourceNotFoundException("QR code không tồn tại");

            ActivityDto activityDto = activityService.getActivityById(qrCodeDto.getActivityId());
            if (activityDto == null) throw new ResourceNotFoundException("Activity không tồn tại");

            return activityDto;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file ảnh QR code", e);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi decode QR code", e);
        }
    }



    @Override
    @Transactional
    @CacheEvict(value = {"qr_codes_list", "qr_codes"}, allEntries = true)
    public QrCodeDto create(QrCodeDto qrCodeDto) {
        if (qrCodeDto == null || qrCodeDto.getQrCode() == null) {
            throw new IllegalArgumentException("QRCodeDto và các trường không được null");
        }

        if (qrCodeDto.getAccountId() == null) {
            throw new IllegalArgumentException("AccountId không được null");
        }

        if (qrCodeDto.getActivityId() == null) {
            throw new IllegalArgumentException("ActivityId không được null");
        }

        if (repository.findQRCodeByQrCode(qrCodeDto.getQrCode()).isPresent()) {
            throw new IllegalArgumentException("QRCode với mã này đã tồn tại");
        }

        try {
            QrCode entity = QrCodeMapper.toEntity(qrCodeDto);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setStatus(qrCodeDto.getStatus() != null ? qrCodeDto.getStatus() : 1);

            // Tạo QR code và upload S3
            String fileName = "qr_" + entity.getQrCode() + "_" + System.currentTimeMillis() + ".png";
            String imageUrl = generateAndUploadQRCode(entity.getQrCode(), fileName);
            entity.setImageUrl(imageUrl);

            QrCode saved = repository.save(entity);
            return QrCodeMapper.toDto(saved);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Lỗi khi tạo hoặc tải QR code", e);
        }
    }


//    @Override
//    public QRCodeDto update(UUID id, QRCodeDto qrCodeDto) throws JsonProcessingException {
//        var existed = repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("QRCode not found"));
//
//        existed.setQrCode(qrCodeDto.getCode());
//
//        if (qrCodeDto.getData() != null) {
//            existed.set(objectMapper.writeValueAsString(qrCodeDto.getData()));
//        }
//
//        existed.setLastEdited(LocalDateTime.now());
//
//        QRCode savedEntity = repository.save(existed);
//        return QRCodeMapper.toDto(savedEntity);
//    }

    @Override
    @Transactional
    @CacheEvict(value = {"qr_codes_list"}, allEntries = true)
    @CachePut(value = "qr_codes", key = "#id")
    public QrCodeDto update(UUID id, QrCodeDto qrCodeDto) {
        var existed = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QRCode not found"));

        existed.setName(qrCodeDto.getName());
        existed.setColor(qrCodeDto.getColor());
        existed.setQrCode(qrCodeDto.getQrCode());
        existed.setStatus(qrCodeDto.getStatus());
        existed.setImageUrl(qrCodeDto.getImageUrl());
        if (qrCodeDto.getActivityId() != null) {
            existed.setActivityId(qrCodeDto.getActivityId());
        }
        if (qrCodeDto.getAccountId() != null) {
            existed.setAccountId(qrCodeDto.getAccountId());
        }

        existed.setLastUpdated(LocalDateTime.now());

        QrCode savedEntity = repository.save(existed);
        return QrCodeMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"qr_codes_list"}, allEntries = true)
    @CachePut(value = "qr_codes", key = "#id")
    public QrCodeDto patchUpdate(UUID id, QrCodeDto qrCodeDto) {
        QrCode existed = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QRCode not found with id " + id));

        boolean regenerateImage = false;

        if (qrCodeDto.getQrCode() != null) {
            existed.setQrCode(qrCodeDto.getQrCode());
        }
        if (qrCodeDto.getColor() != null) {
            existed.setColor(qrCodeDto.getColor());
        }
        if (qrCodeDto.getName() != null) {
            existed.setName(qrCodeDto.getName());
        }
        if (qrCodeDto.getQrCode() != null && !qrCodeDto.getQrCode().equals(existed.getQrCode())) {
            existed.setQrCode(qrCodeDto.getQrCode());
            regenerateImage = true;
        }
        if (qrCodeDto.getAccountId() != null) {
            existed.setAccountId(qrCodeDto.getAccountId());
        }
        if (qrCodeDto.getActivityId() != null) {
            existed.setActivityId(qrCodeDto.getActivityId());
        }

        // regenerate image nếu cần
        if (regenerateImage) {
            String fileName = "qr_" + existed.getQrCode() + "_" + System.currentTimeMillis() + ".png";
            try {
                String imageUrl = generateAndUploadQRCode(existed.getQrCode(), fileName);
                if (imageUrl == null) {
                    throw new RuntimeException("Không tạo được QR image");
                }
                existed.setImageUrl(imageUrl);
            } catch (WriterException | IOException e) {
                throw new RuntimeException("Lỗi khi tạo lại QRCode image", e);
            }
        }

        existed.setLastUpdated(LocalDateTime.now());

        return QrCodeMapper.toDto(repository.save(existed));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"qr_codes_list", "qr_codes"}, key = "#id", allEntries = true)
    public String delete(UUID id) {
        try {
            var existed = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("QRCode not found"));
//        repository.deleteById(id);
            existed.setStatus(0);
            existed.setLastUpdated(LocalDateTime.now());
            repository.save(existed);
            return "Deleted QRCode with ID: " + id;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting QRCode", e);
        }

    }

    @Override
    @Transactional
    @CacheEvict(value = {"qr_codes_list", "qr_codes"}, key = "#id", allEntries = true)
    public String disable(UUID id) {
        try {
            var existed = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("QRCode not found"));
//        repository.deleteById(id);
            existed.setStatus(2);
            existed.setLastUpdated(LocalDateTime.now());
            repository.save(existed);
            return "Disable QRCode with ID: " + id;
        } catch (Exception e) {
            throw new RuntimeException("Error disable QRCode", e);
        }

    }

    @Override
    @Cacheable(value = "qr_codes", key = "#code")
    public QrCodeDto getByCode(String code) {
        return repository.findQRCodeByQrCode(code)
                .map(QrCodeMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("QRCode not found"));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"qr_codes_list"}, allEntries = true)
    @CachePut(value = "qr_codes", key = "#id")
    public QrCodeDto changeStatus(UUID id, Integer status) {
        var existed = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QRCode not found"));
        if (status != null) {
            existed.setStatus(status);
        } else {
            throw new IllegalArgumentException("Status cannot be null");
        }
        existed.setLastUpdated(LocalDateTime.now());
        QrCode savedEntity = repository.save(existed);
        return QrCodeMapper.toDto(savedEntity);
    }

    private String generateAndUploadQRCode(String text, String fileName) throws WriterException, IOException {
        int width = 300;
        int height = 300;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        // Lưu vào memory thay vì file local
        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            // Upload lên S3 và trả về URL
            return s3Service.uploadBytes(pngData, "qrcodes/" + fileName, "image/png");
        }
    }


}
