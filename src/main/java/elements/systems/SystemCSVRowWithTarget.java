package elements.systems;

import elements.concentrations.CalculationResult;
import elements.io.CSVRowWithTarget;

import java.util.ArrayList;
import java.util.Collection;

public class SystemCSVRowWithTarget implements CSVRowWithTarget {
    private final String systemDescription;
    private final CalculationResult calculationResult;

    public SystemCSVRowWithTarget(String systemDescription,
                                  CalculationResult calculationResult) {
        this.systemDescription = systemDescription;
        this.calculationResult = calculationResult;
    }

    @Override
    public Collection<String> getHeadings() {
        ArrayList<String> headings = new ArrayList<>();
        headings.add("System");
        headings.addAll(calculationResult.getHeadingsWithoutElements());
        return headings;
    }

    @Override
    public Collection<Object> getValues() {
        ArrayList<Object> values = new ArrayList<>();
        values.add(systemDescription);
        values.addAll(calculationResult.getValuesWithoutElements());
        return values;
    }

    @Override
    public Double getBestTargetDistance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(CSVRowWithTarget o) {
        throw new UnsupportedOperationException();
    }
}
