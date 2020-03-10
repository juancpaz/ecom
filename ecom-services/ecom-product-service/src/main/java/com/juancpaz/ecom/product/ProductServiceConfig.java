package com.juancpaz.ecom.product;

import org.axonframework.amqp.eventhandling.AMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Channel;

@Configuration
public class ProductServiceConfig {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${axon.amqp.exchange}")
	private String rabbitMQExchange;

	@Value("${axon.amqp.queue}")
	private String rabbitMQQueue;


	@Bean
	public FanoutExchange eventBusExchange() {
		return new FanoutExchange(rabbitMQExchange, true, false);
	}

	// Event bus queue
	@Bean
	public Queue eventBusQueue() {
		return new Queue(rabbitMQQueue, true, false, false);
	}

	// binding queue to exachange
	@Bean
	public Binding binding() {
		return BindingBuilder.bind(eventBusQueue()).to(eventBusExchange());
	}

	@Bean
	public SpringAMQPMessageSource ecomProductEventQueue(AMQPMessageConverter messageConverter) {
	    return new SpringAMQPMessageSource(messageConverter) {

	        @RabbitListener(queues = "${axon.amqp.queue}")
	        @Override
	        public void onMessage(Message message, Channel channel) {
	        	logger.debug("New event received -> "+message);
	        	System.out.println("New event received -> "+message);
	            super.onMessage(message, channel);
	        }
	    };
	}
}
