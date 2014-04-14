<#-- @ftlvariable name="" type="org.skytrail.views.PersonView" -->
<html>
    <body>
        <!-- calls getDBUser().getFullName() and sanitizes it -->
        <h1>Hello, ${DBUser.fullName?html}!</h1>
    </body>
</html>
