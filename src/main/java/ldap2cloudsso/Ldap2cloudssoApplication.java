package ldap2cloudsso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("ldap2cloudsso")

public class Ldap2cloudssoApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ldap2cloudssoApplication.class, args);
    }

}
