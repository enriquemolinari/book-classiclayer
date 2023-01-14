package layer.business.api;

public interface Movies {

  Iterable<MovieRecord> movies();

  MovieRecord detail(Long id);
}
