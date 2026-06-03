package com.paylogic.paywalletlite.repository.identity;

import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.AccountStatus;
import com.paylogic.paywalletlite.domain.identity.enums.RoleType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User save(User user) {
        if (user.getUserId() == null) {
            entityManager.persist(user);
            return user;
        }
        return entityManager.merge(user);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(entityManager.find(User.class, userId));
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.phoneNumber = :phone", User.class);
        query.setParameter("phone", phoneNumber);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByNationalIdNumber(String nationalIdNumber) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.nationalIdNumber = :nid", User.class);
        query.setParameter("nid", nationalIdNumber);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public List<User> findByStatus(AccountStatus status) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.status = :status", User.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<User> findByRole(RoleType role) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.role = :role", User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.phoneNumber = :phone", Long.class)
                .setParameter("phone", phoneNumber)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public void updateLastLogin(UUID userId) {
        entityManager.createQuery(
                        "UPDATE User u SET u.lastLogin = :now WHERE u.userId = :id")
                .setParameter("now", LocalDateTime.now())
                .setParameter("id", userId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void incrementFailedAttempts(UUID userId) {
        entityManager.createQuery(
                        "UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.userId = :id")
                .setParameter("id", userId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void resetFailedAttempts(UUID userId) {
        entityManager.createQuery(
                        "UPDATE User u SET u.failedLoginAttempts = 0, u.lockedUntil = null WHERE u.userId = :id")
                .setParameter("id", userId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void lockAccount(UUID userId, LocalDateTime lockedUntil) {
        entityManager.createQuery(
                        "UPDATE User u SET u.lockedUntil = :locked, u.status = :status WHERE u.userId = :id")
                .setParameter("locked", lockedUntil)
                .setParameter("status", AccountStatus.SUSPENDED)
                .setParameter("id", userId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateStatus(UUID userId, AccountStatus status) {
        entityManager.createQuery(
                        "UPDATE User u SET u.status = :status WHERE u.userId = :id")
                .setParameter("status", status)
                .setParameter("id", userId)
                .executeUpdate();
    }
}