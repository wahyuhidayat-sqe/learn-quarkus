package org.learn.quarkus.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.learn.quarkus.models.User;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class IUserRepository implements UserRepository {

    EntityManager em;

    public IUserRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<User> all(String q) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM User u");
        if (q != null && !q.isEmpty()) {
            queryStr.append(" WHERE u.name LIKE :q OR u.email LIKE :q");
        }
        var query = em.createQuery(queryStr.toString(), User.class);
        if (q != null && !q.isEmpty()) {
            query.setParameter("q", "%" + q + "%");
        }
        return query.getResultList();
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
