package atlantique.cnut.ne.atlantique.util;


import java.util.Map;

public interface UtilService {

    Map<String, Object> response(String code, Boolean status, String message, Object data);
}
