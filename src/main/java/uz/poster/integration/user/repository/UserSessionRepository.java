package uz.poster.integration.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.poster.integration.user.entity.UserSession;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    Optional<UserSession> findByIdAndUserId(String sessionId, String userId);

    Optional<UserSession> findSessionByUserIdAndDeviceId(String userId, String deviceId);
}
