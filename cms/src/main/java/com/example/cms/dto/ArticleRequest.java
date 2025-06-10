package com.example.cms.dto;

import jakarta.validation.constraints.NotBlank;
//import lombok.Data;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;

//@Data
//@Getter
//@Setter
//@NoArgsConstructor
public class ArticleRequest {
  @NotBlank
  private String title;

  @NotBlank
  private String content;

  public ArticleRequest(String title, String content) {
    this.title = title;
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
