import java.util.Random;

/**
 * Created by WU on 17/11/2015.
 */
public class HeartrateSensor {
    public int getReading() {
        return 60 + (int) new Random().nextDouble() * 40;
    }
}
