package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.vladislav.cost_analysis_nau.entity.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserRepository implements CrudRepository<User, Long>
{
    private final List<User> usersList;

    @Autowired
    public UserRepository(List<User> usersList)
    {
        this.usersList = usersList;
    }

    @Override
    public void create(User user)
    {
        usersList.add(user);
    }

    @Override
    public Optional<User> read(Long id)
    {
        return usersList.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    @Override
    public void update(User user)
    {
        for (int i = 0; i < usersList.size(); i++) {
            if (Objects.equals(usersList.get(i).getId(), user.getId())){
                usersList.set(i, user);
                return;
            }
        }
    }

    @Override
    public void delete(Long id)
    {
        usersList.removeIf(user -> user.getId().equals(id));
    }
}
