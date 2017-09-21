package com.movies.movies;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
public class MovieController {

  static final String TOKEN = "?api_key=be2a38521a7859c95e2d73c48786e4bb";

  static final String API_URL = "https://api.themoviedb.org/3/movie";

  //To view the poster prepend poster_path with
  //http://image.tmdb.org/t/p/original

  static final String POSTER_PATH = "http://image.tmdb.org/t/p/original";

  // api documentation
  // https://developers.themoviedb.org/3/movies

  static final String POPULAR = "/popular";
  static final String NOW_PLAYING = "/now_playing";

  @RequestMapping(path = "/", method = RequestMethod.GET)
  public String home () {
    return "home";
  }

  @RequestMapping(path = "/now-playing", method = RequestMethod.GET)
  public String nowPlaying (Model model) {
    model.addAttribute("movies", getMovies(NOW_PLAYING));
    return "now-playing";
  }

  @RequestMapping(path = "/medium-popular-long-name", method = RequestMethod.GET)
  public String mediumPopular (Model model) {
    List<Movie> movies = getMovies(POPULAR).stream()
            .filter(e -> e.getPopularity() < 80.0 && e.getPopularity() > 30.0)
            .filter(e -> e.getTitle().length() > 9)
            .collect(Collectors.toList());
    model.addAttribute("movies", movies);
    return "medium-popular-long-name";
  }


  @RequestMapping(path = "/overview-mashup", method = RequestMethod.GET)
  public String overviewMashup (Model model) {
    String mashupString = "";

    Random random = new Random();

    //Functional Programming approach
    List<String> randomSentences = getMovies(NOW_PLAYING).stream()
            .limit(5)
            .map(e -> e.getOverview().split("\\."))
            .map(e -> e[random.nextInt(e.length)] + ". ")
            .collect(Collectors.toList());

    //Regular approach
        /*
        List<Movie> movies = getMovies(NOW_PLAYING).subList(0, 5);
        List<String> randomSentences = new ArrayList<>();
        for (Movie movie: movies) {
            String[] overviewSentences = movie.getOverview().split("\\.");
            randomSentences.add(overviewSentences[random.nextInt(overviewSentences.length)]);
        }
        */


    //(Common code used for both)
    Collections.sort(randomSentences, (e1, e2) -> random.nextInt(3) - 1);
    for (String s : randomSentences) {
      mashupString += s;
    }

    model.addAttribute("mashup", mashupString);
    return "overview-mashup";
  }

  public static List<Movie> getMovies (String route) {
    RestTemplate restTemplate = new RestTemplate();
    Results results = restTemplate.getForObject(API_URL + route + TOKEN, Results.class);
    return results.getResults();
  }
}