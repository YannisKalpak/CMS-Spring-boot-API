package com.example.cms.controller;

import com.example.cms.entity.User;
import com.example.cms.security.CustomUserDetails;
import com.example.cms.security.CustomUserDetailsService;
import com.example.cms.util.JwtUtil;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AuthenticationManager authManager;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private CustomUserDetailsService userService;

  @InjectMocks
  private AuthController authController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @Test
  void testLoginSuccess() throws Exception {
    // Create mock User entity
    User mockUser = new User();
    mockUser.setUsername("johnny");
    mockUser.setRole("ADMIN");
    mockUser.setPasswordHash("password");

    // Wrap in CustomUserDetails
    CustomUserDetails userDetails = new CustomUserDetails(mockUser);

    // Create authentication with CustomUserDetails as principal
    Authentication auth = new UsernamePasswordAuthenticationToken(
        userDetails, // Correct principal type
        null,
        userDetails.getAuthorities());

    when(authManager.authenticate(any())).thenReturn(auth);
    when(jwtUtil.generateToken("johnny", "ADMIN")).thenReturn("jwt-token");

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"johnny\",\"password\":\"string\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"));
  }

  @Test
  void testLoginWithInvalidRole() throws Exception {
    User mockUser = new User();
    mockUser.setUsername("invalid");
    mockUser.setRole("INVALID_ROLE");

    CustomUserDetails userDetails = new CustomUserDetails(mockUser);
    Authentication auth = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_INVALID")));

    when(authManager.authenticate(any())).thenReturn(auth);

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"invalid\",\"password\":\"pass\"}"))
        .andExpect(status().isOk()); // Should still return 200 with token
  }

  @Test
  void testLoginFailure() throws Exception {
    when(authManager.authenticate(any()))
        .thenThrow(new BadCredentialsException("bad credentials"));

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"wrong\",\"password\":\"creds\"}"))
        .andExpect(status().isUnauthorized());
  }
}
