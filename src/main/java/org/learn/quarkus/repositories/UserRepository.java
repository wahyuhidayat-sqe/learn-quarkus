package org.learn.quarkus.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.learn.quarkus.models.User;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public List<User> all(String q) {
        if (q != null && !q.trim().isEmpty()) {
            return find("name LIKE :q",
                    Parameters.with("q", "%" + q.trim() + "%")).list();
        }
        return listAll();
    }

    @Transactional
    public User create(User user) {
        persistAndFlush(user);
        return user;
    }

    public User findByEmail(String email) {
        return find("email = ?1", email).firstResult();
    }

    public User findById(UUID id) {
        return find("id = ?1", id).firstResult();
    }

    @Transactional
    public void updateUserPhoto(UUID userId, String photoUrl) {
        User user = findById(userId);
        if (user != null) {
            user.photo = photoUrl;
            persistAndFlush(user); // Persist the updated entity
        }
    }
}
