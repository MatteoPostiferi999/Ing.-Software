package business.service;

import dao.interfaces.UserDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TravelerDAO;
import model.user.User;
import model.user.Guide;
import model.user.Traveler;

/**
 * Servizio per la gestione degli utenti, inclusi registrazione, login e gestione dei profili.
 */
public class UserService {

    private final UserDAO userDAO;
    private GuideDAO guideDAO;
    private TravelerDAO travelerDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserService(UserDAO userDAO, GuideDAO guideDAO, TravelerDAO travelerDAO) {
        this.userDAO = userDAO;
        this.guideDAO = guideDAO;
        this.travelerDAO = travelerDAO;
    }

    public void setGuideDAO(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void setTravelerDAO(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    /**
     * Registra un nuovo utente nel sistema.
     * @param user L'utente da registrare
     * @return true se la registrazione è avvenuta con successo, false altrimenti
     */
    public boolean register(User user) {
        if (emailExists(user.getEmail())) {
            return false;
        }
        userDAO.save(user);
        return true;
    }

    /**
     * Effettua il login di un utente.
     * @param email Email dell'utente
     * @param password Password dell'utente
     * @return L'utente se il login è avvenuto con successo, null altrimenti
     */
    public User login(String email, String password) {
        User user = userDAO.findByEmailAndPassword(email, password);
        return user;
    }

    /**
     * Carica un utente completo con i suoi profili di guida e viaggiatore.
     * @param userId ID dell'utente da caricare
     * @return L'utente completo con i suoi profili
     */
    public User getUserById(int userId) {
        User user = userDAO.findById(userId);
        return user;
    }

    /**
     * Carica un utente tramite email.
     * @param email Email dell'utente
     * @return L'utente se trovato, null altrimenti
     */
    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    /**
     * Aggiorna i dati di un utente.
     * @param user L'utente con i dati aggiornati
     */
    public void updateUser(User user) {
        userDAO.update(user);
    }

    /**
     * Verifica se l'utente ha un profilo guida attivo.
     * @param user L'utente da verificare
     * @return true se l'utente ha un profilo guida, false altrimenti
     */
    public boolean hasGuideProfile(User user) {
        if (user == null) return false;

        Guide guide = user.getGuideProfile();
        if (guide == null && guideDAO != null) {
            // Se il profilo non è caricato, proviamo a caricarlo
            guide = guideDAO.findByUserId(user.getUserId());
            if (guide != null) {
                user.setGuideProfile(guide);
                guide.setOwner(user);
            }
        }

        return guide != null;
    }

    /**
     * Verifica se l'utente ha un profilo viaggiatore attivo.
     * @param user L'utente da verificare
     * @return true se l'utente ha un profilo viaggiatore, false altrimenti
     */
    public boolean hasTravelerProfile(User user) {
        if (user == null) return false;

        Traveler traveler = user.getTravelerProfile();
        if (traveler == null && travelerDAO != null) {
            // Se il profilo non è caricato, proviamo a caricarlo
            traveler = travelerDAO.findByUserId(user.getUserId());
            if (traveler != null) {
                user.setTravelerProfile(traveler);
                traveler.setOwner(user);
            }
        }

        return traveler != null;
    }

    /**
     * Ottiene il profilo guida di un utente.
     * @param user L'utente di cui ottenere il profilo guida
     * @return Il profilo guida, o null se non esiste
     */
    public Guide getGuideProfile(User user) {
        if (hasGuideProfile(user)) {
            return user.getGuideProfile();
        }
        return null;
    }

    /**
     * Ottiene il profilo viaggiatore di un utente.
     * @param user L'utente di cui ottenere il profilo viaggiatore
     * @return Il profilo viaggiatore, o null se non esiste
     */
    public Traveler getTravelerProfile(User user) {
        if (hasTravelerProfile(user)) {
            return user.getTravelerProfile();
        }
        return null;
    }

    /**
     * Elimina un utente e i suoi profili dal sistema.
     * @param user L'utente da eliminare
     */
    public void deleteUser(User user) {
        if (user != null) {
            userDAO.delete(user);
        }
    }

    private boolean emailExists(String email) {
        return userDAO.findByEmail(email) != null;
    }
}
