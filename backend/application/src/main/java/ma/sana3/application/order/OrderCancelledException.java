package ma.sana3.application.order;

public class OrderCancelledException extends RuntimeException {

  public OrderCancelledException() {
    super("Cannot complete an item on a cancelled order");
  }
}
