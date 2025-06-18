package business.service;

import dao.interfaces.GuideDAO;
import model.user.Guide;
import model.trip.Trip;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GuideService {
    private final GuideDAO guideDAO;

    public GuideService(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void addGuide(Guide guide) {
        guideDAO.save(guide);
    }

    public Guide getGuideById(int id) {
        return guideDAO.findById(id);
    }

    public void updateGuide(Guide guide) {
        guideDAO.update(guide);
    }

    public void deleteGuide(int id) {
        guideDAO.delete(id);
    }

    /**
     * Ottiene tutti i viaggi assegnati a una guida.
     * @param guide La guida di cui ottenere i viaggi assegnati
     * @return Lista di viaggi assegnati alla guida
     */
    public List<Trip> getAssignedTrips(Guide guide) {
        return guideDAO.getAssignedTrips(guide.getGuideId());
    }

    /**
     * Ottiene i viaggi futuri assegnati a una guida.
     * @param guide La guida di cui ottenere i viaggi futuri
     * @return Lista di viaggi futuri assegnati alla guida
     */
    public List<Trip> getUpcomingAssignedTrips(Guide guide) {
        LocalDate today = LocalDate.now();
        return getAssignedTrips(guide).stream()
                .filter(trip -> trip.getDate().isAfter(today) || trip.getDate().isEqual(today))
                .collect(Collectors.toList());
    }

    /**
     * Ottiene i viaggi passati assegnati a una guida.
     * @param guide La guida di cui ottenere i viaggi passati
     * @return Lista di viaggi passati assegnati alla guida
     */
    public List<Trip> getPastAssignedTrips(Guide guide) {
        LocalDate today = LocalDate.now();
        return getAssignedTrips(guide).stream()
                .filter(trip -> trip.getDate().isBefore(today))
                .collect(Collectors.toList());
    }
}
