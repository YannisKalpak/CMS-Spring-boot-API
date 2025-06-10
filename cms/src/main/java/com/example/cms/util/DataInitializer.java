package com.example.cms.util;

import com.example.cms.entity.User;
import com.example.cms.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (userRepository.count() == 0) {
      User admin = new User();
      admin.setUsername("admin");
      admin.setPasswordHash(passwordEncoder.encode("password"));
      admin.setRole("ADMIN");

      User guest = new User();
      guest.setUsername("guest");
      guest.setPasswordHash(passwordEncoder.encode("guest123"));
      guest.setRole("GUEST");

      userRepository.saveAll(List.of(admin, guest));
      System.out.println("Seeded users: admin/password (ADMIN), guest/guest123 (GUEST)");

      /*
       * if (userRepository.count() == 0) {
       * User admin = new User(null,
       * "admin",
       * passwordEncoder.encode("password"),
       * "ADMIN");
       * User guest = new User(null,
       * "guest",
       * passwordEncoder.encode("guest123"),
       * "GUEST");
       * userRepository.saveAll(List.of(admin, guest));
       * System.out.
       * println("Seeded users: admin/password (ADMIN), guest/guest123 (GUEST)");
       */
    }
  }
}
