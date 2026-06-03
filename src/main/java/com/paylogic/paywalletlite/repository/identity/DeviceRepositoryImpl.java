package com.paylogic.paywalletlite.repository.identity;

import com.paylogic.paywalletlite.domain.identity.Device;
import com.paylogic.paywalletlite.domain.identity.enums.DeviceStatus;
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
public class DeviceRepositoryImpl implements DeviceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Device save(Device device) {
        if (device.getDeviceId() == null) {
            entityManager.persist(device);
            return device;
        }
        return entityManager.merge(device);
    }

    @Override
    public Optional<Device> findById(UUID deviceId) {
        return Optional.ofNullable(entityManager.find(Device.class, deviceId));
    }

    @Override
    public Optional<Device> findByHardwareId(String hardwareId) {
        TypedQuery<Device> query = entityManager.createQuery(
                "SELECT d FROM Device d WHERE d.hardwareId = :hwid", Device.class);
        query.setParameter("hwid", hardwareId);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Device> findByUserId(UUID userId) {
        return entityManager.createQuery(
                        "SELECT d FROM Device d WHERE d.userId = :uid", Device.class)  // 🔥 d.userId pas d.user.userId
                .setParameter("uid", userId)
                .getResultList();
    }

    @Override
    public List<Device> findByStatus(DeviceStatus status) {
        return entityManager.createQuery(
                        "SELECT d FROM Device d WHERE d.status = :status", Device.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Device> findPrimaryDevicesByUserId(UUID userId) {
        return entityManager.createQuery(
                        "SELECT d FROM Device d WHERE d.userId = :uid AND d.isPrimary = true",  // 🔥 d.userId
                        Device.class)
                .setParameter("uid", userId)
                .getResultList();
    }

    @Override
    public Optional<Device> findByUserIdAndHardwareId(UUID userId, String hardwareId) {
        TypedQuery<Device> query = entityManager.createQuery(
                "SELECT d FROM Device d WHERE d.userId = :uid AND d.hardwareId = :hwid",  // 🔥 d.userId
                Device.class);
        query.setParameter("uid", userId);
        query.setParameter("hwid", hardwareId);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void delete(Device device) {
        entityManager.remove(entityManager.contains(device) ? device : entityManager.merge(device));
    }

    @Override
    @Transactional
    public void updateStatus(UUID deviceId, DeviceStatus status) {
        entityManager.createQuery(
                        "UPDATE Device d SET d.status = :status WHERE d.deviceId = :id")
                .setParameter("status", status)
                .setParameter("id", deviceId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateLastSeen(UUID deviceId) {
        entityManager.createQuery(
                        "UPDATE Device d SET d.lastSeen = :now WHERE d.deviceId = :id")
                .setParameter("now", LocalDateTime.now())
                .setParameter("id", deviceId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void revokeAllDevicesExcept(UUID userId, UUID exceptDeviceId) {
        entityManager.createQuery(
                        "UPDATE Device d SET d.status = :status WHERE d.userId = :uid AND d.deviceId != :except")  // 🔥 d.userId
                .setParameter("status", DeviceStatus.REVOKED)
                .setParameter("uid", userId)
                .setParameter("except", exceptDeviceId)
                .executeUpdate();
    }
}