package ma.sana3.domain.catalog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

  Product save(Product product);

  Optional<Product> findById(UUID id);

  List<Product> findByArtisanProfileId(UUID artisanProfileId);

  void deleteById(UUID id);

  ProductSearchResult search(ProductSearchCriteria criteria);
}
