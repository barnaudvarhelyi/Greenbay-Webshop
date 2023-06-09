package mine.homeworkproject.services;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import mine.homeworkproject.dtos.ProductAllDto;
import mine.homeworkproject.dtos.ProductCreateDto;
import mine.homeworkproject.dtos.ProductDto;
import mine.homeworkproject.models.Product;
import org.springframework.http.ResponseEntity;


public interface ProductService {
  void getRandomProductsFromAPI();
  List<ProductAllDto> getAllAvailableProducts();
  ResponseEntity createProduct(ProductCreateDto product, HttpServletRequest request);
  ResponseEntity getProductById(Long id);
  Product findProductById(Long id);
  ResponseEntity deleteProductById(Long id, HttpServletRequest request);
  ResponseEntity editProductById(Long id, ProductCreateDto product, HttpServletRequest request);
  List<ProductDto> search(String searchTerm);
  List<ProductDto> searchAndSortByPurchasePriceAsc(String searchTerm);
  List<ProductDto> searchAndSortByPurchasePriceDesc(String searchTerm);
  List<ProductDto> sortByPurchasePriceAsc();
  List<ProductDto> sortByPurchasePriceDesc();
  void saveProduct(Product product);
}
