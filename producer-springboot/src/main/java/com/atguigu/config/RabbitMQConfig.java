package com.atguigu.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Symon
 * @version 1.0
 * @className RabbitMQConfig
 * @description TODO
 * @date 2020/12/8 19:33
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "boot_topic_exchange";
    public static final String QUEUE_NAME = "boot_queue";

    // 1、交换机
    @Bean("bootExchange")
    public Exchange bootExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }
    // 2、队列
    @Bean("bootQueue")
    public Queue bootQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    // 3、绑定
    @Bean
    public Binding bindQueueExchange(
            @Qualifier("bootExchange") Exchange exchange,
            @Qualifier("bootQueue") Queue queue) {

        return BindingBuilder.bind(queue).to(exchange).with("boot.#").noargs();

    }
}
