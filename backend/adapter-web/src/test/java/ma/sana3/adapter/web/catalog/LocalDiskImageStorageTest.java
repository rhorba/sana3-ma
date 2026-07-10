package ma.sana3.adapter.web.catalog;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalDiskImageStorageTest {

  @TempDir Path tempDir;

  @Test
  void storesAndFindsAFile() {
    LocalDiskImageStorage storage = new LocalDiskImageStorage(tempDir.toString());

    String key = storage.store("content".getBytes(), "image/jpeg");

    assertTrue(key.endsWith(".jpg"));
    Optional<Path> found = storage.findExisting(key);
    assertTrue(found.isPresent());
  }

  @Test
  void rejectsUnsupportedContentType() {
    LocalDiskImageStorage storage = new LocalDiskImageStorage(tempDir.toString());

    assertThrows(
        IllegalArgumentException.class, () -> storage.store("content".getBytes(), "text/plain"));
  }

  @Test
  void deleteRemovesTheFile() {
    LocalDiskImageStorage storage = new LocalDiskImageStorage(tempDir.toString());
    String key = storage.store("content".getBytes(), "image/png");

    storage.delete(key);

    assertTrue(storage.findExisting(key).isEmpty());
  }

  @Test
  void findExistingReturnsEmptyForPathTraversalAttempt() {
    LocalDiskImageStorage storage = new LocalDiskImageStorage(tempDir.toString());

    assertTrue(storage.findExisting("../../etc/passwd").isEmpty());
  }

  @Test
  void deleteIsANoOpForPathTraversalAttempt() throws IOException {
    Path outsideFile = Files.createTempFile("sana3-outside-", ".txt");
    try {
      LocalDiskImageStorage storage = new LocalDiskImageStorage(tempDir.toString());

      storage.delete("../" + outsideFile.getFileName());

      assertTrue(Files.exists(outsideFile), "file outside the upload dir must not be deleted");
    } finally {
      Files.deleteIfExists(outsideFile);
    }
  }
}
