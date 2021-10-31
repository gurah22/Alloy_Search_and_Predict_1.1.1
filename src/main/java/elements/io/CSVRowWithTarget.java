package elements.io;

import java.util.Collection;

public interface CSVRowWithTarget extends Comparable<CSVRowWithTarget> {
    Collection<String> getHeadings();

    Collection<Object> getValues();

    Double getBestTargetDistance();
}
