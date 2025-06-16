package dao.interfaces;

import model.assignment.Assignment;

public interface AssignmentDAO {
    void save(Assignment assignment);
    void delete(Assignment assignment);
    // altri metodi se ti servono (es. getByTrip, getAll...)
}
