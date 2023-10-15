package dev.services.notification;

import dev.database.models.Notification;
import reactor.core.publisher.Flux;

public interface FunkoNotificationService {
    Flux<Notification> getNotifications();
    void notify(Notification notification);
}
