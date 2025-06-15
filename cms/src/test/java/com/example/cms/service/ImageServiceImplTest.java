package com.example.cms.service;

import com.example.cms.entity.Image;
import com.example.cms.repository.ImageRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageServiceImpl imageService;

    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        // Create temp directory for files
        tempDir = Files.createTempDirectory("images");
        // Inject rootLocation via reflection
        ReflectionTestUtils.setField(imageService, "rootLocation", tempDir);
        var rootField = ImageServiceImpl.class.getDeclaredField("rootLocation");
        rootField.setAccessible(true);
        rootField.set(imageService, tempDir);

    }

    @Test
    void testStoreImage_Success() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("photo.png");
        byte[] content = "data".getBytes();
        when(file.getBytes()).thenReturn(content);

        Image saved = new Image();
        when(imageRepository.save(any(Image.class))).thenReturn(saved);

        Image result = imageService.storeImage(file);
        assertNotNull(result);
        Path stored = tempDir.resolve(result.getFilename());
        assertTrue(Files.exists(stored));
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void testDeleteImage_Success() throws Exception {
        // Create a temp file
        Path file = tempDir.resolve("old.png");
        Files.write(file, "x".getBytes());
        Image img = new Image();
        img.setFilename("old.png");

        // No need to stub, deleteImage handles repository.delete internally
        imageService.deleteImage(img);
        assertFalse(Files.exists(file));
        verify(imageRepository).delete(img);
    }

    @Test
    void testDeleteImage_FileNotExists() throws Exception {
        Image img = new Image();
        img.setFilename("missing.png");
        // Call deleteImage without actual file
        imageService.deleteImage(img);
        // Still calls repository.delete
        verify(imageRepository).delete(img);
    }
}
