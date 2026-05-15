package cl.digitalclassroom.classroommanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ClassroomManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassroomManagerApplication.class, args);
    }

}
