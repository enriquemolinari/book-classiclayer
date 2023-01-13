package layer.data.api;

import java.util.List;

public record FullMovieData(ShortMovieData shortMovie,
    List<MovieCastData> casts) {

}
