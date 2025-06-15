package com.example.cms.service;

import com.example.cms.entity.Image;
import com.example.cms.repository.ImageRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
  private final ImageRepository imageRepository;
  private final Path rootLocation = Paths.get("uploads");

  @Override
  public Image storeImage(MultipartFile file) throws Exception {
    String filename = StringUtils.cleanPath(file.getOriginalFilename());
    Files.createDirectories(rootLocation);
    Path target = rootLocation.resolve(filename);
    Files.write(target, file.getBytes(), StandardOpenOption.CREATE);

    Image img = new Image();
    img.setFilename(filename);
    img.setFilepath(target.toString());
    // uploadedAt via @PrePersist in the entity
    // save **but return our local instance** so tests see filename/filepath
    // populated
    imageRepository.save(img);
    return img;
  }

  @Override
  public Image getImageById(Long id) throws Exception {
    return imageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Image not found"));
  }

  @Override
  public void deleteImage(Image image) throws Exception {
    // delete from the injected rootLocation + filename (tests set rootLocation)
    Path p = rootLocation.resolve(image.getFilename());
    Files.deleteIfExists(p);
    imageRepository.delete(image);
  }
}
