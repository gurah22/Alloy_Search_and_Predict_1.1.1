package elements.input;

import elements.calculations.Element;
import elements.calculations.ElementData;

import java.util.ArrayList;
import java.util.List;

public class ParsedElements {
    private List<Element> chosenElements;
    private int wildcardElements;

    public ParsedElements(String elementList) {
        chosenElements = new ArrayList<>();
        wildcardElements = 0;

        for (String elementSymbol : elementList.split(",")) {
            String trimmedElementSymbol = elementSymbol.trim();
            if ("*".equals(trimmedElementSymbol)) {
                wildcardElements++;
            } else {
                Element element = ElementData.getElement(trimmedElementSymbol);
                chosenElements.add(element);
            }
        }
    }

    public List<Element> getChosenElements() {
        return chosenElements;
    }

    public int getWildcardElements() {
        return wildcardElements;
    }
}
