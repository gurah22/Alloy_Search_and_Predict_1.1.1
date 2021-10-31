package elements.runner;

import elements.calculations.Element;
import elements.calculations.RegularCalculation;
import elements.concentrations.CalculationResult;
import elements.concentrations.CalculationResultFactory;
import elements.concentrations.CalculationResultListener;
import elements.concentrations.CalculationResultSummary;
import elements.concentrations2.CloneableDoubleIteratorWithinBounds;
import elements.concentrations2.LinearIteratorBuilder;
import elements.concentrations2.SummingAllocationIterator;
import elements.input.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class CategoriserRunner implements Callable<CalculationResultSummary> {
    private final List<Element> elements;
    private final Input input;
    private final List<CalculationResultListener> resultListeners;

    public CategoriserRunner(List<Element> elements,
                             Input input) {
        this.elements = elements;
        this.input = input;
        this.resultListeners = new ArrayList<>();
    }

    @Override
    public CalculationResultSummary call() {
        CalculationResultSummary calculationResultSummary = new CalculationResultSummary(
                input,
                elements
        );

        CalculationResultFactory calculationResultFactory = new CalculationResultFactory(
                input.getTargetName(),
                input.getTargetValue()
        );

        SummingAllocationIterator summingAllocationIterator = buildSummingAllocationIterator();

        Element[] elementArray = elements.toArray(new Element[elements.size()]);
        while (summingAllocationIterator.hasNext()) {
            Map<Element, Double> allocation = summingAllocationIterator.next();
            List<Double> concentrations = new ArrayList<>();
            for (Element element : elements) {
                concentrations.add(allocation.get(element));
            }

            CalculationResult calculationResult = new RegularCalculation(calculationResultFactory)
                    .calculate(
                            elementArray,
                            concentrations.toArray(new Double[concentrations.size()])
                    );

            calculationResultSummary.handleResult(calculationResult);

            for (CalculationResultListener resultListener : resultListeners) {
                resultListener.handle(calculationResult);
            }
        }

        return calculationResultSummary;
    }

    private SummingAllocationIterator buildSummingAllocationIterator() {
        CloneableDoubleIteratorWithinBounds iterator = new LinearIteratorBuilder()
                .withStartInclusive(input.getLinearStepSize())
                .withEndExclusive(1.0)
                .withStepSize(input.getLinearStepSize())
                .build();

        Map<Element, CloneableDoubleIteratorWithinBounds> allocators = new HashMap<>();
        for (Element element : elements) {
            allocators.put(element, iterator.clone());
        }

        return SummingAllocationIterator.newInstance(
                allocators,
                1.0);
    }

    public void addResultListener(CalculationResultListener resultListener) {
        this.resultListeners.add(resultListener);
    }
}
