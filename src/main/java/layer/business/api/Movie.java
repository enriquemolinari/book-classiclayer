package layer.business.api;

public record Movie(Long id, String name, String duration, String plot,
    String coverImage, Iterable<MovieCast> cast) {
}
