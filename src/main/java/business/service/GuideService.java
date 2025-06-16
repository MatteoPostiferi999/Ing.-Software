package business.service;

import dao.interfaces.GuideDAO;
import model.user.Guide;
import model.user.User;

public class GuideService {
    private final GuideDAO guideDAO;

    public GuideService(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    // Modifica del profilo della Guida
    public void editProfile(Guide guide, String newUserName, String newEmail, String newPassword) {
        User owner = guide.getOwner();
        owner.setUserName(newUserName);
        owner.setEmail(newEmail);
        owner.setPassword(newPassword);

        guideDAO.update(guide); // Persisti le modifiche nel database
    }
}
