package elements.controllers;

import elements.calculations.OutputParameter;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class CalculationRequest {

    private final OutputParameter optimisationParameter;
    private final Double targetValue;
    private final Integer numberOfResults;
    private final Integer numberOfWildcardElements;
    private final Integer numberOfHandPickedElements;
    private final Double stepSize;
    private final String gCaptchaResponse;
    private final String elementList;
    private final List<String> elementSymbols;
    private final LocalDateTime creationTime;

    public CalculationRequest(OutputParameter optimisationParameter,
                              Double targetValue,
                              Integer numberOfResults,
                              Integer numberOfWildcardElements,
                              Integer numberOfHandPickedElements,
                              List<String> elementSymbols,
                              Double stepSize,
                              String gCaptchaResponse) {
        this.creationTime = new LocalDateTime();
        this.optimisationParameter = optimisationParameter;
        this.targetValue = targetValue;
        this.numberOfResults = numberOfResults;
        this.numberOfWildcardElements = numberOfWildcardElements;
        this.numberOfHandPickedElements = numberOfHandPickedElements;
        this.stepSize = stepSize;
        this.gCaptchaResponse = gCaptchaResponse;
        this.elementSymbols = elementSymbols;
        this.elementList = buildElementList(
                numberOfWildcardElements,
                numberOfHandPickedElements,
                elementSymbols
        );
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public OutputParameter getOptimisationParameter() {
        return optimisationParameter;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public Integer getNumberOfResults() {
        return numberOfResults;
    }

    public Integer getNumberOfWildcardElements() {
        return numberOfWildcardElements;
    }

    public Integer getNumberOfHandPickedElements() {
        return numberOfHandPickedElements;
    }

    public Double getStepSize() {
        return stepSize;
    }

    public String getgCaptchaResponse() {
        return gCaptchaResponse;
    }

    public List<String> getElementSymbols() {
        return elementSymbols;
    }

    public String getElementList() {
        return elementList;
    }

    private String buildElementList(Integer numberOfWildcardElements,
                                    Integer numberOfHandPickedElements,
                                    List<String> elements) {
        List<String> selectedElements = new ArrayList<>();

        for (int i = 0; i < numberOfHandPickedElements; i++) {
            selectedElements.add(elements.get(i));
        }

        for (int i = 0; i < numberOfWildcardElements; i++) {
            selectedElements.add("*");
        }

        return StringUtils.join(selectedElements, ',');
    }
}
