package com.atguigu.rabbitmq.ps;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author Symon
 * @version 1.0
 * @className Producer
 * @description Publish/Subscribe发布与订阅模式
 * @date 2020/12/7 13:47
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("test_fanout", BuiltinExchangeType.FANOUT, true, false, false, null);
        /* @param queue the name of the queue
                队列名称
         * @param durable true if we are declaring a durable queue (the queue will survive a server restart)
         *      是否持久化，当mq重启之后，还在
         * @param exclusive true if we are declaring an exclusive queue (restricted to this connection)
         *      是否独占。只能有一个消费者监听这队列
         *      当Connection关闭时，是否删除队列
         * @param autoDelete true if we are declaring an autodelete queue (server will delete it when no longer in use)
         *      是否自动删除。当没有Consumer时，自动删除掉
         * @param arguments other properties (construction arguments) for the queue
                参数
         * @return a declaration-confirm method to indicate the queue was successfully declared
         */
        channel.queueDeclare("test_fanout_queue1", true, false, false, null);
        channel.queueDeclare("test_fanout_queue2", true, false, false, null);

        channel.queueBind("test_fanout_queue1", "test_fanout", "");
        channel.queueBind("test_fanout_queue2", "test_fanout", "");
        /*
         * @param exchange the exchange to publish the message to
         * @param routingKey the routing key
         * @param [mandatory] true if the 'mandatory' flag is to be set
         * @param props other properties for the message - routing headers etc
         * @param body the message body
         * @throws java.io.IOException if an error is encountered
         */
        channel.basicPublish("test_fanout", "", null, ("你好,小兔子").getBytes());
        System.out.println("success");
        channel.close();
        connection.close();
    }

}
