package com.example.cms.service;

import com.example.cms.dto.ArticleRequest;
import com.example.cms.entity.Article;
import com.example.cms.entity.Image;
import com.example.cms.repository.ArticleRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private Article sampleArticle;
    // private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        sampleArticle = new Article();
        sampleArticle.setId(1L);
        sampleArticle.setTitle("Title");
        sampleArticle.setContent("Content");

        // mockFile = mock(MultipartFile.class);
    }

    @Test
    void testGetAllArticles() {
        when(articleRepository.findAll()).thenReturn(Arrays.asList(sampleArticle));
        List<Article> articles = articleService.getAllArticles();
        assertEquals(1, articles.size());
        verify(articleRepository).findAll();
    }

    @Test
    void testGetArticleById_Found() throws Exception {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(sampleArticle));
        Article result = articleService.getArticleById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetArticleById_NotFound() {
        when(articleRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(Exception.class, () -> articleService.getArticleById(2L));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testCreateArticle() throws Exception {
        ArticleRequest req = new ArticleRequest();
        req.setTitle("New");
        req.setContent("Data");

        when(articleRepository.save(any(Article.class))).thenReturn(sampleArticle);

        Article created = articleService.createArticle(req, null);
        assertNotNull(created);
        verify(articleRepository).save(any(Article.class));
    }

    @Test
    void testUpdateArticle() throws Exception {
        ArticleRequest req = new ArticleRequest();
        req.setTitle("Updated");
        req.setContent("New Content");

        when(articleRepository.findById(1L)).thenReturn(Optional.of(sampleArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(sampleArticle);

        Article updated = articleService.updateArticle(1L, req, null);
        assertEquals("Updated", updated.getTitle());
        assertEquals("New Content", updated.getContent());
        verify(articleRepository).save(sampleArticle);
    }

    @Test
    void testDeleteArticle_NoImage() throws Exception {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(sampleArticle));
        articleService.deleteArticle(1L);
        verify(articleRepository).delete(sampleArticle);
        verify(imageService, never()).deleteImage(any(Image.class));
    }

    @Test
    void testDeleteArticle_WithImage() throws Exception {
        Image img = new Image();
        img.setFilename("test.png");
        sampleArticle.setImage(img);
        when(articleRepository.findById(1L)).thenReturn(Optional.of(sampleArticle));

        doNothing().when(imageService).deleteImage(img);
        articleService.deleteArticle(1L);

        verify(imageService).deleteImage(img);
        verify(articleRepository).delete(sampleArticle);
    }
}
