package com.example.cms.service;

import com.example.cms.entity.Image;
import com.example.cms.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

  private final Path rootLocation = Paths.get("src/main/resources/static/images");

  @Autowired
  private ImageRepository imageRepository;

  @Override
  public Image saveImage(MultipartFile file) throws Exception {
    // Validate file type
    String filename = StringUtils.cleanPath(file.getOriginalFilename());
    if (filename.contains("..")) {
      throw new Exception("Invalid file path: " + filename);
    }
    String ext = StringUtils.getFilenameExtension(filename);
    if (!(ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))) {
      throw new Exception("Only JPG/PNG images are allowed");
    }

    // Generate unique filename
    String uniqueFilename = UUID.randomUUID().toString() + "." + ext;
    Files.createDirectories(rootLocation);

    Path destinationFile = rootLocation.resolve(uniqueFilename).normalize();
    try {
      Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new Exception("Failed to store file: " + e.getMessage());
    }

    // Save metadata in DB
    Image image = new Image();
    image.setFilename(uniqueFilename);
    image.setFilepath("/images/" + uniqueFilename);
    image.setUploadedAt(Instant.now());
    return imageRepository.save(image);
  }

  @Override
  public Image getImageById(Long id) throws Exception {
    return imageRepository.findById(id)
        .orElseThrow(() -> new Exception("Image not found with id: " + id));
  }

  @Override
  public void deleteImage(Image image) throws Exception {
    // Delete file from filesystem
    Path filePath = rootLocation.resolve(image.getFilename()).normalize();
    try {
      Files.deleteIfExists(filePath);
    } catch (IOException e) {
      throw new Exception("Failed to delete file: " + e.getMessage());
    }
    imageRepository.delete(image);
  }
}
