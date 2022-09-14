package ldap2scim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("ldap2cloudsso")

public class Ldap2scimApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ldap2scimApplication.class, args);
    }

}
