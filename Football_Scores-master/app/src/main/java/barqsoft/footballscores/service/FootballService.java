package barqsoft.footballscores.service;

import com.google.gson.JsonElement;

import barqsoft.footballscores.model.Team;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Anton on 7/23/2015.
 */
public interface FootballService {

    @GET("/teams/{id}")
    @Headers("X-Auth-Token: fb4b1a45fbea4a8eb42fbcecbc429ffa")
    Team getTeamInfo(@Path("id") String sort);

    @GET("/fixtures/")
    @Headers("X-Auth-Token: fb4b1a45fbea4a8eb42fbcecbc429ffa")
    JsonElement getFixtures(@Query("timeFrameStart") String start, @Query("timeFrameEnd") String end);
}
