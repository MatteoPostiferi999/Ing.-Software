package business.controller;

import business.service.UserService;
import model.user.Guide;
import model.user.Traveler;
import model.user.User;


public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void registerUser(String name, String email, String password) {
        userService.register(new User(name, email, password));
    }

    public Traveler loginAsTraveler(String email, String password) {
        return userService.login(email, password).getTravelerProfile();
    }

    public Guide loginAsGuide(String email, String password) {
        return userService.login(email, password).getGuideProfile();
    }
}
