package mine.homeworkproject.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import jdk.javadoc.internal.doclets.toolkit.util.Comparators;
import mine.homeworkproject.dtos.ProductDto;
import mine.homeworkproject.dtos.ResponseDto;
import mine.homeworkproject.dtos.TransactionDto;
import mine.homeworkproject.dtos.UserByIdResponseDto;
import mine.homeworkproject.dtos.UserProfileResponseDto;
import mine.homeworkproject.dtos.UsersActiveBidsDto;
import mine.homeworkproject.models.Bid;
import mine.homeworkproject.models.Transaction;
import mine.homeworkproject.models.User;
import mine.homeworkproject.repositories.BidRepository;
import mine.homeworkproject.repositories.ProductRepository;
import mine.homeworkproject.repositories.TransactionRepository;
import mine.homeworkproject.repositories.UserRepository;
import mine.homeworkproject.security.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  @PersistenceContext
  private EntityManager entityManager;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final BidRepository bidRepository;
  private final TransactionService transactionService;
  private final TransactionRepository transactionRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, ProductRepository productRepository,
      BidRepository bidRepository, TransactionService transactionService, TransactionRepository transactionRepository) {
    this.userRepository = userRepository;
    this.productRepository = productRepository;
    this.bidRepository = bidRepository;
    this.transactionService = transactionService;
    this.transactionRepository = transactionRepository;
  }

  @Override
  public User findUserByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new RuntimeException(
                    username + " is not found!"));

  }
  @Override
  public List<User> findAllUsers() {
    return userRepository.findAll();
  }
  @Override
  public User findUserById(Long id) {
    return userRepository.findById(id).orElse(null);
  }
  @Override
  public Optional<User> getUserByToken(HttpServletRequest request) {
    String header = request.getHeader(JwtProperties.HEADER_STRING);

    if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
      return Optional.empty();
    }
    String token =
        request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");

    String username =
        JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()))
            .build()
            .verify(token)
            .getSubject();

    if (username != null) {
      return userRepository.findByUsername(username);
    }
    return Optional.empty();
  }
  @Override
  public ResponseEntity getUserProfile(HttpServletRequest request) {
    Optional<User> user = getUserByToken(request);
    if (!user.isPresent()) {
      ResponseDto response = new ResponseDto("User not found!");
      return ResponseEntity.status(404).body(response);
    }
    List<ProductDto> uploadedProducts = productRepository.findAllByUploaderAndAvailable(user.get()).stream()
        .map(ProductDto::new)
        .collect(Collectors.toList());
    List<ProductDto> ownedProducts = productRepository.findAllByOwnerNotEqualsUploader(user.get()).stream()
        .map(ProductDto::new)
        .collect(Collectors.toList());
    List<TransactionDto> userTransactions = transactionRepository.findAllByUser(user.get()).stream()
        .map(TransactionDto::new)
        .collect(Collectors.toList());
    Collections.reverse(userTransactions);

    return ResponseEntity.status(200).body(
      new UserProfileResponseDto(
        user.get().getUsername(),
        uploadedProducts.size(),
        uploadedProducts,
        user.get().getBalanceAsDto(),
        ownedProducts,
        ownedProducts.size(),
        getUsersAllActiveBid(user.get()),
        userTransactions
      )
    );
  }
  @Override
  public ResponseEntity addBalance(HashMap<String, String> balance, HttpServletRequest request) {
    Optional<User> user = getUserByToken(request);
    if (!user.isPresent()) {
      ResponseDto response = new ResponseDto("User not found!");
      return ResponseEntity.status(404).body(response);
    }
    Double balanceDb;
    try {
      balanceDb = Double.parseDouble(balance.get("balance"));
    } catch (NumberFormatException nfe) {
      ResponseDto response = new ResponseDto("Please provide valid amount!");
      return ResponseEntity.status(404).body(response);
    }
    user.get().setPlusBalance(balanceDb);
    userRepository.save(user.get());
    transactionService.save(
        user.get(),
        "Added balance",
        true,
        balanceDb
    );
    return ResponseEntity.status(200).body(new ResponseDto("Balance added successfully!"));
  }
  @Override
  public ResponseEntity getUserProfileById(String username) {
    User user = findUserByUsername(username);
    if (user == null) {
      return ResponseEntity.status(404).body(new ResponseDto("User not found!"));
    }
    List<ProductDto> productsDto = productRepository.findAllByUploaderAndAvailable(user)
        .stream()
        .map(ProductDto::new)
        .collect(Collectors.toList());
    return ResponseEntity.ok(new UserByIdResponseDto(user, productsDto));
  }
  @Override
  public void save(User user) {
    userRepository.save(user);
  }
  @Override
  public User findUserByIdWithOwnedProducts(Long userId) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
    Root<User> root = query.from(User.class);

    root.fetch("ownedProducts", JoinType.LEFT);

    query.select(root).where(criteriaBuilder.equal(root.get("id"), userId));

    return entityManager.createQuery(query).getSingleResult();
  }
  private List<UsersActiveBidsDto> getUsersAllActiveBid(User user) {
    List<Bid> bids = bidRepository.findAllByUser(user);
    List<UsersActiveBidsDto> usersActiveBids = new ArrayList<>();
    if (bids.size() > 0) {
      for (Bid bid : bids) {
        usersActiveBids.add(
            new UsersActiveBidsDto(
                bid.getProduct().getExpiresAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                bid.getProduct().getPhotoUrl(),
                bid.getProduct().getId(),
                bid.getProduct().getName(),
                bid.getAmount(),
                bidRepository.isHighestBidderOnProduct(bid.getProduct(), user)
            )
        );
      }
    }
    return usersActiveBids;
  }
}