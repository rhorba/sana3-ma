package ma.sana3.application.catalog;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException() {
    super("No product found for this id");
  }

  public ProductNotFoundException(UUID productId) {
    super("No product found for id " + productId);
  }
}
