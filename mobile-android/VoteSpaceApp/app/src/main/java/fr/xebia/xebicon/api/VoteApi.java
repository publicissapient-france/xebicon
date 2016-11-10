package fr.xebia.xebicon.api;

import fr.xebia.xebicon.model.Vote;
import retrofit.client.Response;
import retrofit.http.Body;

public interface VoteApi {
    void sendRating(Vote vote);
}
