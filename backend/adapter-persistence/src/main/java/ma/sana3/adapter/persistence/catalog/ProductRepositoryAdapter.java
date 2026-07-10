package ma.sana3.adapter.persistence.catalog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.catalog.ProductSearchCriteria;
import ma.sana3.domain.catalog.ProductSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

  @Override
  public ProductSearchResult search(ProductSearchCriteria criteria) {
    Page<ProductJpaEntity> page =
        springDataProductRepository.search(
            lowercase(criteria.craftType()),
            lowercase(criteria.region()),
            criteria.minPrice(),
            criteria.maxPrice(),
            lowercase(criteria.keyword()),
            PageRequest.of(criteria.page(), criteria.pageSize()));
    List<Product> products = page.getContent().stream().map(ProductEntityMapper::toDomain).toList();
    return new ProductSearchResult(
        products, page.getTotalElements(), criteria.page(), criteria.pageSize());
  }

  private static String lowercase(String value) {
    return value == null ? null : value.toLowerCase();
  }
}
