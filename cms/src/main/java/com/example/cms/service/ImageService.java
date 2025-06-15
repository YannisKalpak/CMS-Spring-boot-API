package com.example.cms.service;

import com.example.cms.entity.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
  Image storeImage(MultipartFile file) throws Exception;

  Image getImageById(Long id) throws Exception;

  void deleteImage(Image image) throws Exception;
}
