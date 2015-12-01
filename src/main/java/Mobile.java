/**
 * Created by WU on 17/11/2015.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Mobile implements Runnable {
    private HeartrateSensor heartrateSensor;
    private TempSensor tempSensor;
    private BloodPressureSensor bloodPressureSensor;
    private String name;

    static HttpClient httpClient = HttpClientBuilder.create().build();
    public Mobile(String name,
                  HeartrateSensor heartrateSensor,
                  TempSensor tempSensor,
                  BloodPressureSensor bloodPressureSensor) {
        this.heartrateSensor = heartrateSensor;
        this.tempSensor = tempSensor;
        this.bloodPressureSensor = bloodPressureSensor;
        this.name = name;
    }
    public static void main(String[] args) {
        beepForOneMinute();
    }

    private static final String BASE_URL = "http://localhost:8080";
    public static void beepForOneMinute() {
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        final Runnable beeper = new Mobile("user1",
                new HeartrateSensor(),
                new TempSensor(),
                new BloodPressureSensor());
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 0, 3, TimeUnit.SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
                System.out.println("finish");
            }
        }, 60, TimeUnit.SECONDS);
    }

    private void uploadData() {
        System.out.println("uploadData");
        DataPoint dataPoint = new DataPoint();
        dataPoint.setName(name);
        dataPoint.setBloodpressure(bloodPressureSensor.getReading());
        dataPoint.setTemperature(tempSensor.getReading());
        dataPoint.setHeartrate(heartrateSensor.getReading());
        dataPoint.setTimeStamp(System.currentTimeMillis());
        try {
            String json = new ObjectMapper().writeValueAsString(dataPoint);
            post(BASE_URL + "/sensor/uploaddata", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void post(String url, String jsonString) {
        try {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(jsonString, ContentType.create("application/json"));
            request.addHeader("Content-Type", "application/json");
            request.setEntity(params);
            httpClient.execute(request);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String get(String url) {
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void run() {
        if (checkStatus()) {
            uploadData();
        }
    }

    private boolean checkStatus() {
        System.out.println("checkStatus");
        String status = get(BASE_URL + "/sensor/status/1");
        System.out.println("status: " + status);
        if (status != null && status.equals("ON")) {
            return true;
        }
        return false;
    }
}
