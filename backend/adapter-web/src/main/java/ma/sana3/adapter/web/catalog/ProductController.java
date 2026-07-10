package ma.sana3.adapter.web.catalog;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.catalog.CreateProductCommand;
import ma.sana3.application.catalog.CreateProductHandler;
import ma.sana3.application.catalog.DeleteProductCommand;
import ma.sana3.application.catalog.DeleteProductHandler;
import ma.sana3.application.catalog.ListOwnProductsHandler;
import ma.sana3.application.catalog.ListOwnProductsQuery;
import ma.sana3.application.catalog.ProductResult;
import ma.sana3.application.catalog.UpdateProductCommand;
import ma.sana3.application.catalog.UpdateProductHandler;
import ma.sana3.domain.user.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/artisan-profiles/me/products")
class ProductController {

  private static final String ROLE_PREFIX = "ROLE_";

  private final CreateProductHandler createProductHandler;
  private final UpdateProductHandler updateProductHandler;
  private final DeleteProductHandler deleteProductHandler;
  private final ListOwnProductsHandler listOwnProductsHandler;

  ProductController(
      CreateProductHandler createProductHandler,
      UpdateProductHandler updateProductHandler,
      DeleteProductHandler deleteProductHandler,
      ListOwnProductsHandler listOwnProductsHandler) {
    this.createProductHandler = createProductHandler;
    this.updateProductHandler = updateProductHandler;
    this.deleteProductHandler = deleteProductHandler;
    this.listOwnProductsHandler = listOwnProductsHandler;
  }

  @PostMapping
  ResponseEntity<ProductResponse> create(
      @AuthenticationPrincipal UUID userId,
      Authentication authentication,
      @Valid @RequestBody UpsertProductRequest request) {
    ProductResult result =
        createProductHandler.handle(
            new CreateProductCommand(
                userId,
                roleOf(authentication),
                request.name(),
                request.description(),
                request.priceAmount(),
                request.priceCurrency(),
                request.craftType(),
                request.imageUrl()));
    return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(result));
  }

  @PutMapping("/{id}")
  ProductResponse update(
      @AuthenticationPrincipal UUID userId,
      Authentication authentication,
      @PathVariable UUID id,
      @Valid @RequestBody UpsertProductRequest request) {
    ProductResult result =
        updateProductHandler.handle(
            new UpdateProductCommand(
                userId,
                roleOf(authentication),
                id,
                request.name(),
                request.description(),
                request.priceAmount(),
                request.priceCurrency(),
                request.craftType(),
                request.imageUrl()));
    return ProductResponse.from(result);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(
      @AuthenticationPrincipal UUID userId, Authentication authentication, @PathVariable UUID id) {
    deleteProductHandler.handle(new DeleteProductCommand(userId, roleOf(authentication), id));
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  List<ProductResponse> list(@AuthenticationPrincipal UUID userId, Authentication authentication) {
    return listOwnProductsHandler
        .handle(new ListOwnProductsQuery(userId, roleOf(authentication)))
        .stream()
        .map(ProductResponse::from)
        .toList();
  }

  private static Role roleOf(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .filter(authority -> authority.startsWith(ROLE_PREFIX))
        .map(authority -> Role.valueOf(authority.substring(ROLE_PREFIX.length())))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Authenticated request missing a role authority"));
  }
}
