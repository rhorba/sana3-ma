package ma.sana3.adapter.persistence.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {

  List<ProductJpaEntity> findByArtisanProfileId(UUID artisanProfileId);

  // Joins to ArtisanProfileJpaEntity for the region filter (docs/stories-sana3-ma-sprint2.md
  // Story 4.3: "region filter joins to artisan_profiles.region, not products").
  // Filter args must arrive pre-lowercased (see ProductRepositoryAdapter) -- calling LOWER() on a
  // null bind parameter makes PostgreSQL infer its type as bytea instead of text/varchar, which
  // then fails with "function lower(bytea) does not exist" the moment any filter is actually null.
  @Query(
      "SELECT p FROM ProductJpaEntity p, ma.sana3.adapter.persistence.artisanprofile.ArtisanProfileJpaEntity a "
          + "WHERE p.artisanProfileId = a.id "
          + "AND (:craftType IS NULL OR LOWER(p.craftType) = :craftType) "
          + "AND (:region IS NULL OR LOWER(a.region) = :region) "
          + "AND (:minPrice IS NULL OR p.priceAmount >= :minPrice) "
          + "AND (:maxPrice IS NULL OR p.priceAmount <= :maxPrice) "
          + "AND (:keyword IS NULL OR LOWER(p.name) LIKE CONCAT('%', :keyword, '%') "
          + "     OR LOWER(p.description) LIKE CONCAT('%', :keyword, '%'))")
  Page<ProductJpaEntity> search(
      @Param("craftType") String craftTypeLowered,
      @Param("region") String regionLowered,
      @Param("minPrice") BigDecimal minPrice,
      @Param("maxPrice") BigDecimal maxPrice,
      @Param("keyword") String keywordLowered,
      Pageable pageable);
}
