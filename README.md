# Password Manager API

The **Password Manager** is a secure backend application for storing and managing passwords using a **RESTful API**. It is built with **Spring Boot** and uses **AES-256 encryption** to securely store passwords in a **MySQL database**. The system requires a **master password**, which is used to derive an encryption key that protects stored passwords. The master password must be provided when the application starts, ensuring that only authorized users can decrypt and access stored credentials.

---

## 🔐 Password Encryption & Key Management

The application secures passwords using **AES-256 encryption** (CBC mode with PKCS5 padding) and a **master password system**.

### **How it Works:**
- When a user provides a plaintext password, it is **encrypted before being stored** in MySQL, ensuring that sensitive data is never exposed.
- The encrypted passwords are stored in a `VARBINARY` column, making them unreadable without the correct decryption key.
- Upon startup, the system checks for the existence of a **`secret.key`** file:
    - If the key file is missing, the user is prompted to create a **new master password**, which is used to generate an encryption key stored securely in `secret.key`.
    - If the key file exists, the user must enter the correct **master password** to derive the encryption key and decrypt stored passwords.
    - Without the correct master password, previously stored passwords cannot be decrypted, ensuring that unauthorized users cannot access sensitive data.

> **Important:** The `secret.key` file must be kept secure. If it is lost or deleted, all stored passwords will be irretrievable.

---

## 📦 Installation & Setup

### **1️⃣ Prerequisites**

Ensure you have installed:

- **Java 17+**
- **MySQL Server**
- **Maven**

### **2️⃣ Configure MySQL Database**

Create a MySQL database and update `src/main/resources/application.properties` with your credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/password_manager
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
```



### **3️⃣ Initialize the Database**

To set up the required database schema, use the provided **`Create_Table_Passwords.sql`** file:

```sql
CREATE TABLE passwords (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARBINARY(512) NOT NULL,
    service VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

You can execute this SQL script in your MySQL database before running the application.

### **4️⃣ Build & Run the Application**

Using **Maven**:

```bash
mvn clean install
mvn spring-boot:run
```

Or using **IntelliJ**/**VS Code**, simply run `PasswordManagerApplication.java`.

---

## 📡 API Usage

Once running, you can interact with the API using **Postman**, **cURL**, or any HTTP client.

### **Create a new password**
Creates a new password entry for a given username and service.
```bash
curl -X POST http://localhost:8080/api/passwords \
     -H "Content-Type: application/json" \
     -d '{"username": "user123", "plaintextPassword": "mypassword", "service": "GitHub"}'
```
#### Example Response:
```json
{
  "id": 1,
  "username": "user123",
  "service": "GitHub",
  "description": null,
  "createdAt": "2024-02-22T14:00:00"
}
```

### **Retrieve all stored passwords**
Fetches all stored password entries.
```bash
curl -X GET http://localhost:8080/api/passwords
```

### **Retrieve password by ID**
Fetches a specific password entry by its ID.
```bash
curl -X GET http://localhost:8080/api/passwords/{id}
```

### **Search passwords by exact username**
Finds password entries associated with an exact username match.
```bash
curl -X GET http://localhost:8080/api/passwords/search/username?username=user123
```

### **Search passwords by username pattern**
Finds password entries where the username contains a specified pattern.
```bash
curl -X GET http://localhost:8080/api/passwords/search/username-like?usernamePattern=%partialName%
```

### **Search passwords by exact service name**
Finds password entries associated with an exact service match.
```bash
curl -X GET http://localhost:8080/api/passwords/search/service?service=GitHub
```

### **Search passwords by service name pattern**
Finds password entries where the service name contains a specified pattern.
```bash
curl -X GET http://localhost:8080/api/passwords/search/service-like?servicePattern=%partialService%
```

### **Update an existing password**
Modifies an existing password entry by providing updated values.
```bash
curl -X PUT http://localhost:8080/api/passwords/{id} \
     -H "Content-Type: application/json" \
     -d '{"username": "user123", "plaintextPassword": "newpassword", "service": "GitHub"}'
```

### **Delete a password entry**
Removes a password entry from the database.
```bash
curl -X DELETE http://localhost:8080/api/passwords/1
```

---

## 🛠️ Tech Stack

| Category         | Technology/Tool                | Role/Usage                                                     |
| ---------------- |--------------------------------| -------------------------------------------------------------- |
| **Language**     | Java 23                       | Core programming language                                      |
| **Framework**    | Spring Boot, Spring Web        | Provides auto-configuration, dependency injection, and REST API management |
| **Data Access**  | Spring Data JPA with Hibernate | Simplifies ORM and database interactions with MySQL            |
| **Database**     | MySQL                          | Relational database for secure storage                         |
| **Encryption**   | Javax.Crypto API    | AES-256 encryption (CBC mode with PKCS5 padding) to secure sensitive data.         |
| **Build Tool**   | Maven                          | Dependency management and build automation                     |
| **Productivity** | Lombok                         | Reduces boilerplate code through annotations                    |

---

