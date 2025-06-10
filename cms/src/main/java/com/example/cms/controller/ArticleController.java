package com.example.cms.controller;

import com.example.cms.dto.ArticleRequest;
import com.example.cms.entity.Article;
import com.example.cms.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

  @Autowired
  private ArticleService articleService;

  @PostMapping
  public ResponseEntity<?> createArticle(
      @Valid @RequestPart("data") ArticleRequest articleRequest,
      @RequestPart("image") MultipartFile imageFile) {
    try {
      Article article = articleService.createArticle(
          articleRequest.getTitle(),
          articleRequest.getContent(),
          imageFile
      );
      return new ResponseEntity<>(article, HttpStatus.CREATED);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<List<Article>> getAllArticles() {
    List<Article> articles = articleService.getAllArticles();
    return ResponseEntity.ok(articles);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getArticleById(@PathVariable Long id) {
    try {
      Article article = articleService.getArticleById(id);
      return ResponseEntity.ok(article);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateArticle(
      @PathVariable Long id,
      @Valid @RequestPart("data") ArticleRequest articleRequest,
      @RequestPart(value = "image", required = false) MultipartFile imageFile) {
    try {
      Article updated = articleService.updateArticle(
          id,
          articleRequest.getTitle(),
          articleRequest.getContent(),
          imageFile
      );
      return ResponseEntity.ok(updated);
    } catch (Exception e) {
      if (e.getMessage().contains("not found")) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
    try {
      articleService.deleteArticle(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }
}
