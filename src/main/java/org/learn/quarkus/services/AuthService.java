package org.learn.quarkus.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.learn.quarkus.models.User;
import org.learn.quarkus.repositories.UserRepository;
import org.learn.quarkus.repositories.storage.StorageRepository;
import org.learn.quarkus.rest.auth.schema.LoginData;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class AuthService {

    private final String jwtSecret;
    private final UserRepository userRepository;
    private final StorageRepository storageRepository;

    public AuthService(
            @ConfigProperty(name = "app.jwt.secret") String jwtSecret,
            UserRepository userRepository,
            StorageRepository storageRepository) {
        this.jwtSecret = jwtSecret;
        this.userRepository = userRepository;
        this.storageRepository = storageRepository;
    }

    public LoginData login(String email, String password) throws AuthException {
        var user = userRepository.findByEmail(email);
        if (user == null || !BcryptUtil.matches(password, user.password)) {
            throw new AuthException("invalid email or password");
        }

        var expiresAt = Instant.now().plusSeconds(60 * 60 * 24);
        var key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        var token = Jwts.builder()
                .signWith(key)
                .subject(user.id.toString())
                .expiration(Date.from(expiresAt))
                .compact();

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
        var key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        var parser = Jwts.parser()
                .verifyWith(key)
                .build();
        var cleanToken = token.replace("Bearer ", "");
        try {
            var claims = parser.parseSignedClaims(cleanToken);
            var subject = claims.getPayload().getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                throw new AuthException("invalid token");
            }
            var user = userRepository.findById(UUID.fromString(subject));
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
