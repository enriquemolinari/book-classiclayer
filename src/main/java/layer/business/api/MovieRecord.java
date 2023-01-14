package layer.business.api;

public record MovieRecord(Long id, String name, String duration, String plot,
    String coverImage, Iterable<MovieCastRecord> cast) {
}
