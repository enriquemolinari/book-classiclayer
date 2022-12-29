package ar.main;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultBearing;
import org.jdbi.v3.core.statement.Update;

public class SetUpDatabase {

  public void setUp() {
    Jdbi jdbi = Jdbi.create("jdbc:derby:memory:cinemacenter;create=true");

    jdbi.useHandle(handle -> {

      handle.execute("CREATE TABLE movie (id_movie INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "name VARCHAR(255), duration INT)");

      handle.execute("CREATE TABLE theatre (id_theatre INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "name VARCHAR(255))");

      handle.execute("CREATE TABLE show (id_show INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_movie INT not null, " + "id_theatre INT not null, "
          + "start_time timestamp not null, "
          + "foreign key (id_movie) references movie, "
          + "foreign key (id_theatre) references theatre)");

      handle.execute("CREATE TABLE seat (id_seat INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_theatre INT not null, number INT not null, "
          + "foreign key (id_theatre) references theatre)");

      handle.execute("CREATE TABLE person (id_person INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "name VARCHAR(255), surname VARCHAR(255), "
          + "email VARCHAR(255))");

      handle.execute("CREATE TABLE movie_cast (id_movie_cast INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_person INT not null, character_name VARCHAR(255), "
          + "foreign key (id_person) references person)");

      handle.execute("CREATE TABLE users (id_user INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "username VARCHAR(255), password VARCHAR(255), "
          + "id_person INT not null, "
          + "points INT not null, "          
          + "foreign key (id_person) references person)");

      handle.execute("CREATE TABLE users_audit (id_user_audit INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "login_date timestamp not null, id_user INT not null, "
          + "foreign key (id_user) references users)");

      handle.execute("CREATE TABLE booking (id_booking INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_show INT not null, id_seat INT not null, "
          + "foreign key (id_show) references show, "
          + "foreign key (id_seat) references seat)");

      handle.execute(
          "CREATE TABLE ranking_detail (id_ranking_detail INT NOT NULL "
              + "primary key generated always as identity (start with 1,increment by 1), "
              + "id_movie INT not null, id_user INT not null, "
              + "value decimal not null, "
              + "foreign key (id_movie) references movie, "
              + "foreign key (id_user) references users)");

      handle.execute("CREATE TABLE ranking (id_ranking INT NOT NULL "
          + "primary key generated always as identity (start with 1,increment by 1), "
          + "id_movie INT not null, value decimal not null, "
          + "foreign key (id_movie) references movie)");

      Update updateSchool = handle.createUpdate("INSERT INTO movie (name, duration) VALUES (?, ?)")
       .bind(0, "School of Rock")
       .bind(1, "109");

      ResultBearing schoolMovie = updateSchool.executeAndReturnGeneratedKeys();
      
      Update updateBigFish = handle.createUpdate("INSERT INTO movie (name, duration) VALUES (?, ?)")
      .bind(0, "Big Fish")
      .bind(1, "125");
      
      ResultBearing bigFishMovie = updateBigFish.executeAndReturnGeneratedKeys();
   
      Update updateJack = handle.createUpdate("INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
      .bind(0, "Jack")
      .bind(1, "Black")
      .bind(2, "jack.black@mymovies.com");      

      ResultBearing jackBlack = updateJack.executeAndReturnGeneratedKeys();

      
      handle.createUpdate("INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
      .bind(0, "Mike")
      .bind(1, "White")
      .bind(2, "mike.white@mymovies.com")      
      .execute();

      handle.createUpdate("INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
      .bind(0, "Ewan")
      .bind(1, "McGregor")
      .bind(2, "ewan.mcgregor@mymovies.com")      
      .execute();

      handle.createUpdate("INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
      .bind(0, "Albert")
      .bind(1, "Finney")
      .bind(2, "albert.finney@mymovies.com")      
      .execute();
      
      handle.createUpdate("INSERT INTO person (name, surname, email) VALUES (?, ?, ?)")
      .bind(0, "Enrique")
      .bind(1, "Molinari")
      .bind(2, "enrique.molinari@gmail.com")      
      .execute();
      
      
      
    });

    // List<User> users = jdbi.withHandle(handle -> {
    // return handle.createQuery("SELECT * FROM movie")
    // .map((rs, ctx) -> new User(rs.getInt("id"), rs.getString("name")))
    // .list();
    // });
    //
    // for (User user : users) {
    // System.out.println(user);
    // }


  }
}
