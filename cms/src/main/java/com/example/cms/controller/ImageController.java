// src/main/java/com/example/cms/controller/ImageController.java
package com.example.cms.controller;

import com.example.cms.entity.Image;
import com.example.cms.service.ImageService;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
  private final ImageService imageService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Image upload(@RequestPart("image") MultipartFile file) throws Exception {
    return imageService.storeImage(file);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','GUEST')")
  public ResponseEntity<Resource> getImage(@PathVariable Long id) throws Exception {
    Image img = imageService.getImageById(id);
    PathResource resource = new PathResource(Paths.get(img.getFilepath()));
    MediaType type = MediaTypeFactory.getMediaType(img.getFilename())
        .orElse(MediaType.APPLICATION_OCTET_STREAM);
    return ResponseEntity.ok()
        .contentType(type)
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + img.getFilename() + "\"")
        .body(resource);
  }
}
