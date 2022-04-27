package consumer;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;

import com.mongodb.*;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import dal.LiftRideDao;
import model.LiftRide;
import org.bson.BSON;
import org.bson.Document;
//import org.springframework.boot.SpringApplication;


public class SkiersRecvMT {

  // create a hello queue to which the message will be delivered
  private static final String QUEUE_NAME = "liftRides";
  // RabbitMQ EC2 Instance Credentials
  private static final String RABBITMQ_HOST = "localhost";
  private static final String RABBITMQ_USERNAME = "guest";
  private static final String RABBITMQ_PASSWORD = "guest";

  private static final int NUM_THREADS = 32;

  public static final Document toDBobject(LiftRide liftRide) {



    Document doc =  new Document("skierID", liftRide.getSkierId()).append("resortID", liftRide.getResortId())
            .append("seasonID", liftRide.getSeasonId()).append( "dayID", liftRide.getDayId()).append( "time", liftRide.getTime())
            .append("waitTime", liftRide.getTime()).append("lifeId", liftRide.getLiftID());


    return doc;
  }

  public static void main(String[] argv) throws Exception {

    //SpringApplication.run(SkiersRecvMT.class, argv);
    System.out.println(RABBITMQ_USERNAME);

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(RABBITMQ_HOST);
    factory.setUsername(RABBITMQ_USERNAME);
    factory.setPassword(RABBITMQ_PASSWORD);
    final Connection connection = factory.newConnection();

    double CurentTime = System.currentTimeMillis();
    double EndTime = System.currentTimeMillis();
    double TotalTime = EndTime - CurentTime;

   /** MongoClient mongoClient = new MongoClient("localhost", 27017);

    MongoDatabase database =  mongoClient.getDatabase("finalproject");

    MongoCollection collection = database.getCollection("LiftRide");**/


    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          final Channel channel = connection.createChannel();
          channel.queueDeclare(QUEUE_NAME, true, false, false, null);

          // max one message per receiver
          channel.basicQos(1);
          System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            System.out.println("Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");

            // use GSON to parse request jsonString and construct model.LiftRide object
            // e.g. "{'skierId':241, 'resortId':56, 'seasonId':56, 'dayId':56, 'time':386, 'waitTime':59, 'liftID':17}"
            Gson gson = new Gson();
            LiftRide liftRide = gson.fromJson(message, LiftRide.class);




            // pass that object to the DAO layer
            LiftRideDao liftRideDao = new LiftRideDao();
            //System.out.println("****model.LiftRide Object consumed*****" + liftRide.toString());




           /** try  {

              //collection.insertOne(toDBobject(liftRide));
              System.out.println("****model.LiftRide Object consumed*****" + liftRide.toString());

            } catch (Exception e){
                System.out.println(e);
            }**/





            // construct a model.LiftRide object with those values
            liftRideDao.createLiftRide(liftRide);
            System.out.println("****model.LiftRide Object consumed*****" + liftRide.toString());

          };
          // process messages
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
          });
        } catch (IOException ex) {
          Logger.getLogger(SkiersRecvMT.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };

    // start threads and block to receive messages in multi-threaded
    Thread recvs[] = new Thread[NUM_THREADS];
    for (int i = 0; i < NUM_THREADS; i++) {
      recvs[i] = new Thread(runnable);
      recvs[i].start();
      System.out.println("Thread #" + i + " has started.");

    }



  }
}