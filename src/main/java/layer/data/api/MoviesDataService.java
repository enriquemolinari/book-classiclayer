package layer.data.api;

import java.util.List;

public interface MoviesDataService {

  List<ShortMovieData> allMovies();

  FullMovieData movieDetail(Long idMovie);

}
