package fr.xebia.xebicon.api;

import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.IOException;

public interface VideoApi {
    PlaylistItemListResponse getVideos() throws IOException;
}
