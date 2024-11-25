package elements.controllers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import elements.calculations.ElementData;
import elements.calculations.OutputParameter;
import elements.requests.RequestLogger;
import elements.requests.RequestType;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static elements.controllers.CaptchaVerifier.verifyCaptcha;
import static elements.controllers.ValidationUtil.*;
import static elements.input.ConcentrationIncrementHelper.validateConcentrationIncrementSize;
import static org.apache.commons.math3.util.CombinatoricsUtils.binomialCoefficient;

@Controller
public class QueueController {

    public static final long MAXIMUM_CALCULATIONS_IN_A_SINGLE_RUN = 10_000_000;
    public static final int HALF_A_SECOND = 500;

    private Cache<UUID, CalculationRequest> expiringRequestCache = CacheBuilder.newBuilder()
            .maximumSize(1000 * 1000)
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    private Cache<UUID, String> expiringResultCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    @Autowired
    @Lazy
    private SearchController searchController;

    @Autowired
    private RequestLogger requestLogger;

    @PostConstruct
    public void calculateFromQueue() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        Map.Entry<UUID, CalculationRequest> earliestCalculationRequest = getEarliestCalculationRequest();
                        if (earliestCalculationRequest != null) {
                            UUID requestTokenUUID = earliestCalculationRequest.getKey();
                            CalculationRequest calculationRequest = getCalculationRequest(requestTokenUUID);
                            String result = searchController.processRequest(calculationRequest);
                            expiringResultCache.put(requestTokenUUID, result);
                            expiringRequestCache.invalidate(requestTokenUUID);
                        }

                        Thread.sleep(HALF_A_SECOND);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            private Map.Entry<UUID, CalculationRequest> getEarliestCalculationRequest() {
                LocalDateTime earliestRequestTime = null;
                Map.Entry<UUID, CalculationRequest> earliestRequestEntry = null;
                for (Map.Entry<UUID, CalculationRequest> uuidCalculationRequestEntry : expiringRequestCache.asMap().entrySet()) {
                    CalculationRequest calculationRequest = uuidCalculationRequestEntry.getValue();
                    if (earliestRequestTime == null || calculationRequest.getCreationTime().isBefore(earliestRequestTime)) {
                        earliestRequestEntry = uuidCalculationRequestEntry;
                    }
                }
                return earliestRequestEntry;
            }
        }).start();
    }

    @RequestMapping(
            value = "/calculationRequestQueue",
            method = RequestMethod.POST
    )
    public String queue(@RequestParam final OutputParameter optimisationParameter,
                        @RequestParam final Double targetValue,
                        @RequestParam final Integer numberOfResults,
                        @RequestParam final Integer numberOfWildcardElements,
                        @RequestParam final Integer numberOfHandPickedElements,
                        @RequestParam final String element1,
                        @RequestParam final String element2,
                        @RequestParam final String element3,
                        @RequestParam final String element4,
                        @RequestParam final String element5,
                        @RequestParam final Double stepSize,
                        @RequestParam(value = "g-recaptcha-response", required = false) String gCaptchaResponse,
                        Map<String, String> model,
                        HttpServletRequest servletRequest) throws Exception {

        final UUID requestTokenUUID = UUID.randomUUID();

        CalculationRequest calculationRequest = new CalculationRequest(
                optimisationParameter,
                targetValue,
                numberOfResults,
                numberOfWildcardElements,
                numberOfHandPickedElements,
                Arrays.asList(
                        element1,
                        element2,
                        element3,
                        element4,
                        element5
                ),
                stepSize,
                gCaptchaResponse
        );

        logRequest(
                servletRequest,
                calculationRequest
        );

        verifyParameters(
                optimisationParameter,
                targetValue,
                numberOfResults,
                numberOfWildcardElements,
                numberOfHandPickedElements,
                stepSize,
                gCaptchaResponse,
                servletRequest,
                calculationRequest.getElementSymbols()
        );

        expiringRequestCache.put(requestTokenUUID, calculationRequest);

        model.put("requestToken", requestTokenUUID.toString());

        return "queue";
    }

    private void logRequest(HttpServletRequest servletRequest, CalculationRequest calculationRequest) {
        RequestType requestType = calculationRequest.getNumberOfWildcardElements() > 0
                ? RequestType.ElementSearchRequest
                : RequestType.ConcentrationSearchRequest;

        String calculationRequestString = "{"
                + "elements: [" + calculationRequest.getElementList() + "], "
                + "stepSize: " + calculationRequest.getStepSize() + ", "
                + "optimisationParameter: " + calculationRequest.getOptimisationParameter() + ", "
                + "targetValue: " + calculationRequest.getTargetValue()
                + "}";

        requestLogger.log(
                servletRequest.getRemoteAddr(),
                requestType,
                calculationRequestString
        );
    }

    @RequestMapping("/requestTokens/{requestToken}/status")
    @ResponseBody
    public QueueStatus status(@PathVariable String requestToken,
                              HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setHeader("Pragma", "No-cache");
        httpServletResponse.setHeader("Cache-Control", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);

        UUID requestTokenUUID = UUID.fromString(requestToken);
        Thread.sleep(1000);

        CalculationRequest requestedCalculation = expiringRequestCache.getIfPresent(requestTokenUUID);

        boolean ready;
        int numberOfOlderRequests = 0;
        int numberOfNewerRequests = 0;
        if (requestedCalculation == null) {
            ready = true;
        } else {
            ready = false;

            Set<Map.Entry<UUID, CalculationRequest>> entries = expiringRequestCache.asMap().entrySet();
            for (Map.Entry<UUID, CalculationRequest> entry : entries) {
                if (entry.getKey().equals(requestTokenUUID)) {
                    continue;
                }

                CalculationRequest calculation = entry.getValue();
                LocalDateTime calculationTime = calculation.getCreationTime();
                LocalDateTime requestCalculationTime = requestedCalculation.getCreationTime();
                if (calculationTime.isBefore(requestCalculationTime)) {
                    numberOfOlderRequests++;
                } else {
                    numberOfNewerRequests++;
                }
            }
        }

        return new QueueStatus(ready, numberOfOlderRequests, numberOfNewerRequests);
    }

    @RequestMapping(
            value = "/requestTokens/{requestToken}/result",
            method = RequestMethod.GET,
            produces = "text/csv")
    @ResponseBody
    public String result(@PathVariable String requestToken,
                         HttpServletResponse httpServletResponse) throws Exception {
        UUID requestTokenUUID = UUID.fromString(requestToken);
        String calculationResultCsv = expiringResultCache.getIfPresent(requestTokenUUID);

        if (calculationResultCsv == null) {
            throw new ValidationException("invalid/expired requestToken: " + requestToken);
        }

        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=result.csv");

        return calculationResultCsv;
    }

    private CalculationRequest getCalculationRequest(UUID requestTokenUUID) {
        return expiringRequestCache.getIfPresent(requestTokenUUID);
    }

    private static class QueueStatus {
        private final long timestamp;
        private final boolean ready;
        private final int numberOfOlderRequests;
        private final int numberOfNewerRequests;

        public QueueStatus(boolean ready,
                           int numberOfOlderRequests,
                           int numberOfNewerRequests) {

            this.timestamp = System.currentTimeMillis();
            this.ready = ready;
            this.numberOfOlderRequests = numberOfOlderRequests;
            this.numberOfNewerRequests = numberOfNewerRequests;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public boolean isReady() {
            return ready;
        }

        public int getNumberOfOlderRequests() {
            return numberOfOlderRequests;
        }

        public int getNumberOfNewerRequests() {
            return numberOfNewerRequests;
        }
    }

    private void verifyParameters(OutputParameter optimisationParameter,
                                  Double targetValue,
                                  Integer numberOfResults,
                                  Integer numberOfWildcardElements,
                                  Integer numberOfHandPickedElements,
                                  Double stepSize,
                                  String gCaptchaResponse,
                                  ServletRequest servletRequest,
                                  List<String> elementSymbols) throws Exception {
        verifyNotNull(optimisationParameter);

        verifyNotNull(targetValue);
        verifyDoubleIsARegularNumber(targetValue);

        verifyNotNull(numberOfWildcardElements);
        verifyNotNull(numberOfHandPickedElements);
        verifyIntegerIsInInclusiveRange(numberOfWildcardElements, 0, 5);

        int totalNumberOfElements = numberOfWildcardElements + numberOfHandPickedElements;
        verifyIntegerIsInInclusiveRange(totalNumberOfElements, 2, 7);

        verifyNotNull(numberOfResults);
        verifyIntegerIsInInclusiveRange(numberOfResults, 1, 1000);

        for (String elementSymbol : elementSymbols) {
            verifyNotNull(elementSymbol);
        }
        validateElements(numberOfHandPickedElements, elementSymbols);

        verifyNotNull(stepSize);
        validateConcentrationIncrementSize(stepSize);
        verifyIntegerIsInInclusiveRange(stepSize * totalNumberOfElements, 0.0, 1.0);

        verifyCaptcha(gCaptchaResponse, servletRequest.getRemoteAddr());

        validateTheRequestedNumberOfCalculations(
                numberOfWildcardElements,
                numberOfHandPickedElements,
                stepSize
        );
    }


    private void validateTheRequestedNumberOfCalculations(Integer numberOfWildcardElements,
                                                          Integer numberOfHandPickedElements,
                                                          Double stepSize) {
        int numberOfAvailableElements = ElementData.getAllAvailableElements().size() - numberOfHandPickedElements;
        long numberOfElementCombinations = binomialCoefficient(numberOfAvailableElements, numberOfWildcardElements);

        int numberOfElements = numberOfWildcardElements + numberOfHandPickedElements;
        int n = numberOfElements;
        int k = (int) ((1.0 - numberOfElements * stepSize) / stepSize);
        long numberOfCalculationsPerElementCombination = binomialCoefficient(n + k - 1, k);

        long totalNumberOfCalculationsRequired = numberOfElementCombinations * numberOfCalculationsPerElementCombination;
        if (totalNumberOfCalculationsRequired > MAXIMUM_CALCULATIONS_IN_A_SINGLE_RUN) {
            throw new ValidationException("Too many calculations were requested");
        }
    }
}
