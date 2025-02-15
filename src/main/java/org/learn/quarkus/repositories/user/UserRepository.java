package org.learn.quarkus.repositories.user;

import org.learn.quarkus.models.primary.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    List<User> all(String q);

    User create(User user);

    User findByEmail(String email);

    User findById(UUID id);

    void updateUserPhoto(UUID userId, String photoUrl);
}
