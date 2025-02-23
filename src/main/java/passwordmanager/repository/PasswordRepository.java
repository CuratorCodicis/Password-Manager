package passwordmanager.repository;

import passwordmanager.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long>{
    //TODO: Add custom query methods if necessary.

    // Fetch a list of all entries, given a username
    List<Password> findByUsername(String username);
    // Fetch a list of all entries, where username LIKE the given String
    List<Password> findByUsernameLike(String username);

    // Fetch a list of entries, given a service
    List<Password> findByService(String service);
    // Fetch a list of all entries, where service LIKE the given String
    List<Password> findByServiceLike(String service);
}
