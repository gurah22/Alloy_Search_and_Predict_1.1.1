package elements.calculations;

public enum OutputParameter {
    PHI("Stability", "phi"),
    DELTA("Strain", "delta"),
    VEC("Valence electron concentration", "VEC"),
    SMIX("Entropy (J/mol/K)", "smix"),
    TM("Melting temperature (K)", "tm"),
    TMAX("Maximum melting temperature (K)", "tMax"),
    PRICE("Price (USD/kg)", "price"),
    PTEMP("Precipitation temperature (K)", "ptemp"),
    HMIX_L("∆Hmax (kJ/mol/K)", "Hmax"),
    HMIX_SS("∆Hss (kJ/mol/K)", "Hss");

    private final String displayText;
    private String columnName;

    OutputParameter(String displayText,
                            String columnName) {
        this.displayText = displayText;
        this.columnName = columnName;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getColumnName() {
        return columnName;
    }
}
