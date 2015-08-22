package sukretbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

import java.util.Stack;

@SpringBootApplication
@ImportResource("Beans.xml")
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        Stack<String> abc;

    }
}

