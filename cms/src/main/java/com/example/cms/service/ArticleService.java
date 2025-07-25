// src/main/java/com/example/cms/service/ArticleService.java
package com.example.cms.service;

import com.example.cms.dto.ArticleRequest;
import com.example.cms.entity.Article;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {
  Article createArticle(ArticleRequest data, MultipartFile imageFile) throws Exception;

  List<Article> getAllArticles();

  Article getArticleById(Long id) throws Exception;

  Article updateArticle(Long id, ArticleRequest data, MultipartFile imageFile) throws Exception;

  void deleteArticle(Long id) throws Exception;
}
