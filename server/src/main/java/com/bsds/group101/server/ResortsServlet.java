package com.bsds.group101.server;

import com.bsds.group101.dal.ResortDao;
import com.bsds.group101.model.ResortSeason;
import com.bsds.group101.model.Year;
import com.bsds.group101.util.ConnectionPool;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.validator.routines.UrlValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@WebServlet(name = "ResortsServlet", value = "/ResortsServlet")
public class ResortsServlet extends HttpServlet {
  // create a hello queue to which the message will be delivered
  private static final String QUEUE_NAME = "resortSeasons";
  // RabbitMQ EC2 Instance Credentials
  private static final String RABBITMQ_HOST = "localhost";
  private static final String RABBITMQ_USERNAME = "guest";
  private static final String RABBITMQ_PASSWORD = "guest";

  // Parse path variables into a Hashmap
  Map<String, String> resortPathMap = new ConcurrentHashMap<>();

  // Instantiate connection pool for long existing
  private ConnectionFactory factory;
  private ObjectPool<Channel> channelObjectPool;

  public ResortsServlet() throws IOException, TimeoutException {
    this.factory = new ConnectionFactory();
    this.factory.setHost(RABBITMQ_HOST);
    this.factory.setUsername(RABBITMQ_USERNAME);
    this.factory.setPassword(RABBITMQ_PASSWORD);
    this.factory.setPort(5672);
    // Pooling the connection pool of RabbitMQ
    this.channelObjectPool =
            new GenericObjectPool<>(new ConnectionPool(QUEUE_NAME, factory.newConnection()));
  }

  // TODO try dummy for Lab4
  String dummyResortsList =
          "{"
                  + "\"resorts\": ["
                  + "{"
                  + "\"resortName\": \"stringA\","
                  + "\"resortID\": \"0\""
                  + "}, {"
                  + "\"resortName\": \"stringB\","
                  + "\"resortID\": \"3\""
                  + "}]}";

  String dummySeasonsList = "{" + "\"seasons\": [" + "\"2008\"," + "\"2022\"" + "]}";

  /**
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    // return response in json
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // urlPath is the string after url pattern described in web.xml
    String urlPath = request.getPathInfo();

    // get a list of ski resorts in the database
    if (urlPath == null || urlPath.isEmpty()) {
      // initiate ResortDao object for data access
      response.setStatus(HttpServletResponse.SC_OK);
      ResortDao resortDao = new ResortDao();
      //      PrintWriter out = response.getWriter();
      //      ArrayList<Integer> resortsList = resortDao.getResortsList();
      //      StringBuilder jsonOutputString = new StringBuilder("{\"resorts\": [");
      //      for (Integer resortId : resortsList){
      //        jsonOutputString.append(String.format("{\"resortID\": \"%s\"},", resortId));
      //      }
      //      jsonOutputString.append("]}");
      //      out.write(jsonOutputString.toString());
      //      out.flush();
      response.getWriter().write(resortDao.getResortsList().toString());
    }

    // check full url at UrlValidator
    String reqUrl = request.getRequestURL().toString();
    // check path match in custom api rules
    String[] urlParts = urlPath.split("/");

    if (!isUrlValid(urlParts, reqUrl)) {
      // if not valid url, return 400 error - Invalid inputs
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      //      response.getWriter().write("{ \"message\":\"Invalid inputs supplied\"}");
      response
              .getWriter()
              .write("{ \"message\":\"" + isUrlValid(urlParts, reqUrl) + reqUrl + "\"}");

    } else {
      // initiate ResortDao object for data access
      ResortDao resortDao = new ResortDao();
      int resortId = Integer.parseInt(resortPathMap.get("resortID"));

      // GET/resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers
      if (urlParts[urlParts.length - 1].equals("skiers")) {
        int seasonId = Integer.parseInt(resortPathMap.get("seasonID"));
        int dayId = Integer.parseInt(resortPathMap.get("dayID"));
        // return 200 success message
        response.setStatus(HttpServletResponse.SC_OK);
        //        response.getWriter().write("{ \"time\":\"Mission Ridge\",
        // \"numSkiers\":\"78999\"}");
        PrintWriter out = response.getWriter();
        int numUniqueSkiers = resortDao.getNumberOfSkiersAtResort(resortId, seasonId, dayId);
        String jsonOutputString =
                String.format("{\"resortID\": \"%s\", \"numSkiers\": %d}", resortId, numUniqueSkiers);
        out.write(jsonOutputString);
        out.flush();
      } else if (urlParts[urlParts.length - 1].equals("seasons")) {
        // GET/resorts/{resortID}/seasons
        // return 200 success message
        response.setStatus(HttpServletResponse.SC_OK);
        //        response.getWriter().write(dummySeasonsList);
        response.getWriter().write(resortDao.getSeasonsListAtResort(resortId).toString());
      }
    }
  }

  /**
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    // return response in json
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // urlPath is the string after url pattern described in web.xml
    String urlPath = request.getPathInfo();

    // empty url after /resorts path
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("{ \"message\":\"Resort not found\"}");
      return;
    }

    // check full url at UrlValidator
    String reqUrl = request.getRequestURL().toString();
    // check path match in custom api rules
    String[] urlParts = urlPath.split("/");

    // case urlParts = [, {resortID}, seasons, {seasonID}, days, {dayID}, skiers]
    if (!isUrlValid(urlParts, reqUrl)) {
      // if not valid url, return 400 error - Invalid inputs
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("{ \"message\":\"Invalid inputs supplied\"}");

    } else {
      // new season created - 201 success message
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.getWriter().write("new season created");

      // join request body as a string.
      // e.g. "{'year': 2019}"
      String requestJsonString = request.getReader().lines().collect(Collectors.joining());

      // use GSON to parse request jsonString and construct Year object
      Gson gson = new Gson();
      Year year = gson.fromJson(requestJsonString, Year.class);

      // initiate ResortSeason object for single record for producing message
      int resortId = Integer.parseInt(resortPathMap.get("resortID"));
      int seasonId = year.getYear();
      ResortSeason resortSeason = new ResortSeason(resortId, seasonId);

      // Producer Process
      // e.g. "{'resortId':1, 'seasonId':2009}"
      String message = resortSeason.toString();
      try {
        // Instead of creating a new channel each time, borrow from GenericObjectPool
        Channel channel = channelObjectPool.borrowObject();
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");
        // Free the use of this channel in multi-threaded
        channelObjectPool.returnObject(channel);
      } catch (Exception e) {
        e.printStackTrace();
      }

      //      ConnectionFactory factory = new ConnectionFactory();
      //      factory.setHost(RABBITMQ_HOST);
      //      factory.setUsername(RABBITMQ_USERNAME);
      //      factory.setPassword(RABBITMQ_PASSWORD);
      //      try (Connection connection = factory.newConnection();
      //          Channel channel = connection.createChannel()) {
      //        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      //        channel.basicPublish("", QUEUE_NAME, null,
      // message.getBytes(StandardCharsets.UTF_8));
      //        System.out.println(" [x] Sent '" + message + "'");
      //      } catch (TimeoutException e) {
      //        e.printStackTrace();
      //      }
    }
  }

  /**
   * Check if path pattern matches at
   * /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
   *
   * @param urlParts
   * @param reqUrl
   * @return
   */
  private boolean isUrlValid(String[] urlParts, String reqUrl) {
    // url validator help module
    UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);

    System.out.println(Arrays.toString(urlParts));
    System.out.println(urlParts.length);

    // Check if the url is valid through UrlValidator
    if (urlValidator.isValid(reqUrl)) {
      // case urlParts = [, {resortID}, seasons, {seasonID}, days, {dayID}, skiers]
      if (urlParts.length == 7) {
        String resortID = urlParts[1];
        //        System.out.println(resortID);
        resortPathMap.put("resortID", resortID);

        for (int i = 2; i < urlParts.length; i++) {
          switch (urlParts[i]) {
            case "seasons":
              String seasonID = urlParts[i + 1];
              resortPathMap.put("seasonID", seasonID);
              //              System.out.println(seasonID);
              break;
            case "days":
              String dayID = urlParts[i + 1];
              resortPathMap.put("dayID", dayID);
              //              System.out.println(dayID);
              break;
            default:
              continue;
          }
        }
        // last check if all info are parsed into hashmap
        return resortPathMap.size() == 3;
      }
      // case urlParts = [, {resortID}, seasons]
      if (urlParts.length == 3 && urlParts[2].equals("seasons")) {
        String resortID = urlParts[1];
        resortPathMap.clear();
        resortPathMap.put("resortID", resortID);
        return resortPathMap.size() == 1;
      }
    }
    return false;
  }
}
