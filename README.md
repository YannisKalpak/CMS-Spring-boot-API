# CMS REST API

A simple Content Management System (CMS) built as a Spring Boot REST API, allowing:

- **Admin** users to create, read, update, delete articles and upload images.  
- **Guest** users to read articles and view images.

All development and testing are done locally.

---

## Tech Stack

| Layer           | Technology                                           |
|-----------------|------------------------------------------------------|
| **Language**    | Java 17 (source) / Java 23 (runtime via VS Code)     |
| **Framework**   | Spring Boot 3.5.0                                    |
| **Build**       | Maven                                                |
| **Database**    | MySQL via XAMPP & phpMyAdmin (DB: `cmsdb`)           |
| **Auth**        | JWT (HS512)                                          |
| **Docs & UI**   | Swagger UI / OpenAPI & H2 DB (for embedded tests)    |
| **IDE**         | VS Code + Java Extension Pack (Microsoft) +          |
                  |   Java Auto Config (Pleiades)                        |

---

## Local Setup

1. **Install Java**  
   - JDK 17 (compile)  
   - JRE 23 (VS Code runtime)

2. **Install MySQL/XAMPP**  
   - Start **Apache** & **MySQL** via XAMPP Control Panel.  
   - In phpMyAdmin (`http://localhost/phpmyadmin`), create a database named `cmsdb`.

3. **Clone & Open**  
   ```bash
   git clone <your-repo-url>
   cd cms
   code .
