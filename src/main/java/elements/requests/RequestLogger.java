package elements.requests;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.PrintWriter;

@Component
public class RequestLogger {

    private final PrintWriter printWriter;
    private final LookupService lookupService;

    public RequestLogger() {
        boolean append;
        try {
            printWriter = new PrintWriter(
                    new FileOutputStream(
                            "requests.csv",
                            append = true
                    )
            );

            lookupService = new LookupService(
                    "GeoLiteCity.dat",
                    LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void log(String clientIp,
                    RequestType systemsRequest,
                    String requestedAlloySystems) {

        Location location = lookupService.getLocation(clientIp);
        String countryName = location == null ? "" : location.countryName;
        String city = location == null ? "" : location.city;

        String message = new LocalDate() + ","
                + new LocalTime() + ","
                + clientIp + ","
                + countryName + ","
                + city + ","
                + systemsRequest + ","
                + "\"" + requestedAlloySystems + "\"";

        printWriter.println(message);
        printWriter.flush();
    }
}
