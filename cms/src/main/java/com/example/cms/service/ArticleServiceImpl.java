package com.example.cms.service;

import com.example.cms.entity.Article;
import com.example.cms.entity.Image;
import com.example.cms.repository.ArticleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArticleServiceImpl implements ArticleService {

  @Autowired
  private ArticleRepository articleRepository;

  @Autowired
  private ImageService imageService;

  @Override
  public Article createArticle(String title, String content, MultipartFile imageFile) throws Exception {
    Image savedImage = imageService.saveImage(imageFile);

    Article article = new Article();
    article.setTitle(title);
    article.setContent(content);
    article.setImage(savedImage);
    return articleRepository.save(article);
  }

  @Override
  public List<Article> getAllArticles() {
    return articleRepository.findAll();
  }

  @Override
  public Article getArticleById(Long id) throws Exception {
    return articleRepository.findById(id)
        .orElseThrow(() -> new Exception("Article not found with id: " + id));
  }

  @Override
  public Article updateArticle(Long id, String title, String content, MultipartFile imageFile) throws Exception {
    Article existing = getArticleById(id);

    existing.setTitle(title);
    existing.setContent(content);

    if (imageFile != null && !imageFile.isEmpty()) {
      // Delete old image
      Image oldImage = existing.getImage();
      if (oldImage != null) {
        imageService.deleteImage(oldImage);
      }
      // Save new image
      Image newImage = imageService.saveImage(imageFile);
      existing.setImage(newImage);
    }

    return articleRepository.save(existing);
  }

  @Override
  public void deleteArticle(Long id) throws Exception {
    Article existing = getArticleById(id);
    Image image = existing.getImage();
    if (image != null) {
      imageService.deleteImage(image);
    }
    articleRepository.delete(existing);
  }
}
