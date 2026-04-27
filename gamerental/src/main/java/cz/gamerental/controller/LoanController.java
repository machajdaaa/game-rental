package cz.gamerental.controller;

import cz.gamerental.model.User;
import cz.gamerental.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public String myLoans(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("loans", loanService.findByUserId(user.getId()));
        return "loans/list";
    }

    @PostMapping("/new")
    public String createLoan(@AuthenticationPrincipal User user, @RequestParam Long gameCopyId) {
        loanService.createLoan(user, gameCopyId);
        return "redirect:/loans";
    }

    @PostMapping("/{id}/return")
    public String returnGame(@PathVariable Long id) {
        loanService.returnGame(id);
        return "redirect:/loans";
    }

    @GetMapping("/admin")
    public String allLoans(Model model) {
        model.addAttribute("loans", loanService.findAll());
        return "loans/admin";
    }
}
