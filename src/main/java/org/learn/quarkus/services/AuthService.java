package org.learn.quarkus.services;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.learn.quarkus.models.User;
import org.learn.quarkus.repositories.UserRepository;
import org.learn.quarkus.repositories.storage.StorageRepository;
import org.learn.quarkus.rest.auth.schema.LoginData;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class AuthService {

    private final String jwtSecret;
    private final UserRepository userRepository;
    private final StorageRepository storageRepository;
    private final JWTParser jwtParser;

    public AuthService(
            @ConfigProperty(name = "app.jwt.secret") String jwtSecret,
            UserRepository userRepository,
            StorageRepository storageRepository,
            JWTParser jwtParser
    ) {
        this.jwtSecret = jwtSecret;
        this.userRepository = userRepository;
        this.storageRepository = storageRepository;
        this.jwtParser = jwtParser;
    }

    public LoginData login(String email, String password) throws AuthException {
        var user = userRepository.findByEmail(email);
        if (user == null || !BcryptUtil.matches(password, user.password)) {
            throw new AuthException("invalid email or password");
        }

        var expiresAt = Instant.now().plusSeconds(60 * 60 * 24);
        var token = Jwt
                .upn(user.email)
                .subject(user.id.toString())
                .expiresAt(expiresAt)
                .signWithSecret(jwtSecret);
        if (user.photo != null) {
            var photoUrl = storageRepository.getDownloadUrl(user.photo);
            user.setPhoto(photoUrl);
        }

        return LoginData.builder()
                .user(user)
                .token(token)
                .build();
    }

    public User verifyToken(String token) throws AuthException {
        if (token == null || token.trim().isEmpty()) {
            throw new AuthException("invalid token");
        }
        try {
            var claims = jwtParser.verify(token, jwtSecret);
            var user = userRepository.findById(UUID.fromString(claims.getSubject()));
            if (user == null) {
                throw new AuthException("invalid token");
            }
            return user;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new AuthException("invalid token");
        }
    }
}
