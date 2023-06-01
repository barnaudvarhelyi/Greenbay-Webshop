package mine.homeworkproject.controllers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import mine.homeworkproject.dtos.ProductAllDto;
import mine.homeworkproject.dtos.ProductCreateDto;
import mine.homeworkproject.dtos.ResponseDto;
import mine.homeworkproject.models.Product;
import mine.homeworkproject.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductsController {

  private final ProductService productService;
  @Autowired
  public ProductsController(ProductService productService) {
    this.productService = productService;
  }
  @GetMapping("/products/all")
  public List<ProductAllDto> getAllProducts() {
    return productService.getAllAvailableProducts();
  }

//  @GetMapping("/products/search")
//  public ResponseEntity searchProductsByStr(@RequestParam(required = false, defaultValue = "") String search) {
//    return productService.searchItemByStr(search);
//  }
//  @GetMapping("/products/sort/{direction}")
//  public ResponseEntity sortProducts(@PathVariable(required = false) String direction){
//    return productService.sortProducts(direction);
//  }

  @RequestMapping(value = "/products", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}, params = "id")
  public ResponseEntity<?> handleProduct(@RequestParam("id") Long id, @RequestBody ProductCreateDto productCreateDto, HttpServletRequest request) {
    ResponseEntity<?> response;
    String method = request.getMethod();

    if (RequestMethod.GET.name().equals(method)) {
      response = getProductById(id);
    } else if (RequestMethod.POST.name().equals(method)) {
      response = createProduct(productCreateDto, request);
    } else if (RequestMethod.PUT.name().equals(method)) {
      response = updateProduct(id, productCreateDto, request);
    } else if (RequestMethod.DELETE.name().equals(method)) {
      response = deleteProductById(id, request);
    } else {
      return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ResponseDto("Unsupported request method"));
    }
    return response;
  }
  @GetMapping("/products/{id}")
  public ResponseEntity getProductById(@PathVariable Long id) {
    return productService.getProductById(id);
  }
  @PostMapping("/products")
  public ResponseEntity createProduct(@RequestBody ProductCreateDto product, HttpServletRequest request) {
    return productService.createProduct(product, request);
  }
  @PutMapping("/products/{id}")
  public ResponseEntity updateProduct(@PathVariable Long id, @RequestBody ProductCreateDto product,HttpServletRequest request) {
    return productService.editProductById(id, product,request);
  }
  @DeleteMapping("/products/{id}")
  public ResponseEntity deleteProductById(@PathVariable Long id, HttpServletRequest request) {
    return productService.deleteProductById(id, request);
  }
}
