
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <title>Alloy Search and Predict</title>
        <meta http-equiv="content-type" content="text/html;charset=utf-8" />
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
            <p>
                <h3>How-To</h3>
                <ol>
                    Alloys Search and Predict is an algorithm that performs calculations of enthalpy, entropy and ultimately alloy forming ability, for different element combinations and concentrations.
                    <br>
                    There are three functionalities available on this website:
                    </br>
                    <br>
                        <li>
                            <u>Basic</u>
                            <br>
                            This function will allow the user to specify individual alloys in a text window. Up to 100 alloys can be specified with a maximum of 7 elements in each alloy.
                            </br>
                        </li>
                        <p>
                        <li>
                            <u>Range Search</u>
                            <br>
                                Use this function to search through a range of compositions of a single system. Specify a certain step size and every element will be varied to search through all of phase space.
                            </br>
                        </li>
                        </p>
                        <p>
                        <li>
                            <u>Advanced Search</u>
                            <br>
                                Include wildcard elements in your search to test for every different combination of elements on the periodic table. The concentration can also be varied according the a specified step size.
                            </br>
                        </li>
                        </p>
                </ol>
                <ol>
                        <b><u>Reading the output file</u></b>
                        <br>
                            After a successful calculation you will receive a .csv file. There will be 13 columns which correspond to the following values (not necessarily in this order):
                           <li>System: The alloy composition as specified in the input.</li>
                           <li>delta: A measure of the difference in atomic radii between the constituent elements (unitless).</li>
                           <li>Hmax: The maximum magnitude of the enthalpy of mixing for intermetallic/multiphase formation (kJ/mol).</li>
                           <li>Hss: The enthalpy of mixing of the solid solution (kJ/mol).</li>
                           <li>Hint: The enthalpy of mixing for the intermetallic compound denoted by the eleAB tag.</li>
                           <li>VEC: Valence electrons per atom.</li>
                           <li>eleAB: The binary mixture that yields the highest magnitude (Hmax).</li>
                           <li>pTemp: The predicted precipitation/segregation temperature of eleAB (K).</li>
                           <li>tMax: The maximum melting temperature of one of the elements within the alloy (K).</li>
                           <li>tm: The theoretical melting temperature of the alloy (K).</li>
                           <li>phi: The single phase solid solution forming ability (>= 1 will form a solid solution at tm) (unitless).</li>
                           <li>smix: Configurational entropy (entropy of mixing) of the solid solution. (j/K/mol).</li>
                           <li>Price: Estimated price (from Sigma-Aldrich) (USD/kg).</li>
                           <li>Structure: Predicted structure.</li>
                        </br>
                    </br>
                </ol>
            </p>
            <b><small>If you find this website useful please provide a monetary <a href="https://www.patreon.com/alloyasap"target="_blank">donation</a>.</small></b>
        </div>

    </body>

</html>
