package cz.gamerental.controller;

import cz.gamerental.model.User;
import cz.gamerental.service.NotificationService;
import cz.gamerental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final NotificationService notificationService;

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
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        Map<Long, List<String>> userRoleNames = users.stream()
                .collect(Collectors.toMap(
                        u -> u.getId(),
                        u -> u.getRoles().stream().map(r -> r.getName()).toList()
                ));
        model.addAttribute("userRoleNames", userRoleNames);
        return "admin/users";
    }

    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("notifications", notificationService.findByUser(user));
        return "auth/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }


}
