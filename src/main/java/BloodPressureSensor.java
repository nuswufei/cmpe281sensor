import java.util.Random;

/**
 * Created by WU on 17/11/2015.
 */
public class BloodPressureSensor {
    public double getReading(){
        Random random = new Random();
        return 80d + random.nextDouble() * 30;
    }
}
