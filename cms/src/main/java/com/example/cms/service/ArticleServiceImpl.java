// src/main/java/com/example/cms/service/ArticleServiceImpl.java
package com.example.cms.service;

import com.example.cms.dto.ArticleRequest;
import com.example.cms.entity.Article;
import com.example.cms.entity.Image;
import com.example.cms.repository.ArticleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
  private final ArticleRepository articleRepository;
  private final ImageService imageService;

  @Override
  public Article createArticle(ArticleRequest data, MultipartFile imageFile) throws Exception {
    Article a = new Article();
    a.setTitle(data.getTitle());
    a.setContent(data.getContent());
    if (imageFile != null && !imageFile.isEmpty()) {
      Image img = imageService.storeImage(imageFile);
      a.setImage(img);
    }
    return articleRepository.save(a);
  }

  @Override
  public List<Article> getAllArticles() {
    return articleRepository.findAll();
  }

  @Override
  public Article getArticleById(Long id) throws Exception {
    return articleRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Article not found"));
  }

  @Override
  public Article updateArticle(Long id, ArticleRequest data, MultipartFile imageFile) throws Exception {
    Article existing = getArticleById(id);
    existing.setTitle(data.getTitle());
    existing.setContent(data.getContent());
    if (imageFile != null && !imageFile.isEmpty()) {
      // delete old
      if (existing.getImage() != null) {
        imageService.deleteImage(existing.getImage());
      }
      Image newImg = imageService.storeImage(imageFile);
      existing.setImage(newImg);
    }
    existing.setUpdatedAt(java.time.Instant.now());
    return articleRepository.save(existing);
  }

  @Override
  public void deleteArticle(Long id) throws Exception {
    Article existing = getArticleById(id);
    if (existing.getImage() != null) {
      imageService.deleteImage(existing.getImage());
    }
    articleRepository.delete(existing);
  }
}
