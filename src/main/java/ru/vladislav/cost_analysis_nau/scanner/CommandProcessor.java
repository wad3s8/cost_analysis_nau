package ru.vladislav.cost_analysis_nau.scanner;


import java.util.Optional;
import org.springframework.stereotype.Component;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.service.UserService;
import ru.vladislav.cost_analysis_nau.service.UserServiceImpl;

@Component
public class CommandProcessor
{
    private final UserService userService;

    public CommandProcessor(UserServiceImpl userServiceImpl)
    {
        this.userService = userServiceImpl;
    }

    public void processCommand(String input)
    {
        try
        {
            String[] cmd = input.trim().split("\\s+");
            if (cmd.length == 0 || cmd[0].isBlank())
            {
                System.out.println("Команда не введена.");
                return;
            }

            switch (cmd[0].toLowerCase())
            {
                case "create" ->
                {
                    if (cmd.length < 3)
                    {
                        System.out.println("Использование: create <id> <name> <password>");
                        return;
                    }

                    Long id = Long.valueOf(cmd[1]);
                    String name = cmd[2];
                    String password = cmd[3];

                    userService.createUser(id, name, password);
                    System.out.println("Пользователь успешно добавлен.");
                }

                case "get" ->
                {
                    if (cmd.length < 2)
                    {
                        System.out.println("Использование: get <id>");
                        return;
                    }

                    Long id = Long.valueOf(cmd[1]);
                    Optional<User> user = userService.findById(id);

                    if (user.isPresent())
                    {
                        System.out.println("Найден пользователь: " + user);
                    }
                    else
                    {
                        System.out.println("Пользователь не найден.");
                    }
                }


                case "update" ->
                {
                    if (cmd.length < 3)
                    {
                        System.out.println("Использование: update <id> <name> <password>");
                        return;
                    }

                    Long id = Long.valueOf(cmd[1]);
                    String name = cmd[2];
                    String password = cmd[3];

                    userService.update(id, name, password);
                    System.out.println("Пользователь успешно обновлён.");
                }

                case "delete" ->
                {
                    if (cmd.length < 2)
                    {
                        System.out.println("Использование: delete <id>");
                        return;
                    }

                    Long id = Long.valueOf(cmd[1]);
                    userService.deleteById(id);
                    System.out.println("Пользователь успешно удалён.");
                }

                default ->
                        System.out.println("Неизвестная команда.");
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Ошибка: id должен быть числом.");
        }
        catch (Exception e)
        {
            System.out.println("Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}