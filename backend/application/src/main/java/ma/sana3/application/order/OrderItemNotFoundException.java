package ma.sana3.application.order;

public class OrderItemNotFoundException extends RuntimeException {

  public OrderItemNotFoundException() {
    super("No order item found for this id");
  }
}
