package projektarbete.projektarbete.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projektarbete.projektarbete.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
