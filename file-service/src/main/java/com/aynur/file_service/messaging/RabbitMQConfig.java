package com.aynur.file_service.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String SCAN_EXCHANGE = "scan.exchange";
    public static final String SCAN_QUEUE = "scan.queue";
    public static final String SCAN_ROUTING_KEY = "scan.request";
    public static final String SCAN_RESULT_QUEUE = "scan.result.queue";
    public static final String SCAN_RESULT_ROUTING_KEY = "scan.result";

    @Bean
    public DirectExchange scanExchange() {
        return new DirectExchange(SCAN_EXCHANGE, true, false);
    }
    @Bean
    public Queue scanQueue() {
        return QueueBuilder.durable(SCAN_QUEUE).build();
    }
    @Bean
    public Binding scanBinding(Queue scanQueue, DirectExchange scanExchange) {
        return BindingBuilder.bind(scanQueue).to(scanExchange).with(SCAN_ROUTING_KEY);
    }
    @Bean
    public Queue scanResultQueue() {
        return QueueBuilder.durable(SCAN_RESULT_QUEUE).build();
    }
    @Bean
    public Binding scanResultBinding(Queue scanResultQueue, DirectExchange scanExchange) {
        return BindingBuilder.bind(scanResultQueue).to(scanExchange).with(SCAN_RESULT_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate JSON ilə işləsin
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter jacksonConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonConverter);
        return template;
    }
    //  Listener-lər də JSON oxusun (ScanResultListener üçün)
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jacksonConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonConverter);
        return factory;
    }
    // Queue/exchange/binding avtomatik yaransın
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
