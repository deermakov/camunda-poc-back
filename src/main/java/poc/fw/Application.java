package poc.fw;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "poc")
@EnableZeebeClient
@EnableScheduling
@Deployment(resources = "classpath:bpmn/poc-process.bpmn")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
