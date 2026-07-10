package ma.sana3.domain.catalog;

public interface ImageStorage {

  String store(byte[] content, String contentType);

  void delete(String storageKey);
}
