package business.service;

import dao.interfaces.TravelerDAO;
import model.user.Traveler;
import model.user.User;

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
