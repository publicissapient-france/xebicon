package fr.xebia.xebicon.model;

public class Rating {

    public final long user;
    public final int rating;
    public final String talkId;

    public Rating(long user, int rating, String talkId) {
        this.user = user;
        this.rating = rating;
        this.talkId = talkId;
    }
}
