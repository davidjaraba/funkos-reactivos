package dev.services.notification;

import dev.database.models.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class FunkoNotificationServiceImpl implements FunkoNotificationService {
    private static FunkoNotificationServiceImpl instance;
    private final Flux<Notification> notificationsFlux;
    private FluxSink<Notification> notifications;

    public FunkoNotificationServiceImpl() {
        notificationsFlux = Flux.<Notification>create(emitter -> notifications = emitter).share();
    }

    public synchronized static FunkoNotificationServiceImpl getInstance() {
        if (instance == null) {
            instance = new FunkoNotificationServiceImpl();
        }
        return instance;
    }

    @Override
    public Flux<Notification> getNotifications() {
        return notificationsFlux;
    }

    @Override
    public void notify(Notification notification) {
        notifications.next(notification);
    }
}
