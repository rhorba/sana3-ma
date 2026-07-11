package ma.sana3.domain.order;

public class IllegalOrderStatusTransitionException extends RuntimeException {

  public IllegalOrderStatusTransitionException(OrderStatus from, OrderStatus to) {
    super("Cannot transition order from " + from + " to " + to);
  }
}
