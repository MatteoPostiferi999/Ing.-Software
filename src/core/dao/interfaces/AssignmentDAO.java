package core.dao.interfaces;

import core.model.Assignment;

public interface AssignmentDAO {
    void save(Assignment assignment);
    void delete(Assignment assignment);
    // altri metodi se ti servono (es. getByTrip, getAll...)
}
