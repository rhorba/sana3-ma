package ma.sana3.adapter.web.catalog;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.catalog.ImageStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class LocalDiskImageStorage implements ImageStorage {

  private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE =
      Map.of(
          "image/jpeg", "jpg",
          "image/png", "png",
          "image/webp", "webp");

  private final Path uploadDir;

  LocalDiskImageStorage(@Value("${app.upload.dir}") String uploadDir) {
    this.uploadDir = Path.of(uploadDir);
    try {
      Files.createDirectories(this.uploadDir);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not create upload directory: " + this.uploadDir, e);
    }
  }

  @Override
  public String store(byte[] content, String contentType) {
    String extension = EXTENSIONS_BY_CONTENT_TYPE.get(contentType);
    if (extension == null) {
      throw new IllegalArgumentException("Unsupported content type: " + contentType);
    }
    String filename = UUID.randomUUID() + "." + extension;
    try {
      Files.write(uploadDir.resolve(filename), content);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not store image: " + filename, e);
    }
    return filename;
  }

  @Override
  public void delete(String storageKey) {
    resolveSafely(storageKey)
        .ifPresent(
            path -> {
              try {
                Files.deleteIfExists(path);
              } catch (IOException e) {
                throw new UncheckedIOException("Could not delete image: " + storageKey, e);
              }
            });
  }

  Optional<Path> findExisting(String storageKey) {
    return resolveSafely(storageKey).filter(Files::exists);
  }

  private Optional<Path> resolveSafely(String storageKey) {
    Path resolved = uploadDir.resolve(storageKey).normalize();
    return resolved.startsWith(uploadDir) ? Optional.of(resolved) : Optional.empty();
  }
}
