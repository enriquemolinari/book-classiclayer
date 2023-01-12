package layer.data.api;

import java.util.List;

public interface MoviesData {

  List<ShortMovieRecord> allMovies();

  FullMovieRecord movieDetail(Long idMovie);

}
