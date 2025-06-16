package core.dao.interfaces;

import core.model.Notification;
import java.util.List;

public interface NotificationDAO {
    void save(Notification notification);
    void update(Notification notification);  // per marcare come letta
    List<Notification> getByUserId(String userId); // per caricarle da DB
}
