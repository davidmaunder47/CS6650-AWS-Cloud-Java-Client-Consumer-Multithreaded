import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.ResortIDSeasonsBody;

import static java.util.concurrent.ThreadLocalRandom.current;

class Phase implements Runnable {
  private String numPhase;
  private int posts_per_thread;
  private CountDownLatch countSignal;
  private int startTime;
  private int endTime;
  private int skiHigh;
  private int skiLow;
  private boolean countSignalBool = false;
  private CSVWriter writer;

  String basePath = "http://localhost:8080/server_war_exploded";
  //String basePath = "http://54.214.229.166/server-v1.0";
  //String basePath = "http://ApacheLoadBalancer-1087266376.us-west-2.elb.amazonaws.com/server-v1.0"; //network load
  // balancer
  //String basePath = "http://54.202.80.249:8080/server-v1.0"; // tomcat1


  public static int sucecess = 0;
  public static int failure = 0;
  public static Vector<Integer> vectorCodes = new Vector<Integer>();
  // public static  Vector<Long> vectorStart = new Vector<Long>();
  // public static  Vector<Long> vectorEnd = new Vector<Long>();
  public static Vector<Long> vectorTotal = new Vector<Long>();
  public static int threadcounter = 0;

  Phase(
      String numPhase,
      int posts_per_thread,
      CountDownLatch countSignal,
      int startTime,
      int endTime,
      int skiLow,
      int skiHigh,
      CSVWriter writer) {
    this.numPhase = numPhase;
    this.posts_per_thread = posts_per_thread;
    this.countSignal = countSignal;
    this.startTime = startTime;
    this.endTime = endTime;
    this.skiHigh = skiHigh;
    this.skiLow = skiLow;
    this.countSignalBool = true;
    this.writer = writer;
  }

  Phase(
      String numPhase,
      int posts_per_thread,
      int startTime,
      int endTime,
      int skiLow,
      int skiHigh,
      CSVWriter writer) {
    this.numPhase = numPhase;
    this.posts_per_thread = posts_per_thread;
    this.countSignal = null;
    this.startTime = startTime;
    this.endTime = endTime;
    this.skiHigh = skiHigh;
    this.skiLow = skiLow;
    this.writer = writer;
  }

  public void run() {
    for (int i = 0; i < this.posts_per_thread; i++) {
      long reqStartTime = System.currentTimeMillis();
      // vectorStart.add(sTime);
      SkiersApi apiInstance = new SkiersApi();
      ResortsApi resortApiInstance = new ResortsApi();
      ApiClient client = apiInstance.getApiClient();
      client.setBasePath(basePath);

      Integer resortID = 56; // Integer | ID of the resort
      String seasonID = "56"; // String | ID of the season
      String dayID = "56"; // String | ID of the day
      Integer skierID =
          current().nextInt(this.skiLow, this.skiHigh + 1); // Integer | ID of the skier;
      Integer time = current().nextInt(100, 500);
      Integer liftID = current().nextInt(0, 20);
      Integer waitTime = current().nextInt(0, 100);
      LiftRide liftRide = new LiftRide();
      // instantiate liftRide object to avoid empty request body
      liftRide.setTime(time);
      liftRide.setLiftID(liftID);
      liftRide.setWaitTime(waitTime);

      Integer randomYear = current().nextInt(2000, 2022);
      Integer randomResortID = current().nextInt(0, 100);
      

      try {
        ApiResponse res =
            apiInstance.writeNewLiftRideWithHttpInfo(liftRide, resortID, seasonID, dayID, skierID);
        //ResortIDSeasonsBody resortIDSeasonsBody = new ResortIDSeasonsBody();
        //resortIDSeasonsBody.setYear(randomYear.toString());
        //resortApiInstance.addSeasonWithHttpInfo(resortIDSeasonsBody, 100);
        //resortApiInstance.addSeason(resortIDSeasonsBody, randomResortID);
        sucecess++;
        vectorCodes.add(new Integer(res.getStatusCode()));

        long latency = System.currentTimeMillis() - reqStartTime;
        String statusCode = String.valueOf(res.getStatusCode());

        // Add record to csv: {"Phase Number", "Start Time", "Request Type", "Latency", "Response
        // Code"}
        String[] record = {
          numPhase, String.valueOf(reqStartTime), "POST", String.valueOf(latency), statusCode
        };
        writer.writeNext(record);
        //        System.out.println(record[0]);
        // System.out.println(res.getStatusCode());
        // Integer verticalResult = apiInstance.getSkierDayVertical(resortID, seasonID, dayID,
        // skierID);
        // System.out.println(verticalResult);
      } catch (ApiException e) {
        failure++;
        System.err.println("Exception when calling SkiersApi#getSkierDayVertical");
        e.printStackTrace();
      }
      threadcounter++;

      long reqEndtime = System.currentTimeMillis();
      // vectorEnd.add(endtime);
      long totaltime = reqEndtime - reqStartTime;
      vectorTotal.add(totaltime);
      //            System.out.println(threadcounter);
    }
    if (countSignalBool) {
      this.countSignal.countDown();
    }
  }
}

public class Main {
  public static void main(String[] args) throws InterruptedException, FileNotFoundException {
    long programStart = System.currentTimeMillis();
    int maxThreads = 1024;
    // int ski_lifts = 45;
    int numRuns = 20;
    int numSkiers = 100000;
    int numthreads = 32;
    /*Vector vector = new Vector<Skiers>(numSkiers);
    for (int i = 0; i < numSkiers; i++) {
        vector.add(i, i);
    }*/

    // For Phase 1
    int posts_per_thread = (int) Math.round(numRuns * 0.2 * (numSkiers / numthreads / 4));
    int startTime1 = 0;
    int endTime1 = 90;
    int phase1Amount = (int) Math.round(numthreads / 4);
    CountDownLatch countSignal1 = new CountDownLatch((int) Math.round(phase1Amount * 0.20));

    // For Phase 2
    int posts_per_thread2 = (int) Math.round(numRuns * 0.6 * (numSkiers / numthreads));
    int startTime2 = 91;
    int endTime2 = 360;
    int phase2Amount = (int) Math.round(numthreads);
    CountDownLatch countSignal2 = new CountDownLatch((int) Math.round(phase2Amount * 0.20));

    // For Phase 3
    int posts_per_thread3 = (int) Math.round(numRuns * 0.1 * (numSkiers / numthreads / 4));
    int startTime3 = 361;
    int endTime3 = 420;
    int phase3Amount = (int) Math.round(numthreads / 4);

    // Create file object for file placed at location specified by filepath (../data/logs/)
    File file =
        new File(
            "../data/logs/lab7_"
                + numRuns
                + "runs_"
                + numSkiers
                + "skiers_"
                + numthreads
                + "threads.csv");

    try {
      // create FileWriter object with file as parameter
      FileWriter outputfile = new FileWriter(file);

      // create CSVWriter object filewriter object as parameter
      CSVWriter writer = new CSVWriter(outputfile);

      // adding header to csv
      String[] header = {"Phase Number", "Start Time", "Request Type", "Latency", "Response Code"};
      writer.writeNext(header);

      // Phase1
      System.out.println("Start of Phase1: ");
      ExecutorService executor = Executors.newFixedThreadPool(phase1Amount);

      // Thread myThreads[] = new Thread[phase1Amount];
      for (int i = 0; i < phase1Amount - 1; i++) {
        executor.submit(
            new Phase(
                "1", posts_per_thread, countSignal1, startTime1, endTime1, i, i * 64, writer));
        // Phase phase = new Phase(posts_per_thread,countSignal1,startTime1,endTime1, i, i*64);
        // myThreads[i] = new Thread(phase);
        // myThreads[i].start();

        // countdown latch// have a wait somwhere here

      }

      /*for (int ii = 0; ii < phase1Amount-1; ii++) {
          myThreads[ii].join();
      }*/
      countSignal1.await();

      // Phase 2
      System.out.println("Start of Phase2: ");
      // Thread myThreads2[] = new Thread[phase2Amount];
      ExecutorService executor2 = Executors.newFixedThreadPool(phase2Amount);
      for (int j = 0; j < phase2Amount - 1; j++) {
        executor2.submit(
            new Phase(
                "2", posts_per_thread2, countSignal2, startTime2, endTime2, 0, numSkiers, writer));
        // new Phase(posts_per_thread,countSignal1,startTime1,endTime1, i, i*64);
        // Phase phase = new Phase(posts_per_thread2,countSignal2,startTime2,endTime2, 0,
        // numSkiers);
        // myThreads2[j] = new Thread(phase);
        // myThreads2[j].start();
        // countdown latch// have a wait somwhere here

      }

      /*for (int jj = 0; jj < phase2Amount-3; jj++) {
          myThreads2[jj].start();
      }*/
      countSignal2.await();

      // ExecutorService executor = Executors.newFixedThreadPool(numthreads);
      // Phase 3
      // Thread myThreads3[] = new Thread[phase3Amount];
      System.out.println("Start of Phase3: ");
      ExecutorService executor3 = Executors.newFixedThreadPool(phase3Amount);
      for (int k = 0; k < phase3Amount - 1; k++) {
        executor3.submit(
            new Phase("3", posts_per_thread3, startTime3, endTime3, k, k * 64, writer));
        // Phase phase = new Phase(posts_per_thread3,startTime3,endTime3, k, k*64);
        // myThreads3[k] = new Thread(phase);
        // myThreads3[k].start();
        // countdown latch// have a wait somwhere here
      }

      executor.shutdown();
      executor2.shutdown();
      executor3.shutdown();

      long programEnd = System.currentTimeMillis();

      StringBuilder sb = new StringBuilder();
      long sum = 0;
      for (int i = 0; i < Phase.vectorCodes.size(); i++) {
        sb.append(Phase.vectorCodes.get(i));
        sb.append(",");
        /*sb.append(Phase.vectorStart.get(i));
        sb.append(",");
        sb.append(Phase.vectorEnd.get(i));
        sb.append(",");*/
        sb.append(Phase.vectorTotal.get(i));
        sb.append("\n");
        sum += Phase.vectorTotal.get(i);
      }

      long meanResponseTime = sum / Phase.vectorTotal.size();

      Collections.sort(Phase.vectorTotal);
      long medianResponseTime = Phase.vectorTotal.get((Phase.vectorTotal.size() / 2));
      int throughput =
          (int)
              (((double) Phase.vectorCodes.size())
                  / (programEnd - programStart)
                  * 1000); // requests per second, casting to double
      System.out.println(throughput);

      //        sb.append(meanResponseTime);
      //        sb.append(",");
      //        sb.append(medianResponseTime);
      //        sb.append(",");
      //        sb.append(throughput);
      //        sb.append(",");
      //
      //        PrintWriter pw = new PrintWriter("lab4_2.csv");
      //        pw.write(sb.toString());
      //        pw.flush();
      //        pw.close();
      System.out.printf(
          "total requests: %d\nmean response time: %d\nmedian response time: %d\nThroughput: %d\nProgramStartTime: %d\nProgramEndtime: %d\nTotalprogramTime: %d\n",
          Phase.vectorCodes.size(),
          meanResponseTime,
          medianResponseTime,
          throughput,
          programStart,
          programEnd,
          programEnd - programStart);

      System.out.printf(
          "success: %d\nfailure %d\nduration: %d\n",
          Phase.sucecess, Phase.failure, programEnd - programStart);

      System.out.println("Finished");
      // closing writer connection
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
