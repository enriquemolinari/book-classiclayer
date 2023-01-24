package layer.data.api;

import java.util.List;

public record ShortMovieData(Long idMovie, String name, String plot,
    int duration, List<String> genres, String idCoverImage) {

}
