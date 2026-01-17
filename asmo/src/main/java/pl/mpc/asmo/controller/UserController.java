package pl.mpc.asmo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mpc.asmo.model.User;
import pl.mpc.asmo.service.UserServiceDb;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceDb userService;

    @Autowired
    public UserController(UserServiceDb userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok().body(userService.getUsers());
    }
}
