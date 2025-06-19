package unit.dao.fake;

import dao.impl.ConcreteApplicationDAO;

public class FakeConcreteApplicationDAO extends ConcreteApplicationDAO {

    public FakeConcreteApplicationDAO() {
        // Non chiama super() --> evita DBManager.getInstance()
        // Imposta i campi manualmente se necessario
        this.setGuideDAO(null); // o mock da fuori
        this.setTripDAO(null);  // o mock da fuori
    }

    // Se necessario, puoi anche overrideare altri metodi
}
