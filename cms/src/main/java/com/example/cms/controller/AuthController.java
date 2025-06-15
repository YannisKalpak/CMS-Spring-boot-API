package com.example.cms.controller;

import com.example.cms.dto.AuthRequest;
import com.example.cms.dto.AuthResponse;
import com.example.cms.entity.User;
import com.example.cms.security.CustomUserDetails;
import com.example.cms.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtUtil jwtUtil;

  @Operation(summary = "Login", tags = "auth-controller")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Authentication successful"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")
  })
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    try {
      Authentication auth = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

      CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
      User user = userDetails.getUser();

      String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
      AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole());
      return ResponseEntity.ok(response);
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }
}
