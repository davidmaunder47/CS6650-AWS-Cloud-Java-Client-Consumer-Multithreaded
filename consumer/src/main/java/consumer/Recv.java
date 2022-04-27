package consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class Recv {

  // create a hello queue to which the message will be delivered
  private static final String QUEUE_NAME = "liftRides";
  // RabbitMQ EC2 Instance Credentials
  private static final String RABBITMQ_HOST = "35.168.93.165";
  private static final String RABBITMQ_USERNAME = "test";
  private static final String RABBITMQ_PASSWORD = "test";

  public static void main(String[] argv) throws Exception {
    // Consumer Process
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(RABBITMQ_HOST);
    factory.setUsername(RABBITMQ_USERNAME);
    factory.setPassword(RABBITMQ_PASSWORD);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback =
        (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
          System.out.println(" [x] Received '" + message + "'");
        };
    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
  }
}
