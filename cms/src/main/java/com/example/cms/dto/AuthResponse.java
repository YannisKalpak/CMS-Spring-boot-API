package com.example.cms.dto;

import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.Getter;

//@Data
//@Getter
// @AllArgsConstructor
public class AuthResponse {
  private String token;

  public AuthResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

}
