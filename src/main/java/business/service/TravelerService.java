package business.service;

import dao.interfaces.TravelerDAO;
import model.user.Traveler;
import model.user.User;

public class TravelerService {
    private final TravelerDAO travelerDAO;

    public TravelerService(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    public void addTraveler(Traveler traveler) {
        travelerDAO.save(traveler);
    }

    public Traveler getTravelerById(int id) {
        return travelerDAO.findById(id);
    }

    public void updateTraveler(Traveler traveler) {
        travelerDAO.update(traveler);
    }

    public void deleteTraveler(int id) {
        travelerDAO.delete(id);
    }
}
