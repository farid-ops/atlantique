package atlantique.cnut.ne.atlantique.util;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UtilServiceImpl implements UtilService {


    @Override
    public Map<String, Object> response(String code, Boolean status, String message, Object data) {
        Map<String, Object> map=new HashMap<>();
        map.put("code", code);
        map.put("status", status);
        map.put("message", message);
        map.put("date", new Date());
        map.put("data", data);
        return map;
    }
}
