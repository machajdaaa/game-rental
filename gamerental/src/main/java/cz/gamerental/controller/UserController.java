package cz.gamerental.controller;

import cz.gamerental.model.User;
import cz.gamerental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String fullName) {
        userService.registerUser(username, email, password, fullName);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "auth/profile";
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }


}
