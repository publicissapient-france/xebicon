package fr.xebia.xebicon.api;

import android.content.Context;
import android.provider.Settings.Secure;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import fr.xebia.xebicon.model.Vote;

import static android.provider.Settings.Secure.ANDROID_ID;

public class ParseVoteApi implements VoteApi {

    private final DatabaseReference firebaseReference;
    private final Context context;

    public ParseVoteApi(Context context) {
        this.context = context;
        firebaseReference = FirebaseDatabase.getInstance().getReference("xebicon");
    }

    @Override
    public void sendRating(Vote vote) {
        String userId = Secure.getString(context.getContentResolver(), ANDROID_ID);

        Map<String, Object> parseVote = new HashMap<>();
        parseVote.put("user", userId);
        parseVote.put("talk", vote.getTalkId());
        parseVote.put("rate", vote.getRate());
        parseVote.put("revelent", vote.getRevelent());
        parseVote.put("content", vote.getContent());
        parseVote.put("speakers", vote.getSpeakers());
        parseVote.put("comment", vote.getComment());

        firebaseReference.child(vote.getTalkId()).child(userId).setValue(parseVote);
    }
}
