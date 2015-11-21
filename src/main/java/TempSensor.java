import java.util.Random;

/**
 * Created by WU on 17/11/2015.
 */
public class TempSensor {
    public double getReading() {
        return 36 + new Random().nextDouble() * 3;
    }
}
