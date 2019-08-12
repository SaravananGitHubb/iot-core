package com.infosys.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.amazonaws.services.sqs.AmazonSQSAsync;

@SpringBootApplication
@EnableSqs
@EnableScheduling
public class IotCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotCoreApplication.class, args);
	}
	@Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSqs, ResourceIdResolver resourceIdResolver) {
        return new QueueMessagingTemplate(amazonSqs, resourceIdResolver);
    }
}
