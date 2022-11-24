package poc;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@Deployment(resources = "classpath:bpmn/poc-process.bpmn")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
