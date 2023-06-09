package mine.homeworkproject.repositories;

import java.util.List;
import java.util.Optional;
import mine.homeworkproject.models.Product;
import mine.homeworkproject.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query("SELECT p FROM Product p WHERE p.forSale = TRUE ORDER BY function('RAND')")
  List<Product> findRandomProducts(Pageable pageable);
  @Query("SELECT p FROM Product p WHERE p.uploader = :uploader AND p.forSale = TRUE")
  List<Product> findAllByUploaderAndAvailable(@Param("uploader") User uploader);
  @Query("SELECT p FROM Product p WHERE p.owner = :owner AND p.owner != p.uploader")
  List<Product> findAllByOwnerNotEqualsUploader(@Param("owner") User owner);
  List<Product> findAllByExpiresAtNotNullAndForSale(Boolean forSale);
  List<Product> findAllByForSale(Boolean forSale);
  List<Product> findByForSaleAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(Boolean forSale, String searchTerm, String searchTerm2);
  List<Product> findByForSaleAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByPurchasePriceAsc(Boolean forSale, String searchTerm, String searchTerm2);
  List<Product> findByForSaleAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByPurchasePriceDesc(Boolean forSale, String searchTerm, String searchTerm2);
  List<Product> findByForSaleOrderByPurchasePriceAsc(Boolean forSale);
  List<Product> findByForSaleOrderByPurchasePriceDesc(Boolean forSale);
  Optional<Product> findById(Long id);
}