package dev.clinic.notificationservice.repositories;

import dev.clinic.notificationservice.models.entities.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findAllByUserId(Long userId);
    boolean existsByUserIdAndToken(Long userId, String token);
}
