package com.example.cms.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "images")
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

  public Image() {
    // JPA
  }

  @PrePersist
  protected void onCreate() {
    this.uploadedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFilepath() {
    return filepath;
  }

  public void setFilepath(String filepath) {
    this.filepath = filepath;
  }

  public Instant getUploadedAt() {
    return uploadedAt;
  }
}
