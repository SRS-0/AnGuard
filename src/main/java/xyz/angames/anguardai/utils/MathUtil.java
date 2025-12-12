package xyz.angames.anguardai.utils;

public class MathUtil {
    public static final double MINIMUM_DIVISOR = 1e-4; // 0.0001

    public static double gcd(double a, double b) {
        if (a < b) return gcd(b, a);
        if (Math.abs(b) < MINIMUM_DIVISOR) return a;
        return gcd(b, a - Math.floor(a / b) * b);
    }
}