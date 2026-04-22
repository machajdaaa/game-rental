package cz.gamerental.service;

import cz.gamerental.model.Notification;
import cz.gamerental.model.User;
import cz.gamerental.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setSentAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public List<Notification> findByUser(User user) {
        return notificationRepository.findByUserId(user.getId());
    }

    public List<Notification> findUnreadByUser(User user) {
        return notificationRepository.findByUserIdAndReadFalse(user.getId());
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Oznámení nebylo nalezeno"));

        notification.setRead(true);

        return notificationRepository.save(notification);
    }
}
