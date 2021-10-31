package elements.io;

import java.util.Collection;
import java.util.Iterator;

public class CSVUtils {
    public static String commaSeparate(Collection<?> values) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = values.iterator();
        while (iterator.hasNext()) {
            String field = iterator.next().toString();
            builder.append(field);
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}
