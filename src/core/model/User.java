package core.model;

/**
 * Rappresenta un utente del sistema. Contiene i profili guida e viaggiatore.
 * Composizione: Guide e Traveler sono parte di User.
 */
public class User {
    private String userId;
    private String userName;
    private String email;
    private String password;

    private Guide guideProfile;
    private Traveler travelerProfile;

    public User() {
        this.guideProfile = new Guide(this);
        this.travelerProfile = new Traveler(this);
    }

    public User(String userId, String userName, String email, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;

        this.guideProfile = new Guide(this);
        this.travelerProfile = new Traveler(this);
    }

    // Getter e Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Guide getGuideProfile() {
        return guideProfile;
    }

    public Traveler getTravelerProfile() {
        return travelerProfile;
    }
}
