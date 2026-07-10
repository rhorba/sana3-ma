package ma.sana3.application.catalog;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.catalog.ProductSearchCriteria;
import ma.sana3.domain.catalog.ProductSearchResult;
import org.springframework.stereotype.Service;

@Service
public class SearchProductsHandler {

  private static final int MIN_PAGE_SIZE = 1;
  private static final int MAX_PAGE_SIZE = 100;
  private static final int DEFAULT_PAGE_SIZE = 20;

  private final ProductRepository productRepository;
  private final ArtisanProfileRepository artisanProfileRepository;

  public SearchProductsHandler(
      ProductRepository productRepository, ArtisanProfileRepository artisanProfileRepository) {
    this.productRepository = productRepository;
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public PublicProductPage handle(SearchProductsQuery query) {
    int safePage = Math.max(0, query.page());
    int safePageSize =
        query.pageSize() <= 0
            ? DEFAULT_PAGE_SIZE
            : Math.min(Math.max(MIN_PAGE_SIZE, query.pageSize()), MAX_PAGE_SIZE);

    ProductSearchResult result =
        productRepository.search(
            new ProductSearchCriteria(
                query.craftType(),
                query.region(),
                query.minPrice(),
                query.maxPrice(),
                query.keyword(),
                safePage,
                safePageSize));

    List<UUID> artisanProfileIds =
        result.products().stream().map(Product::artisanProfileId).distinct().toList();
    Map<UUID, ArtisanProfile> profilesById =
        artisanProfileRepository.findByIds(artisanProfileIds).stream()
            .collect(Collectors.toMap(ArtisanProfile::id, Function.identity()));

    List<PublicProductSummary> summaries =
        result.products().stream()
            .map(
                product ->
                    PublicProductSummaryMapper.toSummary(
                        product, profilesById.get(product.artisanProfileId())))
            .toList();

    return new PublicProductPage(summaries, result.totalElements(), safePage, safePageSize);
  }
}
