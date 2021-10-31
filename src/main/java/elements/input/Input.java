package elements.input;

import elements.calculations.Element;

import java.util.List;

public interface Input {

    Iterable<List<Element>> getElementSelections();

    boolean hasMultipleSelections();

    String getTargetName();

    double getTargetValue();

    int getNumberOfElements();

    double getLinearStepSize();

}
