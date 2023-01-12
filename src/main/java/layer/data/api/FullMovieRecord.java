package layer.data.api;

import java.util.List;

public record FullMovieRecord(ShortMovieRecord shortMovie,
    List<MovieCastRecord> casts) {

}
