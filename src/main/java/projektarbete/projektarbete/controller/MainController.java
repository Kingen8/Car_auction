package projektarbete.projektarbete.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Map;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import projektarbete.projektarbete.entity.AllBids;
import projektarbete.projektarbete.entity.Bid;
import projektarbete.projektarbete.entity.ClosedAuctions;
import projektarbete.projektarbete.entity.Product;

import projektarbete.projektarbete.entity.User;

import projektarbete.projektarbete.repository.AllBidRepository;
import projektarbete.projektarbete.repository.BidRepository;
import projektarbete.projektarbete.repository.ClosedAuctionsRepository;
import projektarbete.projektarbete.repository.ProductRepository;

import projektarbete.projektarbete.repository.UserRepository;
import projektarbete.projektarbete.service.BidService;
import projektarbete.projektarbete.service.ProductService;
import projektarbete.projektarbete.service.SendEmailService;
import projektarbete.projektarbete.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MainController {

  @Autowired
  private ProductRepository pRepo;
  @Autowired
  private ProductService pService;
  @Autowired
  private UserService uService;
  @Autowired
  private BidService bService;
  @Autowired
  private UserRepository uRepo;
  @Autowired
  private BidRepository bRepo;
  @Autowired
  private AllBidRepository allBidsRepo;
  @Autowired
  private SendEmailService sendEmailService;
  @Autowired
  private ClosedAuctionsRepository closedAuctionsRepo;

  @GetMapping("/")
  public String index(Model model) {
    return "redirect:/restricted/0";

  }

  @GetMapping("/closedauctions")
  public String seeClosedAuctions(Model model) {
    model.addAttribute("closedauctions", closedAuctionsRepo.findAll());
    return "closedauctions";
  }

  @RequestMapping("/restricted/{pageno}")
  public String home(Model model, Authentication authentication, @PathVariable() Integer pageno) throws ParseException {

    if (pageno <= 0 || pageno == null) {
      pageno = 0;
    }
    final int PAGESIZE = 8;
    PageRequest paging = PageRequest.of(pageno, PAGESIZE);
    Page<Product> pagedResult = pRepo.findAll(paging);

    List<Product> listPost;
    listPost = pagedResult.getContent();

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = (String) oAuth2User.getAttributes().get("email");
    String name = (String) oAuth2User.getAttributes().get("name");
    List<User> userList = uRepo.findAll();

    model.addAttribute("currentPageNumber", pagedResult.getNumber());
    model.addAttribute("displayableCurrentPageNumber", pagedResult.getNumber() + 1);
    model.addAttribute("nextPageNumber", pageno + 1);
    model.addAttribute("previousPageNumber", pageno - 1);
    model.addAttribute("totalPages", pagedResult.getTotalPages());
    model.addAttribute("totalItems", pagedResult.getTotalElements());
    model.addAttribute("hasNext", pagedResult.hasNext());
    model.addAttribute("hasPrevious", pagedResult.hasPrevious());
    model.addAttribute("products", listPost);
    model.addAttribute("bids", bRepo.findAll());

    for (User a : userList) {
      if (a.getEmail().equals(email)) {
        model.addAttribute("user", a);
        System.out.println(a.getRole());
      }
    }

    // Returns the array in reverse order so we can see the latest bid first.
    List<AllBids> allBidsLista = allBidsRepo.findAll();
    Collections.reverse(allBidsLista);

    model.addAttribute("allbids", allBidsLista);

    Boolean found = false;
    for (User a : userList) {
      if (a.getEmail().equals(email)) {
        found = true;
        System.out.println("already in db");
        break;
      }
    }
    if (!found) {
      User user = new User();
      user.setEmail(email);
      user.setName(name);
      uRepo.save(user);
      System.out.println("Added to db");
    }

    return "index";

  }

  @RequestMapping(value = "/doaddproduct", method = RequestMethod.GET)
  public String addProductView() {
    return "addproduct";
  }

  @RequestMapping(value = "/addProd", method = { RequestMethod.POST, RequestMethod.GET })
  public String addProd(@ModelAttribute("Product") Product product,
      @RequestParam Map<String, String> allFormRequestParams, Authentication authentication) {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = (String) oAuth2User.getAttributes().get("email");

    List<User> userList = uRepo.findAll();

    for (User a : userList) {
      if (a.getEmail().equals(email)) {
        product.setUser(a);
      }
    }
    sendEmailService
        .sendEmail(
            email, "Name of product: " + product.getName() + "\n Description: " + product.getDescription()
                + " \n Price: " + product.getPrice() + "\nBest Regards, \n //Team Auction",
            "Your item was added successfully!");
    pService.saveProduct(product);

    return "redirect:/restricted/0";
  }

  @GetMapping(path = "/product/{id}")
  public String productview(Model model, Product product, User user, @PathVariable Integer id,
      Authentication authentication) {
    model.addAttribute("product", pService.getProById(id));
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = (String) oAuth2User.getAttributes().get("email");
    List<User> userList = uRepo.findAll();
    for (User a : userList) {
      if (a.getEmail().equals(email)) {
        model.addAttribute("user", a);
        System.out.println(a.getRole());
      }
    }
    return "productview";
  }

  @GetMapping("/search")
  public String home(Model model, Product product, @RequestParam String search) {

    model.addAttribute("products", pRepo.searchProduct(search));
    return "searchview";

  }

  @RequestMapping("/addbid/{id}")
  public String addBid(@PathVariable(required = false) Integer id, Product product, Authentication authentication) {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = (String) oAuth2User.getAttributes().get("email");
    List<User> usersList = uRepo.findAll();
    List<Product> productList = pRepo.findAll();
    List<Bid> bidList = bRepo.findAll();
    Bid bid = new Bid();
    AllBids ab = new AllBids();
    Integer startprice = 0;
    Integer lastprice = 0;
    System.out.println(lastprice);

    for (User a : usersList) {
      if (a.getEmail().equals(email)) {
        bid.setUser(a);
        ab.setUser(a);

      }
    }

    Boolean found = false;
    for (Product a : productList) {
      if ((int) product.getId() == (int) a.getId()) {
        System.out.println(product.getId() + "PRODUKTID");
        System.out.println(a.getId() + "OBJEKT ID");
        found = true;

      }
      if (found) {
        lastprice = a.getPrice();
        System.out.println(lastprice + "INNUTI FORLOOP");
        break;

      }

    }

    for (Bid a : bidList) {
      if ((int) a.getProduct().getId() == (int) product.getId()) {
        lastprice = a.getBidAmount();
        System.out.println(a.getBidAmount() + "Bidamount");
        bRepo.delete(a);

      }

    }
    System.out.println(startprice + "STARTPRICE");
    System.out.println(lastprice + "LASTPRICE2");

    bid.setBidAmount(bService.doBid(lastprice));
    bid.setProduct(product);
    bRepo.save(bid);

    ab.setBidAmount(bService.doBid(lastprice));
    ab.setProduct(product);
    allBidsRepo.save(ab);

    for (Product a : productList) {
      if ((int) a.getId() == (int) product.getId()) {

        sendEmailService.sendEmail(
            email, "Product: " + a.getName() + " \n Description: " + a.getDescription() + " \n Your bid: "
                + bid.getBidAmount() + "SEK \n \n May the odds be ever in your favor \n //Team Auction ",
            "Thank you for your bid!");

      }
    }

    return "redirect:/restricted/0";

  }

  @RequestMapping("/closeauction/{id}")
  public String closeAuction(@PathVariable(required = false) Integer id, Product product,
      Authentication authentication) {

    List<Bid> bList = bRepo.findAll();

    for (Bid b : bList) {
      System.out.println(b.getId() == product.getId());
      if ((int) b.getProduct().getId() == (int) product.getId()) {
        ClosedAuctions closedauction = new ClosedAuctions();
        closedauction.setClosedamount(b.getBidAmount());
        closedauction.setProduct(product);
        closedauction.setUser(b.getUser());
        closedauction.setDescription(b.getProduct().getCategories());
        closedauction.setDate(b.getProduct().getDate());
        closedauction.setName(b.getProduct().getName());
        closedauction.setPicture(b.getProduct().getPicture());
        closedauction.setCategories(b.getProduct().getCategories());
        closedAuctionsRepo.save(closedauction);

        sendEmailService.sendEmail(b.getUser().getEmail(),
            "Product: " + b.getProduct().getName() + " \n Description: " + b.getProduct().getDescription()
                + " \n Your bid: " + b.getBidAmount() + "SEK \n \n Best Regards \n // Auction Team",
            "You won the Auction!!");

        bRepo.delete(b);
        pRepo.delete(product);
      }
    }

    return "redirect:/restricted/0";

  }

  @GetMapping(path = "/edit/{id}")
  public String editPost(Model model, @PathVariable Integer id) {
    model.addAttribute("product", pService.getProById(id));
    return "updateview";
  }

  @RequestMapping(path = "/update", method = { RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET })
  public String updatePost(@ModelAttribute("product") Product product,
      @RequestParam Map<String, String> allRequestParams) {

    pService.updateProd(product);
    return "redirect:/restricted/0";
  }

  @RequestMapping("/delete/{id}")
  public String deleteProduct(Product product, @PathVariable Integer id) {
    System.out.println("hej");
    System.out.println(id);
    pService.deleteProd(id);
    // pRepo.deleteById(id);
    return "redirect:/restricted/0";
  }

}
