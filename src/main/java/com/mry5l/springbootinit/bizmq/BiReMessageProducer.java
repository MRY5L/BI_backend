package com.mry5l.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BiReMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param chartId
     */
    public void sendMessage(Long chartId) {
        rabbitTemplate.convertAndSend(BiMqConstant.BI_RE_EXCHANGE_NAME, BiMqConstant.BI_RE_ROUTING_KEY, chartId);
    }

}
