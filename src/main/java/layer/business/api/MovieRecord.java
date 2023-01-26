package layer.business.api;

public record MovieRecord(Long id, String name, String duration, String plot,
    String coverImg, Iterable<String> genres, Iterable<MovieCastRecord> cast,
    String releaseDate, int ageRestriction, RatingRecord rating, int year,
    String directorName) {
}
