<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <title>Alloy Search and Predict</title>
        <meta http-equiv="content-type" content="text/html;charset=utf-8" />
        <script src="/js/jquery-1.10.1.min.js"></script>
        <script type="text/javascript">
            var REQUEST_TOKEN = '${requestToken}';
            var REQUEST_TOKEN_BASE_URL = '/requestTokens/' + REQUEST_TOKEN;
            function statusRequestLoop() {
                $.getJSON(REQUEST_TOKEN_BASE_URL + "/status", function(data) {
                    if (data.ready) {
                        var older_requests_message = "Processing is complete, your file is being downloaded"
                            + " (you may need to choose where to save it)";
                        $("#number-of-older-requests").text(older_requests_message);
                        window.location.replace(REQUEST_TOKEN_BASE_URL + "/result");
                    } else {
                        var older_requests_message = data.numberOfOlderRequests == 0
                            ? "Your request is being processed"
                            : data.numberOfOlderRequests;
                        $("#number-of-older-requests").text(older_requests_message);
                        $("#number-of-newer-requests").text(data.numberOfNewerRequests);
                        setTimeout(statusRequestLoop, 500);
                    }
                });
            }

            $(document).ready(function() {
                statusRequestLoop();
            });
        </script>
        <link rel="stylesheet" type="text/css" href="/css/alloyasap.css">
    </head>

    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

    <%@ include file="/WEB-INF/jsp/include/taglibs.jsp" %>

    <body>

        <%@ include file="/WEB-INF/jsp/include/header.jsp" %>

        <div>
            <h3 id="heading-label">Calculation Request Queue</h3>

            <table id="calculation-requests">
                <tr>
                    <td class="main-column">Number of requests to process<br/>before your request:</td>
                    <td><span id="number-of-older-requests">Retrieving data</span></td>
                </tr>
                <tr>
                    <td class="main-column">Number of requests to process<br/>after your request:</td>
                    <td><span id="number-of-newer-requests">Retrieving data</span></td>
                </tr>
            </table>
        </div>
    </body>
</html>