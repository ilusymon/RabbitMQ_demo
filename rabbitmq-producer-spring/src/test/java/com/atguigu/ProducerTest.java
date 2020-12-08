package com.atguigu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Symon
 * @version 1.0
 * @className ProducerTest
 * @description 消息的可靠性传递
 * @date 2020/12/8 9:24
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring-rabbitmq-producer.xml")
public class ProducerTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 确认模式：主要针对的是交换机，只要交换机收到消息，回调函数便会正确返回
     * 步骤：
     * 1. 确认模式开启：ConnectionFactory中开启publisher-confirms="true"
     * 2. 在rabbitTemplate定义ConfirmCallBack回调函数
     */
    @Test
    public void testConfirm() {
        //2. 定义回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {

                if (ack){
                    //接收成功
                    System.out.println("接收成功消息" + cause);
                }else {
                    //接收失败
                    System.out.println("接收失败消息" + cause);
                    //做一些处理，让消息再次发送。
                }
            }
        });

        //3. 发送消息
        rabbitTemplate.convertAndSend("test_exchange_confirm111", "confirm", "message confirm....");
    }

    /*
    * @author Symon
    * @description : 回退模式
    * 步骤：
    * 1、开启回退模式：publisher-returns="true"
    * 2、设置ReturnCallBack：rabbitTemplate.setReturnCallback(...)
    * 3、设置exchange处理消息模式：
    *   a、如果消息没有路由到queue，则丢弃消息（默认）
    *   b、如果消息没有路由到Queue，返回给消息发送方ReturnCallBack：设置rabbitTemplate.setMandatory(true);
     **/
    @Test
    public void testReturn() {
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /*
             * @param message   消息对象
             * @param replyCode 错误码
             * @param replyText 错误信息
             * @param exchange  交换机
             * @param routingKey 路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("return 执行了....");

                System.out.println(message);
                System.out.println(replyCode);
                System.out.println(replyText);
                System.out.println(exchange);
                System.out.println(routingKey);
            }
        });

        rabbitTemplate.convertAndSend("test_exchange_confirm999","confirm","message confirm....");
    }

    /*
    * @author Symon
    * @description 测试限流
    * @date 2020/12/8 14:37
    * @param []
    * @return void
    **/
    @Test
    public void test3() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("test_exchange_confirm", "confirm", "message confirm....");
        }
    }

    
    /*
    * @author Symon
    * @description 测试ttl过期时间
    * @date 2020/12/8 14:40
    * @param []
    * @return void
    **/
    @Test
    public void testTTL() {

        //队列统一过期时间
        /*for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("test_exchange_ttl","ttl.hehe","message ttl");
        }*/

       //消息单独过期
        rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hehe", "message ttl....", message -> {
            //消息后处理对象，设置一些消息的参数信息
            message.getMessageProperties().setExpiration("5000");
            return message;
        });
    }

    /**
     消息成为死信的三种情况：
     a.队列消息长度到达限制；比如给队列最大只能存储10条消息，当第11条消息进来的时候存不下了，第11条消息就被称为死信
     b.消费者拒接消费消息，basicNack/basicReject,并且不把消息重新放入原目标队列,requeue=false；
     c.原队列存在消息过期设置，消息到达超时时间未被消费；


     * 发送测试死信消息：
     *  1. 过期时间
     *  2. 长度限制
     *  3. 消息拒收
     */
    @Test
    public void testDlx(){
        //1. 测试过期时间，死信消息
        // rabbitTemplate.convertAndSend("test_exchange_dlx","test.dlx.haha","我是一条消息，我会死吗？");

        //2. 测试长度限制后，消息死信
        /*for (int i = 0; i < 20; i++) {
            rabbitTemplate.convertAndSend("test_exchange_dlx","test.dlx.haha","我是一条消息，我会死吗？");
        }*/

        //3.消息拒收
        rabbitTemplate.convertAndSend("test_exchange_dlx","test.dlx.haha","我是一条消息，我会死吗？");
    }


    /*
    * @author Symon
    * @description 延迟队列
    * @date 2020/12/8 15:45
    * @param []
    * @return void
    **/
    @Test
    public  void testDelay() throws InterruptedException {
        //1.发送订单消息。 将来是在订单系统中，下单成功后，发送消息
        rabbitTemplate.convertAndSend("order_exchange", "order.msg", "订单信息：id=1,time=2020年4月30日16:41:47");

        //2.打印倒计时10秒
        for (int i = 10; i > 0 ; i--) {
            System.out.println(i+"...");
            Thread.sleep(1000);
        }
    }

}
