import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;


public class JsonUtility {
    private ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public JsonNode readJson(String json){
        JsonNode jsonNode = objectMapper.readTree(json);
        return jsonNode;
    }
}
