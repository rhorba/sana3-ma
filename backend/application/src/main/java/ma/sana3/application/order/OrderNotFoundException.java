package ma.sana3.application.order;

public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException() {
    super("No order found for this id");
  }
}
