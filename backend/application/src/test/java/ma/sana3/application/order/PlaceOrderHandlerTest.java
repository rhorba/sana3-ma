package ma.sana3.application.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import ma.sana3.domain.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceOrderHandlerTest {

  @Mock private OrderRepository orderRepository;
  @Mock private OrderItemRepository orderItemRepository;
  @Mock private ProductRepository productRepository;

  private PlaceOrderHandler handler;

  @BeforeEach
  void setUp() {
    handler = new PlaceOrderHandler(orderRepository, orderItemRepository, productRepository);
  }

  @Test
  void placesOrderWithSnapshotAndComputesTotal() {
    UUID buyerUserId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    Product product =
        Product.create(
            artisanProfileId,
            "Zellige Tile Set",
            "Handmade",
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            null);
    when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(orderItemRepository.save(any(OrderItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PlaceOrderCommand command =
        new PlaceOrderCommand(
            buyerUserId, "123 Rue Example, Fes", List.of(new PlaceOrderLineItem(product.id(), 2)));

    OrderResult result = handler.handle(command);

    assertEquals(buyerUserId, result.buyerUserId());
    assertEquals(OrderStatus.PLACED, result.status());
    assertEquals(1, result.items().size());
    assertEquals(new BigDecimal("900.00"), result.items().get(0).lineTotal());
    assertEquals(1, result.totals().size());
    assertEquals("MAD", result.totals().get(0).currency());
    assertEquals(new BigDecimal("900.00"), result.totals().get(0).amount());
  }

  @Test
  void computesSeparateTotalsPerCurrency() {
    UUID buyerUserId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    Product madProduct =
        Product.create(
            artisanProfileId, "Tile", null, new BigDecimal("100.00"), "MAD", "Pottery", null);
    Product usdProduct =
        Product.create(
            artisanProfileId, "Rug", null, new BigDecimal("20.00"), "USD", "Weaving", null);
    when(productRepository.findById(madProduct.id())).thenReturn(Optional.of(madProduct));
    when(productRepository.findById(usdProduct.id())).thenReturn(Optional.of(usdProduct));
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(orderItemRepository.save(any(OrderItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PlaceOrderCommand command =
        new PlaceOrderCommand(
            buyerUserId,
            "Address",
            List.of(
                new PlaceOrderLineItem(madProduct.id(), 1),
                new PlaceOrderLineItem(usdProduct.id(), 1)));

    OrderResult result = handler.handle(command);

    assertEquals(2, result.totals().size());
  }

  @Test
  void rejectsALineReferencingAMissingProduct() {
    UUID buyerUserId = UUID.randomUUID();
    UUID missingProductId = UUID.randomUUID();
    when(productRepository.findById(missingProductId)).thenReturn(Optional.empty());
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PlaceOrderCommand command =
        new PlaceOrderCommand(
            buyerUserId, "Address", List.of(new PlaceOrderLineItem(missingProductId, 1)));

    assertThrows(ProductNotFoundException.class, () -> handler.handle(command));

    verify(orderItemRepository, never()).save(any());
  }
}
