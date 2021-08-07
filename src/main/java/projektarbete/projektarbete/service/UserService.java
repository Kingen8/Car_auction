package projektarbete.projektarbete.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import projektarbete.projektarbete.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepo;

}
