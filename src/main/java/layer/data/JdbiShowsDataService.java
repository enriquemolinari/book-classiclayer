package layer.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import layer.data.api.DataException;
import layer.data.api.PlayingData;
import layer.data.api.SeatData;
import layer.data.api.ShowData;
import layer.data.api.ShowsDataService;

public class JdbiShowsDataService implements ShowsDataService {

  private static final int MINUTES_TO_KEEP_RESERVATION = 15;
  private Jdbi jdbi;

  public JdbiShowsDataService(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public List<PlayingData> playingNow(LocalDateTime showsUntil) {
    return jdbi.withHandle(handle -> {
      var playingNow = handle.createQuery(
          "select s.id_show, m.name, m.duration, s.start_time, m.id_cover_image, t.name as tname "
              + "from show s, movie m, theatre t "
              + "where s.id_movie = m.id_movie "
              + "and t.id_theatre = s.id_theatre "
              + "and s.start_time <= :until")
          .bind("until", showsUntil).mapToMap().list();

      return playingNow.stream()
          .map(l -> new PlayingData(Long.valueOf(l.get("id_show").toString()),
              new ToLocalDate(l.get("start_time")).val(),
              l.get("name").toString(),
              Integer.valueOf(l.get("duration").toString()),
              l.get("id_cover_image").toString(), l.get("tname").toString()))
          .collect(Collectors.toUnmodifiableList());
    });
  }

  @Override
  public ShowData show(Long idShow) {
    return jdbi.withHandle(handle -> {
      var show = handle.createQuery(
          "select m.name, m.id_cover_image, m.duration, t.idtheatre, t.name as tname, s.start_time, "
              + "b.reserved, b.confirmed, b.id_seat, se.number "
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

      for (Map<String, Object> map : show) {
        seats.add(new SeatData(Long.valueOf(map.get("id_seat").toString()),
            Integer.valueOf(map.get("number").toString()),
            Boolean.valueOf(map.get("reserved").toString()),
            Boolean.valueOf(map.get("confirmed").toString())));
      }

      return new ShowData(idShow, startTime, movieName, movieDuration,
          coverImage, idTheatre, theatreName, seats);
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

  @Override
  public void confirm(Long idShow, Long idUser, List<Long> idSeats) {
    jdbi.useTransaction(handle -> {
      var seatsChosen = handle.createQuery(
          "select id_show, id_user, id_seat, reserved, confirmed, reserved_until "
              + "from booking "
              + "where id_show = :idshow and id_seat in (<idseats>) for update")
          .bind("idshow", idShow).bindList("idseats", idSeats).mapToMap()
          .list();

      if (!seatsChosen.stream().allMatch(m -> {
        return idUser.equals(Long.valueOf((Integer) m.get("id_user")))
            && new ToBoolean(m.get("reserved")).val() == true
            && new ToBoolean(m.get("confirmed")).val() == false && LocalDateTime
                .now().isBefore(new ToLocalDate(m.get("reserved_until")).val());
      })) {
        throw new DataException("You are not allowed to confirm the seats");
      }

      handle
          .createUpdate("UPDATE booking SET confirmed = true "
              + "where id_show = :idshow and id_seat in (<idseats>)")
          .bind("idshow", idShow).bindList("idseats", idSeats).execute();
    });
  }
}
