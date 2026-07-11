package ma.sana3.domain.order;

public class OrderItemAlreadyCompletedException extends RuntimeException {

  public OrderItemAlreadyCompletedException() {
    super("Order item is already completed");
  }
}
