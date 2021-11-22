# Alloy_Search_and_Predict
High-entropy alloy predictive code described in publication "King, D. J. M., Middleburgh, S. C., McGregor, A. G., &amp; Cortie, M. B. (2016). Predicting the formation and stability of single phase high-entropy alloys. Acta Materialia, 104, 172-179."

This is a version of the code that has a fully built web interface and is best run on a server for multiple users. For a version that is better suited to local use please go here: https://github.com/GM-King/Alloy_Search_and_Predict_1.0

To test the compiled web interface on localhost:8080 I use Spring Boot in the IntelliJ IDE. To host this on a server, build and compile the code using Maven, which will output a .war file in the "target" directory. This .war file should then be run using Java on the server.

N.B. This was written using old Springboot and Maven versions and will no longer compile using the most recent versions, if you wan't a working .war file please email me directly.
