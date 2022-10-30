import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class RestClient {

  public static void main(String[] args) {
    final String sensorName = "Moscow_7";

    registerSensor(sensorName);

    ThreadLocalRandom random = ThreadLocalRandom.current();

    // случайная температура от 0 до +50, идет дождь или нет - случайно
    for (int i = 0; i < 100; i++) {
      System.out.println(i);
      sendMeasurement(random.nextDouble(0.0, 50.0),
          random.nextBoolean(), sensorName);
    }
  }

  private static void registerSensor(String sensorName) {
    final String url = "http://localhost:8080/sensors/registration";

    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("name", sensorName);

    makePostRequestWithJSONData(url, jsonData);
  }

  private static void sendMeasurement(double value, boolean raining, String sensorName) {
    final String url = "http://localhost:8080/measurements/add";

    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("value", value);
    jsonData.put("raining", raining);
    jsonData.put("sensor", Map.of("name", sensorName));

    makePostRequestWithJSONData(url, jsonData);
  }

  private static void makePostRequestWithJSONData(String url, Map<String, Object> jsonData) {
    final RestTemplate restTemplate = new RestTemplate();

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Object> request = new HttpEntity<>(jsonData, headers);

    try {
      restTemplate.postForObject(url, request, String.class);

      System.out.println("Измерение успешно отправлено на сервер!");
    } catch (HttpClientErrorException e) {
      System.out.println("ОШИБКА!");
      System.out.println(e.getMessage());
    }
  }
}
