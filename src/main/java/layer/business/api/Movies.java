package layer.business.api;

public interface Movies {

  Iterable<Movie> movies();

  Movie detail(Long id);
}
