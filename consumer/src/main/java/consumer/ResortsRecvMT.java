package consumer;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.ResortSeason;

public class ResortsRecvMT {

  // create a hello queue to which the message will be delivered
  private static final String QUEUE_NAME = "resortSeasons";
  // RabbitMQ EC2 Instance Credentials
  private static final String RABBITMQ_HOST = "localhost";
  private static final String RABBITMQ_USERNAME = "guest";
  private static final String RABBITMQ_PASSWORD = "guest";

  private static final int NUM_THREADS = 100;

  public static void main(String[] argv) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(RABBITMQ_HOST);
    factory.setUsername(RABBITMQ_USERNAME);
    factory.setPassword(RABBITMQ_PASSWORD);
    final Connection connection = factory.newConnection();

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
            ResortSeason resortSeason = gson.fromJson(message, ResortSeason.class);

            System.out.println("****model.LiftRide Object consumed*****" + resortSeason.toString());

            // pass that object to the DAO layer
            //ResortSeasonDao resortSeasonDao = new ResortSeasonDao();



            // construct a model.LiftRide object with those values
            //resortSeasonDao.createResortSeason(resortSeason);
          };
          // process messages
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
          });
        } catch (IOException ex) {
          Logger.getLogger(ResortsRecvMT.class.getName()).log(Level.SEVERE, null, ex);
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