package elements.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MiscellaneousPageController {

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET)
    public String home() {
        return "home";
    }

    @RequestMapping(
            value = "/recent-publications.htm",
            method = RequestMethod.GET)
    public String recentPublications() {
        return "recent-publications";
    }

    @RequestMapping(
            value = "/how-to.htm",
            method = RequestMethod.GET)
    public String howTo() {
        return "how-to";
    }

    @RequestMapping(
            value = "/access.htm",
            method = RequestMethod.GET)
    public String access() { return "access"; }

}
