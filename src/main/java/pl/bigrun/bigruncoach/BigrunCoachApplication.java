package pl.bigrun.bigruncoach;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.bigrun.bigruncoach.service.ChatService;

@SpringBootApplication
public class BigrunCoachApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigrunCoachApplication.class, args);
    }

    // added just for tests before proper implementation, to remove
    @Bean
    CommandLineRunner test(ChatService chatService) {
        return args -> {
            System.out.println("=== TEST ===");
            String response = chatService.chat("Chcę przebiec maraton w 3h, jakie tempo?");
            System.out.println(response);
            System.out.println("=== END ===");
        };
    }

}
