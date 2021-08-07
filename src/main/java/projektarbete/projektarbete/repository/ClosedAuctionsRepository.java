package projektarbete.projektarbete.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import projektarbete.projektarbete.entity.ClosedAuctions;

public interface ClosedAuctionsRepository extends JpaRepository<ClosedAuctions, Integer> {

}
