package cz.gamerental.controller;

import cz.gamerental.model.User;
import cz.gamerental.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public String myReservations(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("reservations", reservationService.findByUser(user));
        return "reservations/list";
    }

    @PostMapping("/new")
    public String createReservation(@AuthenticationPrincipal User user,
                                    @RequestParam Long gameCopyId) {
        reservationService.createReservation(user, gameCopyId);
        return "redirect:/reservations";
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return "redirect:/reservations";
    }
}