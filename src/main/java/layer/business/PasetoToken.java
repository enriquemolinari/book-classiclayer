package layer.business;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import dev.paseto.jpaseto.Pasetos;
import dev.paseto.jpaseto.lang.Keys;

public class PasetoToken implements Token {

  private byte[] base64Secret;
  private static final long defaultMilliSecondsSinceNow = 60 * 60 * 1000; // 1 hs
  private Long milliSecondsSinceNow;

  public PasetoToken(String base64Secret, long milliSecondsSinceNow) {
    this.base64Secret = Base64.getDecoder().decode(base64Secret);
    this.milliSecondsSinceNow = milliSecondsSinceNow;
  }

  public PasetoToken(String base64Secret) {
    this(base64Secret, defaultMilliSecondsSinceNow);
  }

  private Long expiration() {
    return (new Date().getTime() + this.milliSecondsSinceNow) / 1000;
  }

  @Override
  public String token(Map<String, Object> payload) {
    var pb = Pasetos.V2.LOCAL.builder();

    payload.forEach((key, value) -> {
      pb.claim(key, value);
    });

    pb.setExpiration(Instant.ofEpochSecond(this.expiration()));

    return pb.setSharedSecret(Keys.secretKey(this.base64Secret)).compact();
  }
}

