package uz.poster.integration.notification.otp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.poster.integration.notification.otp.entity.OtpMessage;

import java.util.List;

@Repository
public interface OtpMessageRepository extends JpaRepository<OtpMessage, String> {

    @Query("select otm from OtpMessage otm where otm.messageId in :messageIds")
    List<OtpMessage> findOtpMessagesByMessageIds(List<String> messageIds);
}
