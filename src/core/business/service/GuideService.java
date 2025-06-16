package core.business.service;

import core.dao.interfaces.GuideDAO;
import core.model.Guide;
import core.model.User;

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
