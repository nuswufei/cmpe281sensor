/**
 * Created by WU on 17/11/2015.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
public class Mobile {
    private final String url;
    private HeartrateSensor heartrateSensor;
    private TempSensor tempSensor;
    private BloodPressureSensor bloodPressureSensor;
    private String name;

    static HttpClient httpClient = HttpClientBuilder.create().build();
    public Mobile(String url,
                  String name,
                  HeartrateSensor heartrateSensor,
                  TempSensor tempSensor,
                  BloodPressureSensor bloodPressureSensor) {
        this.url = url;
        this.heartrateSensor = heartrateSensor;
        this.tempSensor = tempSensor;
        this.bloodPressureSensor = bloodPressureSensor;
        this.name = name;
    }
    public static void main(String[] args) {
        Mobile mobile = new Mobile("http://localhost:8080/uploaddata",
                "user1",
                new HeartrateSensor(),
                new TempSensor(),
                new BloodPressureSensor());
        mobile.uploadData();
    }
    private void uploadData() {
        DataPoint dataPoint = new DataPoint();
        dataPoint.setName(name);
        dataPoint.setBloodpressure(bloodPressureSensor.getReading());
        dataPoint.setTemperature(tempSensor.getReading());
        dataPoint.setHeartrate(heartrateSensor.getReading());
        dataPoint.setTimeStamp(System.currentTimeMillis());
        try {
            String json = new ObjectMapper().writeValueAsString(dataPoint);
            post(url, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void post(String url, String jsonString) {
        try {
            HttpPost request = new HttpPost(url);
            StringEntity params =new StringEntity(jsonString, ContentType.create("application/json"));
            request.addHeader("Content-Type", "application/json");
            request.setEntity(params);
            httpClient.execute(request);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
