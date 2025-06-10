package com.example.cms.controller;

import com.example.cms.dto.AuthRequest;
import com.example.cms.dto.AuthResponse;
import com.example.cms.entity.User;
import com.example.cms.repository.UserRepository;
import com.example.cms.util.JwtUtil;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody @Valid AuthRequest req) {
    if (userRepository.findByUsername(req.getUsername()).isPresent()) {
      return ResponseEntity
          .status(HttpStatus.CONFLICT)
          .body(Map.of("error", "Username already taken"));
    }
    User newUser = new User();
    newUser.setUsername(req.getUsername());
    newUser.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    // Default role = GUEST; admins should be created by an existing ADMIN
    newUser.setRole("GUEST");
    userRepository.save(newUser);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
    try {
      @SuppressWarnings("unused")
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
      // If successful, generate JWT
      User user = userRepository.findByUsername(authRequest.getUsername()).get();
      String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
      return ResponseEntity.ok(new AuthResponse(token));
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(401).build();
    }
  }

  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }

  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }
}
