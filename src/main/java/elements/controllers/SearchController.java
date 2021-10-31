package elements.controllers;

import elements.calculations.ElementData;
import elements.calculations.OutputParameter;
import elements.concentrations.CalculationResult;
import elements.concentrations.CalculationResultListener;
import elements.concentrations.CalculationResultSummary;
import elements.input.Input;
import elements.input.WebInputRequest;
import elements.io.CalculationResultManager;
import elements.io.TopResultsInMemory;
import elements.runner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private ConcurrencyGatekeeper concurrencyGatekeeper;

    @Autowired
    private QueueController queueController;

    @RequestMapping(
            value = "/concentration-search.htm",
            method = RequestMethod.GET)
    public String concentrationSearchPage(Map<String, Object> model) {
        model.put("requestWildcards", false);
        return prepareToReturnSearchPage(model);
    }

    @RequestMapping(
            value = "/element-search.htm",
            method = RequestMethod.GET)
    public String elementSearchPage(Map<String, Object> model) {
        model.put("requestWildcards", true);
        return prepareToReturnSearchPage(model);
    }

    private String prepareToReturnSearchPage(Map<String, Object> model) {
        model.put("outputParameters", OutputParameter.values());
        model.put("availableElements", ElementData.getAllAvailableElements());
        model.put("calculationLimit", QueueController.MAXIMUM_CALCULATIONS_IN_A_SINGLE_RUN);
        return "search";
    }

    public String processRequest(CalculationRequest calculationRequest) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        WebInputRequest webInputRequest = new WebInputRequest(
                calculationRequest.getOptimisationParameter(),
                calculationRequest.getTargetValue(),
                calculationRequest.getElementList(),
                calculationRequest.getStepSize()
        );

        try (TopResultsInMemory topResultsInMemory = new TopResultsInMemory(
                new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream)),
                calculationRequest.getNumberOfResults())) {

            runCalculations(
                    webInputRequest,
                    topResultsInMemory
            );

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return byteArrayOutputStream.toString("UTF-8");
    }

    private void runCalculations(Input input,
                                 final CalculationResultManager calculationResultManager) throws Exception {
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        CalculationRunner calculationRunner = new CalculationRunner(input);

        if (input.hasMultipleSelections()) {
            calculationRunner.addResultSummaryListener(new CalculationResultSummaryListener() {
                @Override
                public void handle(CalculationResultSummary summary) {
                    calculationResultManager.write(summary);
                }
            });
        } else {
            calculationRunner.addResultListener(new CalculationResultListener() {
                @Override
                public void handle(CalculationResult result) {
                    calculationResultManager.write(result);
                }
            });
        }

        ProgressListener progressListener = ProgressListener.newProgressListener(input);
        calculationRunner.addResultListener(progressListener);

        calculationRunner.run();

        progressListener.stop();

        BigInteger timeIntervalSeconds = BigInteger.valueOf((long) stopwatch.getSeconds());
        String formattedTimeInterval = IntervalFormatter.formatSeconds(timeIntervalSeconds);
        System.out.println("Total time taken: " + formattedTimeInterval);
    }

}
