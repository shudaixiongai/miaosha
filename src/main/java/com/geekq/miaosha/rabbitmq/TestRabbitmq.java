package com.geekq.miaosha.rabbitmq;

import com.geekq.miaosha.redis.RedisService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class TestRabbitmq {

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MQSender mqSender;

    public static void main(String[] args) {


    }

    private void test() {
        mqSender.sendMiaoshaMessagetest("11");
    }
}
