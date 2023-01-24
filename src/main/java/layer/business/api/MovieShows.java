package layer.business.api;

public record MovieShows(MovieRecord movie, Iterable<ShowTimeRecord> shows) {

}
