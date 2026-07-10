package ma.sana3.adapter.web.catalog;

import java.nio.file.Path;
import java.util.Optional;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/images")
class ProductImageController {

  private final LocalDiskImageStorage imageStorage;

  ProductImageController(LocalDiskImageStorage imageStorage) {
    this.imageStorage = imageStorage;
  }

  @GetMapping("/{filename}")
  ResponseEntity<Resource> serve(@PathVariable String filename) {
    Optional<Path> path = imageStorage.findExisting(filename);
    if (path.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    MediaType contentType =
        MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM);
    return ResponseEntity.ok().contentType(contentType).body(new FileSystemResource(path.get()));
  }
}
