package projektarbete.projektarbete.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import projektarbete.projektarbete.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

  @Query("SELECT p FROM Product p WHERE p.categories LIKE %?1%")
  public List<Product> searchProduct(String categories);

}
