package ma.sana3.adapter.web.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProductImageControllerTest {

  @TempDir Path tempDir;

  private MockMvc mockMvc;
  private LocalDiskImageStorage imageStorage;

  @BeforeEach
  void setUp() {
    imageStorage = new LocalDiskImageStorage(tempDir.toString());
    mockMvc = MockMvcBuilders.standaloneSetup(new ProductImageController(imageStorage)).build();
  }

  @Test
  void servesAnExistingImage() throws Exception {
    String key = imageStorage.store("fake-image-bytes".getBytes(), "image/jpeg");

    mockMvc.perform(get("/api/v1/products/images/" + key)).andExpect(status().isOk());
  }

  @Test
  void returnsNotFoundForUnknownFile() throws Exception {
    mockMvc
        .perform(get("/api/v1/products/images/does-not-exist.jpg"))
        .andExpect(status().isNotFound());
  }

  @Test
  void returnsNotFoundForPathTraversalAttempt() throws Exception {
    mockMvc
        .perform(get("/api/v1/products/images/..%2f..%2fetc%2fpasswd"))
        .andExpect(status().isNotFound());
  }
}
