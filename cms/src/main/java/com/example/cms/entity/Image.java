package com.example.cms.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
public class Image {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String filename;

  @Column(nullable = false)
  private String filepath;

  @Column(nullable = false, updatable = false)
  private Instant uploadedAt;

  @PrePersist
  protected void onCreate() {
    this.uploadedAt = Instant.now();
  }

  public Instant getUploadedAt() {
    return uploadedAt;
  }
}
