package elements.input;

import jakarta.validation.ValidationException;

public class ConcentrationIncrementHelper {
    private static final double TOTAL = 1.0;

    public static void validateConcentrationIncrementSize(double incrementSize) {
        int scaledTotal = getScaledTotal(incrementSize);
        int decimalPlaces = 15;
        long scalingFactor = (long) Math.pow(10, decimalPlaces);
        if (Math.round(scaledTotal * incrementSize * scalingFactor) != scalingFactor) {
            throw new ValidationException("Invalid concentration increment size (" + incrementSize + "); it must evenly divide 1.00, e.g. 0.01 or 0.005");
        }
    }

    private static int getScaledTotal(double incrementSize) {
        return (int) Math.round(TOTAL / incrementSize);
    }

}
