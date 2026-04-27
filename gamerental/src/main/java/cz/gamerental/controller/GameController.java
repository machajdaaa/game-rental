package cz.gamerental.controller;

import cz.gamerental.model.Game;
import cz.gamerental.model.User;
import cz.gamerental.service.GameService;
import cz.gamerental.service.LoanService;
import cz.gamerental.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final ReservationService reservationService;
    private final LoanService loanService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        if(search != null && !search.isBlank()) {
            model.addAttribute("games", gameService.searchByTitle(search));
        } else {
            model.addAttribute("games", gameService.findAll());
        }

        model.addAttribute("publishers", gameService.findAllPublishers());
        model.addAttribute("categories", gameService.findAllCategories());
        model.addAttribute("search", search);
        return "games/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal User user,
                         Model model) {
        Game game = gameService.findById(id);
        model.addAttribute("game", game);
        List<String> categoryNames = game.getCategories().stream()
                .map(c -> c.getName())
                .toList();
        model.addAttribute("categoryNames", categoryNames);
        long availableCopies = game.getCopies().stream().filter(c -> c.getAvailable()).count();
        model.addAttribute("totalCopies", game.getCopies().size());
        model.addAttribute("availableCopies", availableCopies);

        Set<Long> alreadyReservedIds = user != null
                ? reservationService.getReservedCopyIds(user)
                : Collections.emptySet();
        Set<Long> loanedCopyIds = user != null
                ? loanService.getActiveLoanCopyIds(user)
                : Collections.emptySet();
        Set<Long> reservableCopyIds = game.getCopies().stream()
                .filter(c -> !c.getAvailable()
                        && !alreadyReservedIds.contains(c.getId())
                        && !loanedCopyIds.contains(c.getId()))
                .map(c -> c.getId())
                .collect(Collectors.toSet());
        model.addAttribute("reservableCopyIds", reservableCopyIds);
        model.addAttribute("alreadyReservedIds", alreadyReservedIds);
        return "games/detail";
    }

    @GetMapping("/new")
    public String newGameForm(Model model) {
        model.addAttribute("publishers", gameService.findAllPublishers());
        model.addAttribute("categories", gameService.findAllCategories());
        return "games/form";
    }

    @PostMapping("/new")
    public String createGame(@RequestParam String title,
                             @RequestParam Integer minPlayers,
                             @RequestParam Integer maxPlayers,
                             @RequestParam Integer minAge,
                             @RequestParam Integer durationMinutes,
                             @RequestParam Long publisherId,
                             @RequestParam Set<Long> categoryIds,
                             @RequestParam Integer copyCount) {
        gameService.save(title, minPlayers, maxPlayers, minAge, durationMinutes, publisherId, categoryIds, copyCount);
        return "redirect:/games";
    }


    @PostMapping("/{id}/delete")
    public String deleteGame(@PathVariable Long id) {
        gameService.delete(id);
        return "redirect:/games";
    }

}
