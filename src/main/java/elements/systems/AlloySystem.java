package elements.systems;

import elements.calculations.Element;

public class AlloySystem {
    private final Element[] elements;
    private final Double[] concentrations;
    private final String systemDescription;

    public AlloySystem(String systemDescription) {
        this.systemDescription = systemDescription;
        this.elements = SystemCalculator.getElements(systemDescription);
        this.concentrations = SystemCalculator.getConcentrations(systemDescription);
    }

    public Element[] getElements() {
        return elements;
    }

    public Double[] getConcentrations() {
        return concentrations;
    }

    public String getSystemDescription() {
        return systemDescription;
    }

    @Override
    public String toString() {
        return "AlloySystem{" + systemDescription + '}';
    }
}
