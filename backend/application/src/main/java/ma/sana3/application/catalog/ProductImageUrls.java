package ma.sana3.application.catalog;

final class ProductImageUrls {

  private static final String PREFIX = "/api/v1/products/images/";

  private ProductImageUrls() {}

  static String toUrl(String storageKey) {
    return PREFIX + storageKey;
  }

  static String toStorageKey(String url) {
    return url == null ? null : url.substring(PREFIX.length());
  }
}
