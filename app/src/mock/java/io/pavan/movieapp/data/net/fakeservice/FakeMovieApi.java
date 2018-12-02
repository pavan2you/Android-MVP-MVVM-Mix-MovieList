package io.pavan.movieapp.data.net.fakeservice;

import com.google.gson.Gson;

import io.pavan.movieapp.arch.data.net.ApiCallback;
import io.pavan.movieapp.data.model.MovieList;
import io.pavan.movieapp.data.net.IMovieApi;

/**
 * Created by pavan on 02/12/18
 */
public class FakeMovieApi implements IMovieApi {

    public static String sJson = "{\"movies\":[{\"Title\":\"Avengers:Age of Ultron \",\"Year\":\"2015 \",\"Rated\":\"PG-13 \",\"Released\":\"01 May 2015 \",\"Runtime\":\"141 min \",\"Genre\":\"Action, Adventure, Sci-Fi \",\"Director\":\"Joss Whedon \",\"Writer\":\"Joss Whedon, Stan Lee (based on the Marvel comics by), Jack Kirby (based on the Marvel comics by), Joe Simon (character created by: Captain America), Jack Kirby (character created by: Captain America), Jim Starlin (character created by: Thanos) \",\"Actors\":\"Robert Downey Jr., Chris Hemsworth, Mark Ruffalo, Chris Evans \",\"Plot\":\"When Tony Stark and Bruce Banner try to jump-start a dormant peacekeeping program called Ultron, things go horribly wrong and it's up to Earth's mightiest heroes to stop the villainous Ultron from enacting his terrible plan. \",\"Language\":\"English, Korean \",\"Country\":\"USA \",\"Awards\":\"7 wins & 45 nominations.\"},{\"Title\":\"TheAvengers \",\"Year\":\"2012 \",\"Rated\":\"PG-13 \",\"Genre\":\"Action, Adventure, Sci-Fi \",\"Actors\":\"Robert Downey Jr., Chris Evans, Mark Ruffalo, Chris Hemsworth \",\"Plot\":\"Earth's mightiest heroes must come together and learn to fight as a team if they are going to stop the mischievous Loki and his alien army from enslaving humanity. \",\"Language\":\"English, Russian, Hindi \",\"Country\":\"USA \",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNDYxNjQyMjAtNTdiOS00NGYwLWFmNTAtNThmYjU5ZGI2YTI1XkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg \",\"Released\":\"04 May 2012 \",\"Runtime\":\"143 min \",\"Director\":\"Joss Whedon \",\"Writer\":\"Joss Whedon (screenplay), Zak Penn (story), Joss Whedon (story) \",\"Awards\":\"Nominated for 1 Oscar. Another 38 wins & 79 nominations. \"}]}";

    @Override
    public void getMovieList(ApiCallback<MovieList> callback) {
        Gson gson = new Gson();
        MovieList response = gson.fromJson(sJson, MovieList.class);
        callback.onResult(response);
    }
}
