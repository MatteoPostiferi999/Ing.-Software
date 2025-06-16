package dao.impl;

import dao.interfaces.ApplicationDAO;
import model.application.Application;
import model.application.ApplicationStatus;
import model.user.Guide;
import model.trip.Trip;

import java.util.List;

public class ConcreteApplicationDAO implements ApplicationDAO {

    @Override
    public Application getById(int id) {
        return null;
    }

    @Override
    public List<Application> getByTrip(Trip trip) {
        return null;
    }

    @Override
    public List<Application> getByGuide(Guide guide) {
        return null;
    }

    @Override
    public void save(Application application) {
    }

    @Override
    public void updateStatus(Application application, ApplicationStatus status) {
    }

    @Override
    public void delete(Application application) {
    }
}
