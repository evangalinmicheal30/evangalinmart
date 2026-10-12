# Evangalinmart

A Java 17 + Spring Boot 2.7.18 + Maven grocery-store capstone project. It uses Thymeleaf for pages, Spring Data JPA for persistence, and an embedded H2 database. Maven builds a Tomcat 9-compatible WAR named `evangalinmart.war`.

## Requirements

- JDK 17 or newer
- Maven 3.6+
- Apache Tomcat 9.0

## Open and build in VS Code

1. Extract this ZIP into a new folder. If OneDrive locks build files, choose a location outside OneDrive.
2. In VS Code choose **File → Open Folder…** and select the `evangalinmart` folder containing `pom.xml`.
3. Open **Terminal → New Terminal** and run:

```powershell
mvn clean package
```

The deployable file will be `target/evangalinmart.war`.

## Deploy to Tomcat 9

1. Stop Tomcat 9.
2. Copy `target/evangalinmart.war` into Tomcat's `webapps` folder, replacing the older WAR. Remove the old exploded `webapps/evangalinmart` folder if it remains.
3. Start Tomcat and wait for deployment to complete.
4. Open `http://localhost:8080/evangalinmart/`. The login page appears first. Choose **Create an account** to make your own Buyer or Seller email/password; there are no preset credentials.

For local development without external Tomcat, use `mvn spring-boot:run` and open `http://localhost:8080/`.

## Features

- Buyer and Seller registration/login; passwords are BCrypt-hashed in H2
- Buyers can browse/search groceries, manage their basket, checkout, and view their own orders
- Buyers can leave one 1–5 star review per grocery item; reviews are saved in H2 and displayed on product pages
- Sellers can add, edit, and remove grocery products and review all received orders
- Sample grocery products are added on first start

Choose Buyer or Seller on the registration screen. This is a learning capstone app; it does not collect payments or provide separate seller verification.
