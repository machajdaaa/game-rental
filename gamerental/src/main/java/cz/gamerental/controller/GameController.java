package cz.gamerental.controller;

import cz.gamerental.model.Game;
import cz.gamerental.service.GameService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

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
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("game", gameService.findById(id));
        return "games/detail";
    }

    @GetMapping("/new")
    public String newGameForm(Model model) {
        model.addAttribute("publishers", gameService.findAllPublishers());
        model.addAttribute("categories", gameService.findAllCategories());
        return "games/form";
    }

    @PostMapping("/{new}")
    public String createGame(@RequestParam String title,
                             @RequestParam Integer minPlayers,
                             @RequestParam Integer maxPlayers,
                             @RequestParam Integer minAge,
                             @RequestParam Integer durationMinutes,
                             @RequestParam Long publisherId,
                             @RequestParam Set<Long> categoryIds) {
        gameService.save(title, minPlayers, maxPlayers, minAge, durationMinutes, publisherId, categoryIds);
        return "redirect:/games";
    }


    @PostMapping("/{id}/delete")
    public String deleteGame(@PathVariable Long id) {
        gameService.delete(id);
        return "redirect:/games";
    }

}
