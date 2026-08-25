package com.example.ordermanagementsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileStorageService {

    // File tối đa 5 MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // Thư mục lưu file
    private final Path uploadDir = Paths.get("uploads/products");

    // Các đuôi file cho phép
    private final Set<String> allowedExtensions = Set.of(
            ".jpg",
            ".jpeg",
            ".png",
            ".webp"
    );

    // Các loại file cho phép
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(FileStorageService.class);

    // Tạo thư mục nếu chưa có
    public FileStorageService() throws IOException {
        Files.createDirectories(uploadDir);
    }

    // Lưu file upload vào thư mục
    public String saveFile(MultipartFile file) throws IOException {

        // Kiểm tra file có rỗng không
        if (file.isEmpty()) {
            log.warn("Upload failed: file is empty");
            throw new IllegalArgumentException("File is empty");
        }

        // Kiểm tra file có vượt quá 5 MB không
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("File too large: {} bytes", file.getSize());
            throw new IllegalArgumentException("File too large");
        }

        // Lấy loại file
        String contentType = file.getContentType();

        // Kiểm tra loại file có được phép không
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.warn("File type is not allowed: {}", contentType);
            throw new IllegalArgumentException("File type is not allowed");
        }

        // Lấy tên file gốc
        String originalFileName = file.getOriginalFilename();

        // Kiểm tra tên file
        if (originalFileName == null || originalFileName.isBlank()) {
            log.warn("Upload failed: file name is invalid");
            throw new IllegalArgumentException("File name is invalid");
        }

        // Tìm dấu "." cuối cùng
        int dotIndex = originalFileName.lastIndexOf(".");

        // File phải có đuôi
        if (dotIndex < 0) {
            log.warn("Upload failed: file has no extension");
            throw new IllegalArgumentException("File phải có đuôi hợp lệ");
        }

        // Lấy đuôi file và chuyển thành chữ thường
        String extension = originalFileName
                .substring(dotIndex)
                .toLowerCase();

        // Kiểm tra đuôi file có được phép không
        if (!allowedExtensions.contains(extension)) {
            log.warn("File extension is not allowed: {}", extension);
            throw new IllegalArgumentException(
                    "File type is not allowed"
            );
        }

        // Tạo tên file mới bằng UUID
        String fileName = UUID.randomUUID() + extension;

        // Tạo đường dẫn file cần lưu
        Path targetPath = uploadDir.resolve(fileName);

        // Copy file vào thư mục
        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        // Ghi log khi upload thành công
        log.info("File uploaded successfully: {}", fileName);

        // Trả về đường dẫn để lưu vào database
        return "/uploads/products/" + fileName;
    }
}