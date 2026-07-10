package ma.sana3.adapter.persistence.catalog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import org.springframework.stereotype.Repository;

@Repository
class ProductRepositoryAdapter implements ProductRepository {

  private final SpringDataProductRepository springDataProductRepository;

  ProductRepositoryAdapter(SpringDataProductRepository springDataProductRepository) {
    this.springDataProductRepository = springDataProductRepository;
  }

  @Override
  public Product save(Product product) {
    ProductJpaEntity saved =
        springDataProductRepository.save(ProductEntityMapper.toEntity(product));
    return ProductEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<Product> findById(UUID id) {
    return springDataProductRepository.findById(id).map(ProductEntityMapper::toDomain);
  }

  @Override
  public List<Product> findByArtisanProfileId(UUID artisanProfileId) {
    return springDataProductRepository.findByArtisanProfileId(artisanProfileId).stream()
        .map(ProductEntityMapper::toDomain)
        .toList();
  }

  @Override
  public void deleteById(UUID id) {
    springDataProductRepository.deleteById(id);
  }
}
