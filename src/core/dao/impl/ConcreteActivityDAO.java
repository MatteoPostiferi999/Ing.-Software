package core.dao.impl;

import core.dao.interfaces.ActivityDAO;
import core.model.Activity;
import core.model.Trip;

import java.util.*;

public class ConcreteActivityDAO implements ActivityDAO {
    @Override
    public List<Activity> getAllByTrip(Trip trip) {
        return new ArrayList<>();
    }

    @Override
    public void addToTrip(Trip trip, Activity activity) {
        // implementazione vuota per ora
    }

    @Override
    public void removeFromTrip(Trip trip, String activityName) {
        // implementazione vuota per ora
    }
}
