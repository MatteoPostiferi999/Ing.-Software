package business.controller;

import business.service.*;
import model.user.Guide;
import model.user.Skill;
import model.notification.Notification;
import model.trip.Trip;
import model.application.Application;
import model.application.ApplicationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuideController {
    private Guide guide;
    private ViewTripsService viewTripsService;
    private ApplicationService applicationService;
    private NotificationService notificationService;
    private GuideService guideService;

    public GuideController(Guide guide, ViewTripsService viewTripsService,
                          ApplicationService applicationService,
                          NotificationService notificationService,
                          GuideService guideService) {
        this.guide = guide;
        this.viewTripsService = viewTripsService;
        this.applicationService = applicationService;
        this.notificationService = notificationService;
        this.guideService = guideService;
    }

    /**
     * Visualizza i viaggi disponibili filtrati per le competenze della guida e il range di date.
     * @param minDate Data minima di inizio viaggio
     * @param maxDate Data massima di inizio viaggio
     * @return Lista dei viaggi che soddisfano i criteri
     */
    public List<Trip> viewAvailableTrips(LocalDate minDate, LocalDate maxDate) {
        viewTripsService.setStrategy(new GuideFilter(guide, minDate, maxDate));
        return viewTripsService.viewTrips();
    }

    /**
     * Visualizza i dettagli di un viaggio specifico.
     * @param tripId ID del viaggio
     * @return Il viaggio con tutti i dettagli o null se non trovato
     */
    public Trip viewTripDetails(int tripId) {
        return viewTripsService.viewTripDetails(tripId);
    }

    /**
     * Invia una candidatura per un viaggio.
     * @param trip Il viaggio a cui candidarsi
     * @param cv Curriculum vitae o informazioni aggiuntive
     * @return true se la candidatura è stata inviata con successo, false altrimenti
     */
    public boolean submitApplication(Trip trip, String cv) {
        try {
            applicationService.sendApplication(cv, guide, trip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ritira una candidatura per un viaggio.
     * @param trip Il viaggio per cui ritirare la candidatura
     * @return true se la candidatura è stata ritirata con successo, false altrimenti
     */
    public boolean withdrawApplication(Trip trip) {
        try {
            applicationService.withdrawApplication(guide, trip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ottiene tutte le candidature della guida con il loro stato attuale.
     * @return Lista di candidature
     */
    public List<Application> trackApplicationsStatus() {
        return applicationService.getApplicationsByGuide(guide);
    }

    /**
     * Ottiene le candidature raggruppate per stato.
     * @return Mappa con le candidature raggruppate per stato
     */
    public Map<ApplicationStatus, List<Application>> getApplicationsByStatus() {
        List<Application> applications = applicationService.getApplicationsByGuide(guide);
        return applications.stream()
                .collect(Collectors.groupingBy(Application::getStatus));
    }

    /**
     * Aggiorna le competenze della guida.
     * @param updatedSkills Lista aggiornata delle competenze
     * @return true se l'aggiornamento è andato a buon fine, false altrimenti
     */
    public boolean updateGuideSkills(List<Skill> updatedSkills) {
        try {
            guide.setSkills(updatedSkills);
            guideService.updateGuide(guide);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Aggiunge una nuova competenza alla guida.
     * @param skill La nuova competenza da aggiungere
     * @return true se l'aggiunta è andata a buon fine, false altrimenti
     */
    public boolean addSkill(Skill skill) {
        try {
            guide.addSkill(skill);
            guideService.updateGuide(guide);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Rimuove una competenza dalla guida.
     * @param skill La competenza da rimuovere
     * @return true se la rimozione è andata a buon fine, false altrimenti
     */
    public boolean removeSkill(Skill skill) {
        List<Skill> currentSkills = guide.getSkills();
        if (currentSkills.contains(skill)) {
            currentSkills.remove(skill);
            guide.setSkills(currentSkills);
            guideService.updateGuide(guide);
            return true;
        }
        return false;
    }

    /**
     * Ottiene tutte le notifiche della guida.
     * @return Lista delle notifiche
     */
    public List<Notification> getNotifications() {
        return notificationService.getNotificationsByRecipient(guide);
    }

    /**
     * Legge la prossima notifica non letta.
     * @return La prossima notifica non letta o null se non ce ne sono
     */
    public Notification readNextUnreadNotification() {
        return notificationService.getNextUnreadNotification(guide);
    }

    /**
     * Segna una notifica specifica come letta.
     * @param notificationId L'ID della notifica
     * @return true se la notifica è stata trovata e marcata come letta, false altrimenti
     */
    public boolean markNotificationAsRead(int notificationId) {
        return notificationService.markAsReadById(notificationId);
    }

    /**
     * Segna tutte le notifiche della guida come lette.
     */
    public void markAllNotificationsAsRead() {
        notificationService.markAllAsRead(guide);
    }

    /**
     * Ottiene tutti i viaggi a cui la guida è stata assegnata.
     * @return Lista dei viaggi assegnati
     */
    public List<Trip> getAssignedTrips() {
        return guideService.getAssignedTrips(guide);
    }

    /**
     * Ottiene i viaggi futuri a cui la guida è stata assegnata.
     * @return Lista dei viaggi futuri
     */
    public List<Trip> getUpcomingAssignedTrips() {
        return guideService.getUpcomingAssignedTrips(guide);
    }

    /**
     * Ottiene i viaggi passati a cui la guida è stata assegnata.
     * @return Lista dei viaggi passati
     */
    public List<Trip> getPastAssignedTrips() {
        return guideService.getPastAssignedTrips(guide);
    }
}
