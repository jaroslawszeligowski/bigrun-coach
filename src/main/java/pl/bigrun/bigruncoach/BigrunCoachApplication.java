package pl.bigrun.bigruncoach;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.bigrun.bigruncoach.service.ChatService;
import pl.bigrun.bigruncoach.service.RagService;

@SpringBootApplication
public class BigrunCoachApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigrunCoachApplication.class, args);
    }

    // added just for tests before proper implementation, to remove
    @Bean
    CommandLineRunner test(ChatService chatService, ChatClient chatClient) {
        return args -> {
            System.out.println("=== TEST ===");
//            String response = chatService.chat("Chcę przebiec maraton w 3h, jakie tempo?");
//            String response = chatService.chat("Chcę przebiec Półmaraton w 3h, jakie tempo?");
            String response = chatService.chat("oblicz nawodnienie podczas biegu maratonskiego ktory trwa 3h przy temp 10st C?");
            System.out.println(response);
            System.out.println("=== END ===");
        };
    }

}
