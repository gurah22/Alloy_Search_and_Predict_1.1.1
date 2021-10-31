package elements.runner;

import elements.calculations.Element;
import elements.concentrations.CalculationResultListener;
import elements.concentrations.CalculationResultSummary;
import elements.input.Input;
import elements.io.CalculationResultManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class CalculationRunner {

    public static final int BATCH_SIZE = 1000;
    private final Input input;
    private ArrayList<CalculationResultListener> resultListeners;
    private ArrayList<CalculationResultSummaryListener> resultSummaryListeners;

    public CalculationRunner(Input input) {
        this.input = input;
        this.resultListeners = new ArrayList<>();
        this.resultSummaryListeners = new ArrayList<>();
    }

    public void run() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CompletionService<CalculationResultSummary> completionService = new ExecutorCompletionService(executorService);

        Iterator<List<Element>> elementSelections = input.getElementSelections().iterator();

        while (elementSelections.hasNext()) {
            int tasks = 0;
            while (elementSelections.hasNext() && tasks < BATCH_SIZE) {
                CategoriserRunner categoriserRunner = new CategoriserRunner(
                        elementSelections.next(),
                        input
                );

                for (CalculationResultListener resultListener : resultListeners) {
                    categoriserRunner.addResultListener(resultListener);
                }

                completionService.submit(categoriserRunner);
                tasks++;
            }

            for (int i = 0; i < tasks; i++) {
                CalculationResultSummary summary = completionService.take().get();
                for (CalculationResultSummaryListener listener : resultSummaryListeners) {
                    listener.handle(summary);
                }
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public void addResultListener(CalculationResultListener listener) {
        resultListeners.add(listener);
    }

    public void addResultSummaryListener(CalculationResultSummaryListener listener) {
        resultSummaryListeners.add(listener);
    }
}
