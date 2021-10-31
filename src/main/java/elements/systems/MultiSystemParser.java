package elements.systems;

import java.util.ArrayList;
import java.util.List;

public class MultiSystemParser {
    public static final String WHITESPACE_PATTERN = "[\r\n\t ]";

    public static boolean isValid(String systemsRequest) {
        String elementPattern = "([A-Z][a-z]?)";
        String decimalPattern = "([0-9]+(\\.([0-9]+)?)?)";
        String elementWithConcentrationPattern = String.format("(%s%s?)", elementPattern, decimalPattern);
        String systemPattern = String.format("%s{2,7}", elementWithConcentrationPattern);
        String whitespacePattern = String.format("(%s+)", WHITESPACE_PATTERN);
        String multipleSystemPattern = String.format("%s?%s(%s%s){0,99}%s?",
                whitespacePattern,
                systemPattern,
                whitespacePattern,
                systemPattern,
                whitespacePattern);
        return systemsRequest.matches(multipleSystemPattern);
    }

    public static List<AlloySystem> parseSystems(String systemsRequest) {
        String trimmedSystemRequest = systemsRequest.trim();

        List<AlloySystem> alloySystems = new ArrayList<>();
        for (String systemDescription : trimmedSystemRequest.split(WHITESPACE_PATTERN + "+")) {
            alloySystems.add(new AlloySystem(systemDescription));
        }
        return alloySystems;
    }
}
