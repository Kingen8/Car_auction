package projektarbete.projektarbete.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import projektarbete.projektarbete.entity.Product;
import projektarbete.projektarbete.repository.ProductRepository;

@Service
public class ProductService {

  @Autowired
  private ProductRepository prorepository;

  public Product saveProduct(Product pro) {
    return prorepository.save(pro);
  }

  public List<Product> getAllProducts() {
    return prorepository.findAll();
  }

  public Product getProById(int id) {
    return prorepository.findById(id).orElse(null);
  }

  public Product updateProd(Product product) {
    Product existingProd = prorepository.findById(product.getId()).orElse(null);
    existingProd.setDescription(product.getDescription());
    existingProd.setCategories(product.getCategories());
    existingProd.setName(product.getName());
    existingProd.setPicture(product.getPicture());
    existingProd.setPrice(product.getPrice());
    return prorepository.save(existingProd);
  }

  public String deleteProd(int id) {
    prorepository.deleteById(id);
    return "product removed";
  }
}
