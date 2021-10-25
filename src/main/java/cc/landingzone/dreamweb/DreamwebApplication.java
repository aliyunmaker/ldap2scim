package cc.landingzone.dreamweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("cc.landingzone.dreamweb")

public class DreamwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DreamwebApplication.class, args);
    }

}
