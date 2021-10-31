package elements.concentrations;

import elements.calculations.Element;

public class ComparisonUtil {

    private ComparisonUtil() {}

    public static int compareElements(Element[] lhs, Element[] rhs) {
        for (int i = 0; i < lhs.length && i < rhs.length; i++) {
            Element lhsElement = lhs[i];
            Element rhsElement = rhs[i];
            if (!lhsElement.equals(rhsElement)) {
                return lhsElement.compareTo(rhsElement);
            }
        }

        if (lhs.length != rhs.length) {
            return lhs.length < rhs.length ? -1 : 1;
        }

        return 0;
    }

    public static int compareConcentrations(Double[] lhs, Double[] rhs) {
        for (int i = 0; i < lhs.length; i++) {
            Double lhsConcentration = lhs[i];
            Double rhsConcentration = rhs[i];
            if (!lhsConcentration.equals(rhsConcentration)) {
                return lhsConcentration.compareTo(rhsConcentration);
            }
        }

        if (lhs.length != rhs.length) {
            return lhs.length < rhs.length ? -1 : 1;
        }

        return 0;
    }

}
