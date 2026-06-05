package cl.digitalclassroom.assessmentmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AssessmentManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssessmentManagerApplication.class, args);
    }

}
