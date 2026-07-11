package ma.sana3.application.order;

public class OrderHasCompletedItemsException extends RuntimeException {

  public OrderHasCompletedItemsException() {
    super("Cannot cancel an order that already has a fulfilled item");
  }
}
