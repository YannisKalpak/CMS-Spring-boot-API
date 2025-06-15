package com.example.cms.controller;

import com.example.cms.dto.ArticleRequest;
import com.example.cms.entity.Article;
import com.example.cms.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

  private final ArticleService articleService;
  private final ObjectMapper objectMapper;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Article> createArticle(
      @RequestPart("data") String dataJson,
      @RequestPart("image") MultipartFile image) throws Exception {
    // manually parse the JSON part
    ArticleRequest request = objectMapper.readValue(dataJson, ArticleRequest.class);
    Article created = articleService.createArticle(request, image);
    return ResponseEntity.ok(created);
  }

  @GetMapping
  public ResponseEntity<List<Article>> listArticles() {
    return ResponseEntity.ok(articleService.getAllArticles());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Article> getArticle(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(articleService.getArticleById(id));
    } catch (Exception e) {
      // You can customize the error response as needed
      return ResponseEntity.internalServerError().build();
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping(path = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Article> updateArticle(
      @PathVariable Long id,
      @RequestPart("data") String dataJson,
      @RequestPart("image") MultipartFile image) throws Exception {
    ArticleRequest request = objectMapper.readValue(dataJson, ArticleRequest.class);
    Article updated = articleService.updateArticle(id, request, image);
    return ResponseEntity.ok(updated);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
    try {
      articleService.deleteArticle(id);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      // You can customize the error response as needed
      return ResponseEntity.internalServerError().build();
    }
  }
}
