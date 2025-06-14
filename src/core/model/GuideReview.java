package core.model;
// GuideReview.java
// package core.model;

/**
 * Recensione dedicata a una Guide.
 */
public class GuideReview extends Review {
    private int guideId;

    public GuideReview(int idReview, int rating, String text, int guideId) {
        super(idReview, rating, text);
        this.guideId = guideId;
    }

    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }
}