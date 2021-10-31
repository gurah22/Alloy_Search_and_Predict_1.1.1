package elements.io;

import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class TopResultsInMemory implements AutoCloseable, CalculationResultManager {
    private final static int PURGE_LIMIT = 100;

    private final AtomicLong purgeCounter = new AtomicLong(0);
    private final ConcurrentNavigableMap<CSVRowWithTarget, CSVRowWithTarget> topResults = new ConcurrentSkipListMap<>();
    private final Writer resultWriter;
    private final int numberOfResults;

    public TopResultsInMemory(Writer resultWriter,
                              int numberOfResults) {
        this.resultWriter = resultWriter;
        this.numberOfResults = numberOfResults;
    }

    @Override
    public void write(CSVRowWithTarget csvRowWithTarget) {
        purgeIfRequired();

        topResults.put(csvRowWithTarget, csvRowWithTarget);
    }

    @Override
    public void close() throws Exception {
        try {
            printTopResults();
        } finally {
            try {
                if (resultWriter != null) {
                    resultWriter.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void printTopResults() {
        Iterator<CSVRowWithTarget> iterator = topResults.keySet().iterator();

        boolean headingRowHasBeenWritten = false;
        for (int i = 0; iterator.hasNext() && i < numberOfResults; i++) {
            CSVRowWithTarget csvRowWithTarget = iterator.next();
            if (!headingRowHasBeenWritten) {
                printRow(csvRowWithTarget.getHeadings());
                headingRowHasBeenWritten = true;
            }

            printRow(csvRowWithTarget.getValues());
        }
    }

    private void printRow(Collection<?> items) {
        try {
            resultWriter.write(CSVUtils.commaSeparate(items) + "\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void purgeIfRequired() {
        if (!isPurgeRequired()) {
            return;
        }

        try {
            Iterator<CSVRowWithTarget> iterator = topResults.descendingKeySet().iterator(); // ARE YOU SLOW?
            for (int i = 0; iterator.hasNext() && i < PURGE_LIMIT; i++) {
                iterator.next();
                iterator.remove();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPurgeRequired() {
        long purgeCount = purgeCounter.incrementAndGet();
        if (purgeCount <= numberOfResults) {
            return false;
        }

        return (purgeCount - numberOfResults) % PURGE_LIMIT == 0;
    }
}
