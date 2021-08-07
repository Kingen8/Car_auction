package projektarbete.projektarbete.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projektarbete.projektarbete.entity.Bid;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

}
