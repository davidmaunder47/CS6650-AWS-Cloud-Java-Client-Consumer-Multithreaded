<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Lab4</title>
</head>
<body>
<h1><%= "Lab 4 - Milestone 1" %>
</h1>
<ul>
    <li><b>resorts:
        Ski Resort Lift Data information</b>
        <ol>
            <li><a href="resorts">200 - get a list of ski resorts in the database</a></li>
            <li><a href="resorts/1/seasons/2019/days/3/skiers">200 - get number of unique
                skiers at resort/season/day</a></li>
            <li><a href="resorts/1/seasons">200 - get a list of seasons for the specified
                resort</a></li>
            <li><a href="resorts/1/seasons/2019//////days/3/skiers">400 - ResortsServlet Error</a>
            </li>
        </ol>
    </li>
    <br/>

    <li><b>skiers:
        Information about skiers and their lift usage</b>
        <ol>
            <li><a href="skiers/1/seasons/2019/days/3/skiers/33">200 - get ski day vertical for a
                skier</a></li>
            <li><a href="skiers/3/vertical">200 - get the total vertical for the skier for specified
                seasons at the specified resort</a></li>
            <li><a href="skiers/1/seasons/2019//////days/3/skiers/">400 - SkiersServlet Error</a>
            </li>
            <li><a href="skiers">404 - Data not found</a></li>
        </ol>
    </li>
    <br/>

    <li><b>statistics:
        Server side response time values</b>
        <ol>
            <li><a href="statistics">200 - StatsServlet</a></li>
        </ol>
    </li>

</ul>
</body>
</html>