package com.bsds.group101.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "StatsServlet", value = "/StatsServlet")
public class StatsServlet extends HttpServlet {
  // TODO try dummy for Lab4
  String dummyResponse =
      "{"
          + "\"endpointStats\": ["
          + "{"
          + "\"URL\": \"/resorts\","
          + "\"operation\": \"GET\","
          + "\"mean\": 11,"
          + "\"max\": 198"
          + "}, {"
          + "\"URL\": \"/resorts/{resortID}/seasons\","
          + "\"operation\": \"GET\","
          + "\"mean\": 7,"
          + "\"max\": 120"
          + "}, {"
          + "\"URL\": \"/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}\","
          + "\"operation\": \"GET\","
          + "\"mean\": 9,"
          + "\"max\": 100"
          + "}, {"
          + "\"URL\": \"/skiers/{skierID}/vertical\","
          + "\"operation\": \"GET\","
          + "\"mean\": 6,"
          + "\"max\": 114"
          + "}]}";

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // return response in json
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().write(dummyResponse);
  }
}
