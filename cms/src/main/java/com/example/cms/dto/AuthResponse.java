// src/main/java/com/example/cms/dto/AuthResponse.java
package com.example.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
  private String token;
  private String username;
  private String role;
}
// This class represents the response sent back to the client after a successful
// authentication.