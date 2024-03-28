package com.mry5l.springbootinit.bizmq;


import com.rabbitmq.client.Channel;
import com.mry5l.springbootinit.common.ErrorCode;
import com.mry5l.springbootinit.exception.BusinessException;
import com.mry5l.springbootinit.model.entity.Chart;
import com.mry5l.springbootinit.openai.ChatGptService;
import com.mry5l.springbootinit.service.ChartService;

import java.io.IOException;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BiReMessageConsumer {
    @Autowired
    private ChartService chartService;

    @Autowired
    private ChatGptService chatGptService;

    @RabbitListener(queues = {"bi_re_queue"}, ackMode = "MANUAL")
    public void receiveMessage(Long chartId, Channel channel, @Header("amqp_deliveryTag") long deliveryTag) throws IOException {
        if (chartId == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表为空");
        }

        if (chart.getNumber() > 2) {
            channel.basicNack(deliveryTag, false, false);
            chart.setStatus("noSuccess");
            chartService.updateById(chart);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表重试次数达到上限,请删除后重试");
        }
        String userInput = buildUserInput(chart);
        if (chatGptService.maxLength(userInput)) {
            channel.basicNack(deliveryTag, false, false);
            chart.setStatus("lengthMax");
            chartService.updateById(chart);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "内容过多请删减后尝试");
        }
        String results = chatGptService.doChatMq(
                userInput +
                        "\n 请生成JSON代码,如option = {\n title: {\n    text: '网站用户增长趋势'\n }}",
                channel, deliveryTag, chart);
        chart.setNumber(chart.getNumber() + 1);
        chart.setStatus("running");
        chartService.updateById(chart);
        String[] splits = results.split("【【【【【");
        log.info("ChartGPT返回结果为: {}", Arrays.toString(splits));
        if (splits.length < 3) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "AI 生成错误");
            return;
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setName(chart.getName());
        updateChartResult.setStatus("success");
        boolean res = this.chartService.updateById(updateChartResult);
        if (!res) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
        }
        channel.basicAck(deliveryTag, false);
    }

    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String chartData = chart.getChartData();
        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(chartData).append("\n");
        return userInput.toString();
    }

    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }
}
