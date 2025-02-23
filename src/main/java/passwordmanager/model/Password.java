package passwordmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * This class represents the 'passwords' table in the MySQL database.
 */

@Entity
@Table(name = "passwords")
@Data // Generate getters, setters, toString, equals, hashCode
@NoArgsConstructor // Generate no-args constructor required by JPA
@AllArgsConstructor // Generate all-args constructor
public class Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "password", nullable = false, columnDefinition = "VARBINARY(512)")
    private byte[] encryptedPassword;

    @Column(nullable = false)
    private String service;

    private String description;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    //------------
    //Constructors, Getters and Setters by Lombok
    //------------

    //This method runs before INSERT
    @PrePersist
    protected void onCreate() {
        // If createdAt is null, set it to the current time
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    //TODO: maybe add "updatedAt" attribute

    //TODO: handle duplication?
}
