package layer.business;

import java.util.Map;

interface Token {

  String token(Map<String, Object> payload);

}
