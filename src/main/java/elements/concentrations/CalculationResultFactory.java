package elements.concentrations;

import elements.calculations.Element;
import elements.structure.ResultingStructure;

public class CalculationResultFactory {
    private final String targetName;
    private final Double targetValue;

    public CalculationResultFactory(String targetName,
                                    Double targetValue) {
        this.targetName = targetName;
        this.targetValue = targetValue;
    }

    public CalculationResult newInstance(Element[] elements,
                                         Double[] concentrations,
                                         ResultingStructure structure) {
        return new CalculationResult(
                elements,
                concentrations,
                structure,
                targetName,
                targetValue
        );
    }
}
