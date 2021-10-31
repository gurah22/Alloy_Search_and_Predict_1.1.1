package elements.input;

import elements.calculations.Element;
import elements.calculations.OutputParameter;
import elements.io.ElementSelectionIterator;

import java.util.Iterator;
import java.util.List;

public class WebInputRequest implements Input {
    private final OutputParameter optimisationParameter;
    private final Double targetValue;
    private final Integer numberOfElements;
    private final Double stepSize;

    private List<Element> preselectedElements;
    private int numberOfWildcardElements;

    public WebInputRequest(OutputParameter optimisationParameter,
                           Double targetValue,
                           String elementList,
                           Double stepSize) {
        this.optimisationParameter = optimisationParameter;
        this.targetValue = targetValue;
        this.stepSize = stepSize;

        ParsedElements parsedElements = new ParsedElements(elementList);
        this.preselectedElements = parsedElements.getChosenElements();
        this.numberOfWildcardElements = parsedElements.getWildcardElements();
        this.numberOfElements = preselectedElements.size() + numberOfWildcardElements;
    }

    @Override
    public Iterable<List<Element>> getElementSelections() {
        return new Iterable<List<Element>>() {
            @Override
            public Iterator<List<Element>> iterator() {
                return new ElementSelectionIterator(
                        preselectedElements,
                        numberOfWildcardElements
                );
            }
        };
    }

    @Override
    public boolean hasMultipleSelections() {
        return numberOfWildcardElements > 0;
    }

    @Override
    public String getTargetName() {
        return optimisationParameter.getColumnName();
    }

    @Override
    public double getTargetValue() {
        return targetValue;
    }

    @Override
    public int getNumberOfElements() {
        return numberOfElements;
    }

    @Override
    public double getLinearStepSize() {
        return stepSize;
    }

}
