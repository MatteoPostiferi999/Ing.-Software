package unit.business.service;

import business.service.NotificationService;
import business.service.ReviewService;
import model.review.Review;
import model.review.ReviewRegister;
import model.review.Reviewable;
import dao.interfaces.ReviewDAO;
import model.user.Traveler;
import model.notification.Notifiable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewDAO reviewDAO;

    @Mock
    private ReviewRegister reviewRegister;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Review review;

    @Mock
    private Reviewable reviewable;

    @Mock
    private Traveler author;

    @Mock
    private Reviewable notifiableTarget;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewService = new ReviewService(reviewDAO, reviewRegister, notificationService);
    }

    @Test
    @DisplayName("Costruttore senza NotificationService dovrebbe creare istanza")
    void testConstructorWithoutNotificationService() {
        ReviewService service = new ReviewService(reviewDAO, reviewRegister);
        assertNotNull(service);
    }

    @Test
    @DisplayName("Costruttore con NotificationService dovrebbe creare istanza")
    void testConstructorWithNotificationService() {
        ReviewService service = new ReviewService(reviewDAO, reviewRegister, notificationService);
        assertNotNull(service);
    }

    @Test
    @DisplayName("setNotificationService dovrebbe impostare il servizio notifiche")
    void testSetNotificationService() {
        ReviewService service = new ReviewService(reviewDAO, reviewRegister);
        service.setNotificationService(notificationService);
        // Test indiretto tramite createAndAddReview
        assertNotNull(service);
    }

    @Test
    @DisplayName("addReview dovrebbe aggiungere recensione al registro e salvarla")
    void testAddReview() {
        reviewService.addReview(review);

        verify(reviewRegister).addReview(review);
        verify(reviewDAO).save(review);
    }

    @Test
    @DisplayName("createAndAddReview dovrebbe creare, aggiungere e salvare la recensione")
    void testCreateAndAddReview() {
        int rating = 5;
        String comment = "Ottima esperienza";
        when(author.getUserName()).thenReturn("Mario Rossi");

        reviewService.createAndAddReview(rating, comment, reviewable, author);

        verify(reviewRegister).addReview(any(Review.class));
        verify(reviewDAO).save(any(Review.class));
    }

    @Test
    @DisplayName("createAndAddReview con target notificabile dovrebbe inviare notifica")
    void testCreateAndAddReviewWithNotifiableTarget() {
        int rating = 4;
        String comment = "Buona esperienza";
        String userName = "Giovanni Bianchi";

        // Creiamo un mock che implementa sia Reviewable che Notifiable
        ReviewableNotifiable target = mock(ReviewableNotifiable.class);

        when(author.getUserName()).thenReturn(userName);

        reviewService.createAndAddReview(rating, comment, target, author);

        verify(reviewRegister).addReview(any(Review.class));
        verify(reviewDAO).save(any(Review.class));
        verify(notificationService).sendNotification(eq(target), contains("Hai ricevuto una nuova recensione"));
        verify(notificationService).sendNotification(eq(target), contains(userName));
        verify(notificationService).sendNotification(eq(target), contains("4/5"));
    }

    @Test
    @DisplayName("createAndAddReview senza NotificationService non dovrebbe lanciare eccezioni")
    void testCreateAndAddReviewWithoutNotificationService() {
        ReviewService serviceWithoutNotification = new ReviewService(reviewDAO, reviewRegister);
        ReviewableNotifiable target = mock(ReviewableNotifiable.class);
        int rating = 3;
        String comment = "Esperienza media";

        when(author.getUserName()).thenReturn("Test User");

        assertDoesNotThrow(() ->
                serviceWithoutNotification.createAndAddReview(rating, comment, target, author)
        );

        verify(reviewRegister).addReview(any(Review.class));
        verify(reviewDAO).save(any(Review.class));
    }

    @Test
    @DisplayName("getReviewsByTarget dovrebbe restituire le recensioni dal DAO")
    void testGetReviewsByTarget() {
        List<Review> expectedReviews = Arrays.asList(review);
        when(reviewDAO.getByTarget(reviewable)).thenReturn(expectedReviews);

        List<Review> result = reviewService.getReviewsByTarget(reviewable);

        assertEquals(expectedReviews, result);
        verify(reviewDAO).getByTarget(reviewable);
    }

    @Test
    @DisplayName("getReviewsByAuthor dovrebbe restituire le recensioni dal DAO")
    void testGetReviewsByAuthor() {
        List<Review> expectedReviews = Arrays.asList(review);
        when(reviewDAO.getByAuthor(author)).thenReturn(expectedReviews);

        List<Review> result = reviewService.getReviewsByAuthor(author);

        assertEquals(expectedReviews, result);
        verify(reviewDAO).getByAuthor(author);
    }

    @Test
    @DisplayName("getReviewById dovrebbe restituire la recensione dal DAO")
    void testGetReviewById() {
        int reviewId = 123;
        when(reviewDAO.getById(reviewId)).thenReturn(review);

        Review result = reviewService.getReviewById(reviewId);

        assertEquals(review, result);
        verify(reviewDAO).getById(reviewId);
    }

    @Test
    @DisplayName("deleteReview dovrebbe rimuovere dal registro e dal DAO")
    void testDeleteReview() {
        int reviewId = 456;
        when(review.getReviewID()).thenReturn(reviewId);

        reviewService.deleteReview(review);

        verify(reviewRegister).removeReview(review);
        verify(reviewDAO).delete(reviewId);
    }

    @Test
    @DisplayName("updateReview dovrebbe aggiornare recensione esistente")
    void testUpdateReviewExists() {
        int reviewId = 789;
        int newRating = 4;
        String newText = "Recensione aggiornata";

        Review existingReview = mock(Review.class);
        when(reviewDAO.getById(reviewId)).thenReturn(existingReview);
        when(existingReview.getReviewID()).thenReturn(reviewId);

        List<Review> mockReviews = new ArrayList<>();
        Review registerReview = mock(Review.class);
        when(registerReview.getReviewID()).thenReturn(reviewId);
        mockReviews.add(registerReview);
        when(reviewRegister.getReviews()).thenReturn(mockReviews);

        reviewService.updateReview(reviewId, newRating, newText);

        verify(existingReview).setRating(newRating);
        verify(existingReview).setText(newText);
        verify(reviewDAO).save(existingReview);
        verify(registerReview).setRating(newRating);
        verify(registerReview).setText(newText);
    }

    @Test
    @DisplayName("updateReview con recensione inesistente non dovrebbe fare nulla")
    void testUpdateReviewNotExists() {
        int reviewId = 999;
        int newRating = 3;
        String newText = "Testo non applicato";

        when(reviewDAO.getById(reviewId)).thenReturn(null);

        reviewService.updateReview(reviewId, newRating, newText);

        verify(reviewDAO).getById(reviewId);
        verify(reviewDAO, never()).save(any(Review.class));
        verify(reviewRegister, never()).getReviews();
    }

    @Test
    @DisplayName("updateReview dovrebbe aggiornare solo la recensione corrispondente nel registro")
    void testUpdateReviewInRegisterOnlyCorrectOne() {
        int targetReviewId = 100;
        int otherReviewId = 200;
        int newRating = 5;
        String newText = "Aggiornamento specifico";

        Review existingReview = mock(Review.class);
        when(reviewDAO.getById(targetReviewId)).thenReturn(existingReview);

        // Setup del registro con multiple recensioni
        Review targetRegisterReview = mock(Review.class);
        Review otherRegisterReview = mock(Review.class);
        when(targetRegisterReview.getReviewID()).thenReturn(targetReviewId);
        when(otherRegisterReview.getReviewID()).thenReturn(otherReviewId);

        List<Review> mockReviews = Arrays.asList(otherRegisterReview, targetRegisterReview);
        when(reviewRegister.getReviews()).thenReturn(mockReviews);

        reviewService.updateReview(targetReviewId, newRating, newText);

        // Verifica che solo la recensione target sia stata aggiornata
        verify(targetRegisterReview).setRating(newRating);
        verify(targetRegisterReview).setText(newText);
        verify(otherRegisterReview, never()).setRating(anyInt());
        verify(otherRegisterReview, never()).setText(anyString());
    }

    @Test
    @DisplayName("getAverageRating dovrebbe restituire la media dal registro")
    void testGetAverageRating() {
        double expectedAverage = 4.2;
        when(reviewRegister.getAverageRating()).thenReturn(expectedAverage);

        double result = reviewService.getAverageRating();

        assertEquals(expectedAverage, result);
        verify(reviewRegister).getAverageRating();
    }

    @Test
    @DisplayName("getAllReviews dovrebbe restituire tutte le recensioni dal registro")
    void testGetAllReviews() {
        List<Review> expectedReviews = Arrays.asList(review);
        when(reviewRegister.getReviews()).thenReturn(expectedReviews);

        List<Review> result = reviewService.getAllReviews();

        assertEquals(expectedReviews, result);
        verify(reviewRegister).getReviews();
    }

    // Interfaccia helper per i test che combina Reviewable e Notifiable
    private interface ReviewableNotifiable extends Reviewable, Notifiable {
    }
}