
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <title>Alloy Search and Predict</title>
        <meta http-equiv="content-type" content="text/html;charset=utf-8" />
        <script src='https://www.google.com/recaptcha/api.js'></script>
        <script src="/js/jquery-1.10.1.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $("#systems-request").submit(function(event) {
                    var valid = true;
                    var form = event.currentTarget;

                    var recaptcha_value = form[1].value;
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
        </script>
        <link rel="stylesheet" type="text/css" href="/css/alloyasap.css">
    </head>











    <body>

        <div style="background:#D0D0D0;">
    <table>
        <tr>
            <td>
                <h2>Alloy Search and Predict</h2>
            </td>
            <td>
                <table>
                    <td style="background:lightgreen; text-align:center">
                        <a href="/">Home</a>
                    </td>
                    <td style="background:lightgreen; text-align:center">
                        <a href="/how-to.htm">How-To</a>
                    </td>
                    <td style="background:lightgreen; text-align:center">
                        <a href="/systems.htm">Basic</a>
                    </td>
                    <td style="background:lightgreen; text-align:center">
                        <a href="/concentration-search.htm">Range<br/>Search</a>
                    </td>
                    <td style="background:lightgreen; text-align:center">
                        <a href="/element-search.htm">Advanced<br/>Search</a>
                    </td>
                    <td style="background:lightgreen; text-align:center">
                        <a href="/recent-publications.htm">Recent<br/>Publications</a>
                    </td>
                </table>
            </td>
        </tr>
    </table>

    <table>
        <tr>
            <td align="left">
                <b>Authors: D. J. M. King and A. G. McGregor</b>
            </td>
        </tr>
        <tr>
            <td align="left">
                <b>Email: daniel.miks AT live.com</b>
            </td>
        </tr>
    </table>
</div>


        <div>
            <form id="systems-request" action="/systems.csv" method="POST">
                <table style="border-style: solid; border-color: #D0D0D0">
                    <tr style="background:lightgreen">
                        <td colspan="2" style="text-align:center">
                            <h3>Simulate specific alloys</h3>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <ul>
                                Specify up to 100 individual alloy compositions with a maximum of 7 elements.
                                <p>This function will accept atomic ratio, atomic percent and atomic fractions.</p>
                        <td>
                    </tr>
                    <tr style="background:lightgreen">
                        <td style="text-align:right">
                            Alloy compositions:
                            <br/>
                            (with 3 examples)
                        </td>
                        <td>
                            <textarea name="systemsRequest" cols="40" rows="10">Al3CoCrFeNi
Co0.25Cr0.25Fe0.25Ni0.25
CoCrFe</textarea>
                        <td>
                    </tr>
                    <tr>
                        <td style="text-align:right">
                            To protect against misuse of our free service,
                            <br/>
                            please tick the box:
                        </td>
                        <td>
                            <div class="g-recaptcha" data-sitekey="6Le-5wcTAAAAALZY9IW0S409JmECaYDxdK6xnYYx"></div>
                            <span id="recaptcha_error" class="error_message"/>
                        </td>
                    </tr>
                    <tr style="background:lightgreen">
                        <td colspan="2" style="text-align:center"><input type="submit" value="Simulate"/></td>
                    </tr>
                </table>
            </form>

        </div>
<b><small>If you find this website useful please provide a monetary <a href="https://www.patreon.com/alloyasap"target="_blank">donation</a>.</small></b>
    </body>

</html>
