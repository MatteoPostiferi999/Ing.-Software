package core.business.service;

import core.dao.interfaces.TravelerDAO;
import core.model.Traveler;
import core.model.User;

public class TravelerService {
    private final TravelerDAO travelerDAO;

    public TravelerService(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    // Modifica del profilo del Traveler
    public void editProfile(Traveler traveler, String newUserName, String newEmail, String newPassword) {
        User owner = traveler.getOwner();
        owner.setUserName(newUserName);
        owner.setEmail(newEmail);
        owner.setPassword(newPassword);

        travelerDAO.update(traveler); // Persisti le modifiche nel database
    }
}
