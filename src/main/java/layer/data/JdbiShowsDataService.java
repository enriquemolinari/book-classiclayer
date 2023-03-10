package layer.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import layer.data.api.DataException;
import layer.data.api.PlayingData;
import layer.data.api.SeatData;
import layer.data.api.ShowConfirmed;
import layer.data.api.ShowData;
import layer.data.api.ShowsDataService;

public class JdbiShowsDataService implements ShowsDataService {

  private static final int MINUTES_TO_KEEP_RESERVATION = 5;
  private Jdbi jdbi;

  public JdbiShowsDataService(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public List<PlayingData> playingNow(LocalDateTime showsUntil) {
    return jdbi.withHandle(handle -> {
      var playingNow = handle.createQuery(
          "select s.id_show, s.price, m.id_movie, m.name, m.duration, m.id_cover_image, "
              + "s.start_time, t.name as tname "
              + "from show s, movie m, theatre t "
              + "where s.id_movie = m.id_movie "
              + "and t.id_theatre = s.id_theatre "
              + "and s.start_time <= :until order by m.id_movie")
          .bind("until", showsUntil).mapToMap().list();

      var movieIds = playingNow.stream()
          .map(p -> Long.valueOf(p.get("id_movie").toString()))
          .collect(Collectors.toUnmodifiableSet());

      var genres = handle.createQuery(
          "select id_movie, GROUP_CONCAT(description separator ',') as desc "
              + "from movie_genre mg, genre g "
              + "where mg.id_genre = g.id_genre "
              + "and id_movie in (<idmovies>) " + " group by id_movie")
          .bindList("idmovies", movieIds).mapToMap().list();

      var gens = genres.stream()
          .map(g -> Map.of(Long.valueOf(g.get("id_movie").toString()),
              Arrays.asList(g.get("desc").toString().split(","))))
          .collect(Collectors.toUnmodifiableList());

      return playingNow.stream().map(l -> {

        var idm = Long.valueOf(l.get("id_movie").toString());

        var gensForM = gens.stream().filter(g -> g.containsKey(idm))
            .collect(Collectors.toUnmodifiableList());

        return new PlayingData(Long.valueOf(l.get("id_show").toString()),
            new ToLocalDate(l.get("start_time")).val(),
            Long.valueOf(l.get("id_movie").toString()),
            l.get("name").toString(),
            Integer.valueOf(l.get("duration").toString()),
            l.get("id_cover_image").toString(), gensForM.get(0).get(idm),
            l.get("tname").toString(),
            Float.valueOf(l.get("price").toString()));
      }).collect(Collectors.toUnmodifiableList());

    });
  }

  @Override
  public ShowData show(Long idShow) {
    return jdbi.withHandle(handle -> {
      var show = handle.createQuery(
          "select m.name, m.id_cover_image, m.duration, t.id_theatre, t.name as tname"
              + ", s.start_time, s.price, "
              + "b.reserved, b.reserved_until, b.confirmed, b.id_seat, se.number "
              + " from show s, booking b, seat se, movie m, theatre t "
              + " where s.id_show = :idshow and m.id_movie = s.id_movie"
              + "  and s.id_show = b.id_show"
              + "  and s.id_theatre = se.id_theatre"
              + "  and se.id_theatre = t.id_theatre"
              + "  and b.id_seat = se.id_seat")
          .bind("idshow", idShow).mapToMap().list();

      var seats = new ArrayList<SeatData>();
      var movieName = show.get(0).get("name").toString();
      var coverImage = show.get(0).get("id_cover_image").toString();
      var movieDuration =
          Integer.valueOf(show.get(0).get("duration").toString());
      var startTime = new ToLocalDate(show.get(0).get("start_time")).val();
      var idTheatre = Long.valueOf(show.get(0).get("id_theatre").toString());
      var theatreName = show.get(0).get("tname").toString();
      var price = (BigDecimal) show.get(0).get("price");

      for (Map<String, Object> map : show) {
        seats.add(new SeatData(Long.valueOf(map.get("id_seat").toString()),
            Integer.valueOf(map.get("number").toString()),
            Boolean.valueOf(map.get("reserved").toString()) == true
                && (LocalDateTime.now().isBefore(
                    new ToLocalDate(map.get("reserved_until")).val())),
            Boolean.valueOf(map.get("confirmed").toString())));
      }

      return new ShowData(idShow, startTime, movieName, movieDuration,
          coverImage, idTheatre, theatreName, seats, price.floatValue());
    });
  }

  @Override
  public void reserve(Long idShow, Long idUser, List<Long> idSeats) {
    jdbi.useTransaction(handle -> {
      var seatsChosen = handle.createQuery(
          "select id_show, id_seat, reserved, confirmed, reserved_until "
              + "from booking "
              + "where id_show = :idshow and id_seat in (<idseats>) for update")
          .bind("idshow", idShow).bindList("idseats", idSeats).mapToMap()
          .list();

      checkReservedOrConfirmed(seatsChosen);

      handle.createUpdate(
          "UPDATE booking SET id_user = :iduser, reserved = true, reserved_until = :until "
              + "where id_show = :idshow and id_seat in (<idseats>)")
          .bind("iduser", idUser).bind("idshow", idShow)
          .bind("until",
              LocalDateTime.now().plusMinutes(MINUTES_TO_KEEP_RESERVATION))
          .bindList("idseats", idSeats).execute();
    });
  }

  @Override
  public boolean isReservedBy(Long idShow, Long idUser, List<Long> idSeats) {
    return jdbi.withHandle(handle -> {
      var seatsChosen = handle.createQuery(
          "select id_show, id_user, id_seat, reserved, confirmed, reserved_until "
              + "from booking "
              + "where id_show = :idshow and id_seat in (<idseats>)")
          .bind("idshow", idShow).bindList("idseats", idSeats).mapToMap()
          .list();

      if (seatsChosen.size() == 0) {
        return false;
      }

      return reservedByUser(seatsChosen, idUser);
    });
  }

  private void checkReservedOrConfirmed(List<Map<String, Object>> seatsChosen) {
    if (seatsChosen.stream().anyMatch(m -> {
      return (new ToBoolean(m.get("reserved")).val() == true && LocalDateTime
          .now().isBefore(new ToLocalDate(m.get("reserved_until")).val()))
          || new ToBoolean(m.get("confirmed")).val() == true;
    })) {
      throw new DataException(
          "At least one of the selected seats has just been reserved");
    }
  }

  private boolean reservedByUser(List<Map<String, Object>> seatsChosen,
      Long idUser) {
    return seatsChosen.stream().allMatch(m -> {
      var idUserdb = m.get("id_user");
      if (idUserdb == null) {
        return false;
      }
      return idUser.equals(Long.valueOf((Integer) idUserdb))
          && new ToBoolean(m.get("reserved")).val() == true
          && new ToBoolean(m.get("confirmed")).val() == false && LocalDateTime
              .now().isBefore(new ToLocalDate(m.get("reserved_until")).val());
    });
  }

  @Override
  public ShowConfirmed confirm(Long idShow, Long idUser, List<Long> idSeats,
      float totalAmount, int newPoints) {
    return jdbi.inTransaction(handle -> {
      var seatsChosen = handle.createQuery(
          "select id_show, id_user, id_seat, reserved, confirmed, reserved_until "
              + "from booking "
              + "where id_show = :idshow and id_seat in (<idseats>) for update")
          .bind("idshow", idShow).bindList("idseats", idSeats).mapToMap()
          .list();

      if (!reservedByUser(seatsChosen, idUser)) {
        throw new DataException("You are not allowed to confirm the seats");
      }

      // confirm all the seats
      handle
          .createUpdate("UPDATE booking SET confirmed = true "
              + "where id_show = :idshow and id_seat in (<idseats>)")
          .bind("idshow", idShow).bindList("idseats", idSeats).execute();

      // calculate points earned by the user
      handle
          .createUpdate("UPDATE users SET points = points + :points "
              + "where id_user = :iduser")
          .bind("iduser", idUser).bind("points", newPoints).execute();

      // create the sale
      var timePayed = LocalDateTime.now();
      var saleId = handle
          .createUpdate("INSERT INTO sale(id_show, amount, id_user, payed_at) "
              + "values(:idshow, :amount, :iduser, :date)")
          .bind("idshow", idShow).bind("amount", totalAmount)
          .bind("iduser", idUser).bind("date", timePayed)
          .executeAndReturnGeneratedKeys().mapTo(Long.class).one();

      return new ShowConfirmed(saleId, timePayed);
    });
  }
}
