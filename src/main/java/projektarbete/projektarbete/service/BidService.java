package projektarbete.projektarbete.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import projektarbete.projektarbete.entity.AllBids;
import projektarbete.projektarbete.entity.Bid;
import projektarbete.projektarbete.entity.Product;
import projektarbete.projektarbete.repository.AllBidRepository;
import projektarbete.projektarbete.repository.BidRepository;

@Service
public class BidService {

  @Autowired
  private BidRepository bidRepo;
  @Autowired
  private AllBidRepository allBidRepo;

  public Integer doBid(Integer bidding) {

    Integer round = bidding / 10;
    System.out.println(round + "Ifrån bidservice metod");
    Integer finalbid = bidding;
    finalbid += round;

    System.out.println(finalbid + "FINALBID");

    return finalbid;
  }

}
