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
    //todo place your token here
    String token = "YOUR_TOKEN_HERE";

    @GET("/teams/{id}")
    @Headers("X-Auth-Token: " + token)
    Team getTeamInfo(@Path("id") String sort);

    @GET("/fixtures/")
    @Headers("X-Auth-Token: " + token)
    JsonElement getFixtures(@Query("timeFrameStart") String start, @Query("timeFrameEnd") String end);
}
