package elements.controllers;

import elements.calculations.Element;
import elements.calculations.ElementData;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static void validateElements(Integer numberOfElements,
                                        List<String> elementSymbols) {
        Iterator<String> elementSymbolIterator = elementSymbols.iterator();
        Set<Element> elementSet = new HashSet<>();
        for (int i = 0; i < numberOfElements; i++) {
            String elementSymbol = elementSymbolIterator.next();
            elementSet.add(ElementData.getElement(elementSymbol));
        }

        if (elementSet.size() != numberOfElements) {
            throw new ValidationException("Duplicate or bad symbols: " + elementSymbols);
        }
    }

    public static void verifyIntegerIsInInclusiveRange(double value,
                                                       double min,
                                                       double max) {
        if (value < min || value > max) {
            throw new ValidationException("Value must be within range [" + min + ", " + max + "]");
        }
    }

    public static void verifyDoubleIsARegularNumber(Double value) {
        if (value.isNaN() || value.isInfinite()) {
            throw new ValidationException("Value must be a regular floating point number");
        }
    }

    public static void verifyNotNull(Object o) {
        if (o == null) {
            throw new ValidationException("Parameter should not be null");
        }
    }

}
