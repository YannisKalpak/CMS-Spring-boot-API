package com.example.cms.controller;

import com.example.cms.entity.Image;
import com.example.cms.service.ImageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {
    private MockMvc mockMvc;
    private Path tempDir;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    private Image sampleImage;

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();

        // Create temp directory
        tempDir = Files.createTempDirectory("image-test");

        // Create real temp file
        Path tempFile = tempDir.resolve("test.jpg");
        Files.write(tempFile, "test content".getBytes());

        sampleImage = new Image();
        sampleImage.setId(1L);
        sampleImage.setFilename("test.jpg");
        sampleImage.setFilepath(tempFile.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temp files
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadImage_Success() throws Exception {
        when(imageService.storeImage(any())).thenReturn(sampleImage);

        MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test".getBytes());

        mockMvc.perform(multipart("/api/images").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("test.jpg"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "GUEST" })
    void getImage_Success() throws Exception {
        when(imageService.getImageById(1L)).thenReturn(sampleImage);

        mockMvc.perform(get("/api/images/1"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"test.jpg\""))
                .andExpect(content().bytes("test content".getBytes()));
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "GUEST" })
    void getImage_NotFound() throws Exception {
        when(imageService.getImageById(1L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/images/1"))
                .andExpect(status().isNotFound());
    }
}