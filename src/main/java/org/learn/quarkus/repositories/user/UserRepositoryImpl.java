package org.learn.quarkus.repositories.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import org.learn.quarkus.configs.Configs;
import org.learn.quarkus.models.primary.User;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {

    @Inject
    @PersistenceUnit(name = Configs.PRIMARY_DB, unitName = Configs.PRIMARY_DB)
    EntityManager em;

    @Override
    public List<User> all(String q) {
        if (q != null && !q.isEmpty()) {
            var query = "SELECT u FROM User u WHERE u.name LIKE :q OR u.email LIKE :q";
            return em.createQuery(query, User.class)
                    .setParameter("q", "%" + q + "%")
                    .getResultList();
        }
        return em.createQuery("SELECT p FROM User p", User.class).getResultList();
    }

    @Override
    @Transactional
    public User create(User user) {
        em.persist(user);
        return user;
    }

    @Override
    public User findByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User findById(UUID id) {
        return em.find(User.class, id);
    }

    @Override
    @Transactional
    public void updateUserPhoto(UUID userId, String photoUrl) {
        var user = em.find(User.class, userId);
        if (user != null) {
            user.setPhoto(photoUrl);
            em.merge(user);
        }
    }
}
