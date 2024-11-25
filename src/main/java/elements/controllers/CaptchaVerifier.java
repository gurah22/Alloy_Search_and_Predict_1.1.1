package elements.controllers;

import kong.unirest.Unirest;


import jakarta.validation.ValidationException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static elements.controllers.ValidationUtil.verifyNotNull;

public class CaptchaVerifier {

    private static final Logger LOG = Logger.getLogger(CaptchaVerifier.class.getSimpleName());

    private static final String CAPTCHA_KEY = "6Le-5wcTAAAAAGdK6kFP6ZgHUCbXVZ_LDBEUUdNF";
    private static final String CAPTCHA_KEY_PUBLIC = "6Le-5wcTAAAAALZY9IW0S409JmECaYDxdK6xnYYx";

    public static void verifyCaptcha(String gCaptchaResponse, String remoteAddress) throws Exception {
        boolean disableCaptcha = Boolean.parseBoolean(System.getProperty("disable.captcha"));
        if (disableCaptcha) {
            LOG.log(Level.SEVERE, "Captcha is disabled");
            return;
        }

        verifyNotNull(gCaptchaResponse);

        if (gCaptchaResponse.isEmpty()) {
            handleEmptyCaptcha(remoteAddress);
        }

        boolean success = Unirest.post("https://www.google.com/recaptcha/api/siteverify")
                .header("accept", "application/json")
                .queryString("secret", CAPTCHA_KEY)
                .field("response", gCaptchaResponse)
                .field("remoteip", remoteAddress)
                .asJson()
                .getBody()
                .getObject()
                .getBoolean("success");

        if (!success) {
            String message = "Rejecting request from " + remoteAddress + ": google says captcha is invalid";
            LOG.log(Level.INFO, message);
            throw new ValidationException(message);
        }
    }

    private static void handleEmptyCaptcha(String remoteAddress) {
        String message = "Rejecting request from " + remoteAddress + ": captcha was empty";
        LOG.log(Level.INFO, message);
        throw new ValidationException(message);
    }
}
