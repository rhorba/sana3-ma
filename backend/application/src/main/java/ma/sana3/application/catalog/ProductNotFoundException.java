package ma.sana3.application.catalog;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException() {
    super("No product found for this id");
  }
}
