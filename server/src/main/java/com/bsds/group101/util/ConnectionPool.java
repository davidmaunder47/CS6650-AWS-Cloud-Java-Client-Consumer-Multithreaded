package com.bsds.group101.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;

public class ConnectionPool extends BasePooledObjectFactory<Channel> {
    String queueName;
    Connection connection;

    public ConnectionPool(String QueueName, Connection connection) {
        this.queueName = QueueName;
        this.connection = connection;
    }

    @Override
    public Channel create() throws IOException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, true, false, false, null);
        return channel;
    }

    /** Use the default PooledObject implementation. */
    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<Channel>(channel);
    }

    /** When an object is returned to the pool, clear the buffer. */
    @Override
    public void passivateObject(PooledObject<Channel> pooledObject) {
        //    pooledObject.getObject().setLength(0);
    }
}