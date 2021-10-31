package elements.controllers;

import elements.calculations.RegularCalculation;
import elements.concentrations.CalculationResult;
import elements.concentrations.CalculationResultFactory;
import elements.requests.RequestLogger;
import elements.requests.RequestType;
import elements.systems.AlloySystem;
import elements.systems.MultiSystemParser;
import elements.systems.SystemCSVRowWithTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static elements.controllers.CaptchaVerifier.verifyCaptcha;

@Controller
public class SystemsController {

    private static final int MAX_SYSTEM_REQUEST_LENGTH = 1024;

    private static final Logger LOG = Logger.getLogger(SystemsController.class.getSimpleName());

    @Autowired
    private RequestLogger requestLogger;

    @RequestMapping(
            value = "/systems.htm",
            method = RequestMethod.GET)
    public String systemPage() {
        return "systems";
    }

    @RequestMapping(
            value = "/systems.csv",
            method = RequestMethod.POST,
            produces = "text/csv")
    @ResponseBody
    public String systemCsv(@RequestParam String systemsRequest,
                            @RequestParam("g-recaptcha-response") String gCaptchaResponse,
                            HttpServletRequest servletRequest,
                            HttpServletResponse servletResponse) throws Exception {

        servletResponse.setHeader("Content-Disposition", "attachment; filename=result.csv");

        String clientIp = servletRequest.getRemoteAddr();
        verifyCaptcha(gCaptchaResponse, clientIp);
        verifySystems(systemsRequest);

        List<AlloySystem> requestedAlloySystems = MultiSystemParser.parseSystems(systemsRequest);

        requestLogger.log(
                clientIp,
                RequestType.SystemsRequest,
                requestedAlloySystems.toString()
        );

        RegularCalculation regularCalculation = new RegularCalculation(
                new CalculationResultFactory("delta", 0.0)
        );

        StringBuilder resultBuilder = new StringBuilder();
        boolean headerRequired = true;
        for (AlloySystem alloySystem : requestedAlloySystems) {
            CalculationResult calculationResult = regularCalculation.calculate(alloySystem);

            SystemCSVRowWithTarget csvRow = new SystemCSVRowWithTarget(
                    alloySystem.getSystemDescription(),
                    calculationResult
            );

            if (headerRequired) {
                headerRequired = false;
                resultBuilder.append(commaSeparate(csvRow.getHeadings()));
            }

            resultBuilder.append(commaSeparate(csvRow.getValues()));
        }

        return resultBuilder.toString();
    }

    private void verifySystems(@RequestParam String systemsRequest) {
        if (systemsRequest.length() > MAX_SYSTEM_REQUEST_LENGTH) {
            LOG.log(Level.INFO, "system request is too long");
            throw new ValidationException();
        }

        if (!MultiSystemParser.isValid(systemsRequest)) {
            LOG.log(Level.INFO, "ignoring invalid system request");
            throw new ValidationException();
        }
    }

    private String commaSeparate(Collection<?> values) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = values.iterator();
        while (iterator.hasNext()) {
            String field = iterator.next().toString();
            builder.append(field);
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append('\n');
        return builder.toString();
    }

}
