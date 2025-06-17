package business.service;

import dao.interfaces.GuideDAO;
import model.user.Guide;

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
}
