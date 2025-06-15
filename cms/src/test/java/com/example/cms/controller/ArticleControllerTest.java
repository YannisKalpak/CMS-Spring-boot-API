package com.example.cms.controller;

import com.example.cms.dto.ArticleRequest;
import com.example.cms.entity.Article;
import com.example.cms.entity.Image;
import com.example.cms.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ArticleService articleService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ArticleController articleController;

    private Article sampleArticle;
    private ArticleRequest articleRequest;
    private MockMultipartFile imageFile;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(articleController).build();

        sampleArticle = new Article();
        sampleArticle.setId(1L);
        sampleArticle.setTitle("Test Article");
        sampleArticle.setContent("Test Content");

        Image image = new Image();
        image.setFilename("test.jpg");
        sampleArticle.setImage(image);

        articleRequest = new ArticleRequest();
        articleRequest.setTitle("Test Title");
        articleRequest.setContent("Test Content");

        imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image".getBytes());

        lenient().when(objectMapper.readValue(anyString(), eq(ArticleRequest.class)))
                .thenReturn(articleRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createArticle_Success() throws Exception {
        when(articleService.createArticle(any(), any())).thenReturn(sampleArticle);

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"title\":\"Test\",\"content\":\"Content\"}".getBytes());

        mockMvc.perform(multipart("/api/articles")
                .file(data)
                .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Article"));
    }

    @Test
    void listArticles_Success() throws Exception {
        List<Article> articles = Arrays.asList(sampleArticle);
        when(articleService.getAllArticles()).thenReturn(articles);

        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Article"));
    }

    @Test
    void getArticle_Success() throws Exception {
        when(articleService.getArticleById(1L)).thenReturn(sampleArticle);

        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getArticle_NotFound() throws Exception {
        when(articleService.getArticleById(1L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateArticle_Success() throws Exception {
        when(articleService.updateArticle(anyLong(), any(), any())).thenReturn(sampleArticle);

        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"title\":\"Updated\",\"content\":\"New Content\"}".getBytes());

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/articles/1")
                .file(data)
                .file(imageFile)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Article"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteArticle_Success() throws Exception {
        doNothing().when(articleService).deleteArticle(1L);

        mockMvc.perform(delete("/api/articles/1"))
                .andExpect(status().isOk());
    }
}