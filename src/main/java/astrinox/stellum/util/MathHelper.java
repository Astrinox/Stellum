package astrinox.stellum.util;

public class MathHelper {
    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return outMin + (value - inMin) * (outMax - outMin) / (inMax - inMin);
    }
}
