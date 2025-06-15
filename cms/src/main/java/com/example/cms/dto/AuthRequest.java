// src/main/java/com/example/cms/dto/AuthRequest.java
package com.example.cms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthRequest {
  private String username;
  private String password;
}
