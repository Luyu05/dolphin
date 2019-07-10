package com.luyu.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by luyu on 2019/7/10.
 * start mq
 */
@Service
public class MqBroadService implements InitializingBean { //todo 接入mq具体实现

    private static final Logger logger = LoggerFactory.getLogger(MqBroadService.class);

    private void startProducer() {
        //启动生产者
    }

    private void createConsumerGroup() {
        //创建广播消费组
    }

    private void startConsumer() {
        //创建消费者
    }

    public void send(Map<String, String> msgMap) {

    }


    public void afterPropertiesSet() throws Exception {
        startProducer();
        createConsumerGroup();
        startConsumer();
    }
}