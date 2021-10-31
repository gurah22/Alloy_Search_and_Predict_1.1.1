<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <title>Alloy Search and Predict</title>
        <meta http-equiv="content-type" content="text/html;charset=utf-8" />

        <script src='https://www.google.com/recaptcha/api.js'></script>
        <script src="/js/jquery-1.10.1.min.js"></script>
        <script src="/js/math.min.js"></script>
        <script src="/js/search-page.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                SEARCH_PAGE.setCalculationLimit(${calculationLimit});
                SEARCH_PAGE.refreshElementDropdowns();
                SEARCH_PAGE.update_step_size_dropdown();
                $("#search-request").submit(function(event) {
                    var valid = true;
                    var form = event.currentTarget;

                    var number_of_wildcard_elements = ${requestWildcards} ? parseInt(form[0].value) : 0;
                    var number_of_hand_picked_elements = parseInt(form[1].value);

                    var has_valid_number_of_elements = true;
                    has_valid_number_of_elements = update_number_of_elements_error_message(
                        number_of_wildcard_elements + number_of_hand_picked_elements < 2,
                        "There must be at least 2 elements in total",
                        has_valid_number_of_elements
                    );

                    valid = valid && update_number_of_elements_error_message(
                        number_of_wildcard_elements + number_of_hand_picked_elements > 7,
                        "There must not be more than 7 elements in total",
                        has_valid_number_of_elements
                    );

                    var recaptcha_value = form[11].value;
                    if (!recaptcha_value) {
                        $("#recaptcha_error").text("Please tick the box");
                        valid = false;
                    } else {
                        $("#recaptcha_error").text("");
                    }

                    if (!valid) {
                        event.preventDefault();
                    }
                });
            });


            function update_number_of_elements_error_message(error_condition,
                                                             error_message,
                                                             valid_number_of_elements) {
                if (!valid_number_of_elements) {
                    return false;
                }

                if (error_condition) {
                    $("#numberOfWildcardElements_error").text(error_message);
                    $("#numberOfHandPickedElements_error").text(error_message);
                    return false;
                }

                $("#numberOfWildcardElements_error").text("");
                $("#numberOfHandPickedElements_error").text("");
                return true;
            }
        </script>

        <link rel="stylesheet" type="text/css" href="/css/alloyasap.css">
    </head>

    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

    <%@ include file="/WEB-INF/jsp/include/taglibs.jsp" %>

    <body>

        <%@ include file="/WEB-INF/jsp/include/header.jsp" %>

        <div style="width:50em">
            <form id="search-request" action="/calculationRequestQueue" method="POST">
                <c:if test="${not requestWildcards}">
                    <input type="hidden" id="numberOfWildcardElements" name="numberOfWildcardElements" value="0"></input>
                </c:if>
                <table style="border-style: solid; border-color: #D0D0D0">
                    <tr style="background:lightgreen">
                        <td colspan="2" style="text-align:center">
                            <c:choose>
                                <c:when test="${requestWildcards}">
                                    <h3>Use wildcard elements to search the periodic table</h3>
                                </c:when>
                                <c:otherwise>
                                    <h3>Search a range of compositions of a specific system</h3>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <c:if test="${requestWildcards}">
                        <tr <c:if test="${not requestWildcards}">style="background:lightgreen"</c:if>>
                            <td align="right">
                                Number of "wildcard" elements:<br/>
                            </td>
                            <td>
                                <select id="numberOfWildcardElements" name="numberOfWildcardElements" onchange="SEARCH_PAGE.update_step_size_dropdown();">
                                    <option>1</option>
                                    <option>2</option>
                                    <option>3</option>
                                    <option>4</option>
                                </select>
                                <span id="numberOfWildcardElements_error" class="error_message"/>
                            </td>
                        </tr>
                    </c:if>
                    <tr <c:if test="${requestWildcards}">style="background:lightgreen"</c:if>>
                        <td align="right">Specific number <c:if test="${requestWildcards}">of</c:if> elements:</td>
                        <td>
                            <select id="numberOfHandPickedElements" name="numberOfHandPickedElements" onchange="SEARCH_PAGE.refreshElementDropdowns(); SEARCH_PAGE.update_step_size_dropdown();">
                                <c:if test="${requestWildcards}">
                                    <option>0</option>
                                    <option selected="selected">1</option>
                                </c:if>
                                <option>2</option>
                                <option>3</option>
                                <option>4</option>
                                <option>5</option>
                            </select>
                            <span id="numberOfHandPickedElements_error" class="error_message"/>
                        </td>
                    </tr>
                    <tr <c:if test="${requestWildcards}">style="background:lightgreen"</c:if>>
                        <td align="right">Select elements:</td>
                        <td>
                            <select id="element1" name="element1" onchange="SEARCH_PAGE.restrictToOneOfEachElement(0);">
                                <c:forEach var="element" items="${availableElements}">
                                    <option value="<c:out value="${element}"/>"><c:out value="${element}"/></option>
                                </c:forEach>
                            </select>
                            <select id="element2" name="element2" onchange="SEARCH_PAGE.restrictToOneOfEachElement(1);">
                                <c:forEach var="element" items="${availableElements}">
                                    <option value="<c:out value="${element}"/>"><c:out value="${element}"/></option>
                                </c:forEach>
                            </select>
                            <select id="element3" name="element3" onchange="SEARCH_PAGE.restrictToOneOfEachElement(2);">
                                <c:forEach var="element" items="${availableElements}">
                                    <option value="<c:out value="${element}"/>"><c:out value="${element}"/></option>
                                </c:forEach>
                            </select>
                            <select id="element4" name="element4" onchange="SEARCH_PAGE.restrictToOneOfEachElement(3);">
                                <c:forEach var="element" items="${availableElements}">
                                    <option value="<c:out value="${element}"/>"><c:out value="${element}"/></option>
                                </c:forEach>
                            </select>
                            <select id="element5" name="element5" onchange="SEARCH_PAGE.restrictToOneOfEachElement(4);">
                                <c:forEach var="element" items="${availableElements}">
                                    <option value="<c:out value="${element}"/>"><c:out value="${element}"/></option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr <c:if test="${not requestWildcards}">style="background:lightgreen"</c:if>>
                        <td style="text-align:right">
                            Element concentration step size:
                            <br>
                        </td>
                        <td><select id="stepSize" name="stepSize"/></td>
                    </tr>
                    <tr <c:if test="${requestWildcards}">style="background:lightgreen"</c:if>>
                        <td align="right">Number of result rows to keep:</td>
                        <td>
                            <select id="numberOfResults" name="numberOfResults">
                                <option>10</option>
                                <option>50</option>
                                <option>100</option>
                                <option>1000</option>
                            </select>
                        </td>
                    </tr>
                    <tr <c:if test="${not requestWildcards}">style="background:lightgreen"</c:if>>
                        <td align="right">Rank all result rows according to optimisation parameter:</td>
                        <td>
                            <select name="optimisationParameter">
                                <c:forEach var="item" items="${outputParameters}">
                                    <option value="<c:out value="${item}"/>"><c:out value="${item.displayText}"/></option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr <c:if test="${not requestWildcards}">style="background:lightgreen"</c:if>>
                        <td align="right" value="targetValue">Target value for optimisation parameter:</td>
                        <td><input type="text" value="0" name="targetValue"/></td>
                    </tr>
                    <tr <c:if test="${requestWildcards}">style="background:lightgreen"</c:if>>
                        <td style="text-align:right">To protect against misuse of our free service,<br>please tick the box:</td>
                        <td>
                            <div class="g-recaptcha" data-sitekey="6Le-5wcTAAAAALZY9IW0S409JmECaYDxdK6xnYYx"></div>
                            <span id="recaptcha_error" class="error_message"/>
                        </td>
                    </tr>
                    <tr <c:if test="${not requestWildcards}">style="background:lightgreen"</c:if>>
                        <td colspan="2" style="text-align:center"><input type="submit" value="Search"></input></td>
                    </tr>

                </table>
            </form>

        </div>
<b><small>If you find this website useful please provide a monetary <a href="https://www.patreon.com/alloyasap"target="_blank">donation</a>.</small></b>
    </body>

</html>
