package com.example.cms.controller;

import com.example.cms.entity.Image;
import com.example.cms.service.ImageService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

  @Autowired
  private ImageService imageService;

  @PostMapping
  public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
    try {
      Image savedImage = imageService.saveImage(file);
      return new ResponseEntity<>(savedImage, HttpStatus.CREATED);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getImage(@PathVariable Long id) {
    try {
      Image image = imageService.getImageById(id);
      // Serve static resource from classpath
      Resource resource = new ClassPathResource("static" + image.getFilepath());
      if (!resource.exists()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
      }
      Path path = Paths.get(resource.getURI());
      String contentType = Files.probeContentType(path);

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFilename() + "\"")
          .body(resource);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }
}
