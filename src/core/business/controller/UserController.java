package core.business.controller;

import core.business.service.UserService;
import core.model.User;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void register(User user) {
        userService.register(user);
    }

    public boolean login(String userId, String password) {
        return userService.login(userId, password);
    }
}
