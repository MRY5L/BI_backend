package com.mry5l.springbootinit.bizmq;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.mry5l.springbootinit.common.ErrorCode;
import com.mry5l.springbootinit.exception.BusinessException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @PostConstruct
    public void init() {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            String exchangeName = "bi_re_exchange";
            channel.exchangeDeclare(exchangeName, "direct");
            String queueName = "bi_re_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, "bi_re_routingKey");
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "重试生成图表RabbitMq初始化失败");
        }
    }
}
