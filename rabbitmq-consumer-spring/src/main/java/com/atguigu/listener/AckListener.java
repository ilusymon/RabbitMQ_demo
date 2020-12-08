package com.atguigu.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Symon
 * @version 1.0
 * @className AckListener
 * @description TODO
 * @date 2020/12/8 10:55
 */
@Component
public class AckListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // ①接收消息
            System.out.println(new String(message.getBody()));

            // ②处理业务逻辑
            System.out.println("处理业务逻辑");
            // int a = 1 / 0;// 出现错误


            //手动签收
            /*
             * 第一个参数：表示收到的标签
             * 第二个参数：如果为true表示可以签收所有的消息
             */
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            e.printStackTrace();
            // ④ 拒绝签收
             /*
            第三个参数：requeue：重回队列。
            设置为true，则消息重新回到queue，broker会重新发送该消息给消费端
             */
            channel.basicNack(deliveryTag, true, true);
        }
    }

    /*@Override
    public void onMessage(Message message) {
        System.out.println(new String(message.getBody()));
    }*/
}
