package org.learn.quarkus.services;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.learn.quarkus.exception.DuplicateValueException;
import org.learn.quarkus.models.primary.User;
import org.learn.quarkus.repositories.user.UserRepository;
import org.learn.quarkus.repositories.storage.StorageRepository;
import org.learn.quarkus.rest.user.schema.CreateUserRequest;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.learn.quarkus.exception.ExceptionUtil.isConstraintViolation;

@ApplicationScoped
@Slf4j
public class UserService {

    UserRepository userRepository;
    StorageRepository storageRepository;

    public UserService(
            UserRepository userRepository,
            StorageRepository storageRepository
    ) {
        this.userRepository = userRepository;
        this.storageRepository = storageRepository;
    }

    public User createUser(CreateUserRequest request) throws DuplicateValueException {
        String hash = BcryptUtil.bcryptHash(request.password);
        User user = User.builder()
                .name(request.name)
                .email(request.email)
                .password(hash)
                .build();

        try {
            return userRepository.create(user);
        } catch (PersistenceException e) {
            if (isConstraintViolation(e)) {
                log.warn("Duplicate entry error: {}", e.getMessage());
                throw new DuplicateValueException("Email has already been used.");
            }

            log.error("Unexpected database error: {}", e.getMessage());
            throw new PersistenceException("Database operation failed.");
        }
    }

    public List<User> getUser(String q) {
        return userRepository.all(q);
    }


    public void uploadPhoto(UUID userId, InputStream inputStream) {
        var fileName = ".jpg";
        var blob = storageRepository.uploadFile(inputStream, fileName, "image/jpeg");
        userRepository.updateUserPhoto(userId, blob.path);
    }

    public String getPhotoUrl(User user) {
        if (user.photo == null) {
            return null;
        }
        try {
            return storageRepository.getDownloadUrl(user.photo);
        } catch (Exception e) {
            log.error("error getting photo url: {}", e.getMessage());
            return null;
        }
    }
}
