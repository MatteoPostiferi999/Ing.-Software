package business.controller;

import business.service.UserService;
import model.user.Guide;
import model.user.Traveler;
import model.user.User;

/**
 * Controller per la gestione delle operazioni utente, inclusi registrazione,
 * login e gestione dei profili.
 */
public class UserController {
    private UserService userService;
    private User currentUser;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registra un nuovo utente nel sistema.
     * @param name Nome utente
     * @param email Email
     * @param password Password
     * @return true se la registrazione ha successo, false altrimenti (es. email già esistente)
     */
    public boolean registerUser(String name, String email, String password) {
        User newUser = new User(name, email, password);
        boolean success = userService.register(newUser);
        if (success) {
            this.currentUser = newUser;
        }
        return success;
    }

    /**
     * Effettua il login di un utente.
     * @param email Email dell'utente
     * @param password Password dell'utente
     * @return true se il login ha successo, false altrimenti
     */
    public boolean login(String email, String password) {
        User user = userService.login(email, password);
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Restituisce il profilo viaggiatore dell'utente corrente.
     * Se non esiste, lo crea.
     * @return Il profilo viaggiatore
     */
    public Traveler getTravelerProfile() {
        if (currentUser == null) {
            return null;
        }

        Traveler travelerProfile = currentUser.getTravelerProfile();
        if (travelerProfile == null) {
            // Creazione del profilo Traveler se non esiste
            travelerProfile = new Traveler(currentUser);
            currentUser.setTravelerProfile(travelerProfile);
            userService.updateUser(currentUser);
        }
        return travelerProfile;
    }

    /**
     * Restituisce il profilo guida dell'utente corrente.
     * Se non esiste, lo crea.
     * @return Il profilo guida
     */
    public Guide getGuideProfile() {
        if (currentUser == null) {
            return null;
        }

        Guide guideProfile = currentUser.getGuideProfile();
        if (guideProfile == null) {
            // Creazione del profilo Guide se non esiste
            guideProfile = new Guide(currentUser);
            currentUser.setGuideProfile(guideProfile);
            userService.updateUser(currentUser);
        }
        return guideProfile;
    }

    /**
     * Restituisce true se l'utente ha già un profilo guida.
     * @return true se esiste il profilo guida, false altrimenti
     */
    public boolean hasGuideProfile() {
        return currentUser != null && userService.hasGuideProfile(currentUser);
    }

    /**
     * Restituisce true se l'utente ha già un profilo viaggiatore.
     * @return true se esiste il profilo viaggiatore, false altrimenti
     */
    public boolean hasTravelerProfile() {
        return currentUser != null && userService.hasTravelerProfile(currentUser);
    }

    /**
     * Effettua il logout dell'utente corrente.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Restituisce l'utente attualmente loggato.
     * @return L'utente corrente o null se nessun utente ha effettuato il login
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
