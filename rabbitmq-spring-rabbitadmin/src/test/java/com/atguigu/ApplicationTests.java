package com.atguigu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Symon
 * @version 1.0
 * @className ApplicationTests
 * @description TODO
 * @date 2020/12/8 18:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testAdmin() {
        DirectExchange directExchange = new DirectExchange("test.direct", true, false);
        rabbitAdmin.declareExchange(directExchange);

        Queue directQueue = new Queue("test.direct.queue", true);
        rabbitAdmin.declareQueue(directQueue);

        rabbitAdmin.declareBinding(BindingBuilder
                .bind(directQueue)
                .to(directExchange)
                .with("direct"));


        FanoutExchange fanoutExchange = new FanoutExchange("test.fanout", true, false);
        rabbitAdmin.declareExchange(fanoutExchange);

        Queue fanoutQueue = new Queue("test.fanout.queue", true);
        rabbitAdmin.declareQueue(fanoutQueue);

        rabbitAdmin.declareBinding(BindingBuilder
                .bind(fanoutQueue)
                .to(fanoutExchange)//广播没有路由
        );


        TopicExchange topicExchange = new TopicExchange("test.topic");
        rabbitAdmin.declareExchange(topicExchange);

        Queue topicQueue = new Queue("test.topic.queue");
        rabbitAdmin.declareQueue(topicQueue);

        rabbitAdmin.declareBinding(
                BindingBuilder.bind(topicQueue)
                .to(topicExchange)
                .with("user.#")
        );


        rabbitAdmin.purgeQueue("test.topic.queue", false);
    }

}
