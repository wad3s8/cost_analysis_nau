package ru.vladislav.cost_analysis_nau.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vladislav.cost_analysis_nau.scanner.CommandProcessor;
import java.util.Scanner;

@Configuration
@Getter
public class Config {

    @Autowired
    private CommandProcessor commandProcessor;

    @Bean
    public CommandLineRunner commandScanner()
    {
        return args ->
        {
            try (Scanner scanner = new Scanner(System.in))
            {
                System.out.println("Введите команду:");
                System.out.println("create <id> <name> <password>");
                System.out.println("get <id>");
                System.out.println("getAll");
                System.out.println("update <id> <name> <password>");
                System.out.println("delete <id>");
                System.out.println("exit");

                while (true)
                {
                    System.out.print("> ");
                    String input = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(input.trim()))
                    {
                        System.out.println("Выход из программы...");
                        break;
                    }

                    commandProcessor.processCommand(input);
                }
            }
        };
    }

}
