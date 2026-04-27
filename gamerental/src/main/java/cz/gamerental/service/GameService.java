package cz.gamerental.service;

import cz.gamerental.model.Game;
import cz.gamerental.model.GameCategory;
import cz.gamerental.model.GameCopy;
import cz.gamerental.model.Publisher;
import cz.gamerental.repository.GameCategoryRepository;
import cz.gamerental.repository.GameCopyRepository;
import cz.gamerental.repository.GameRepository;
import cz.gamerental.repository.PublisherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final PublisherRepository publisherRepository;
    private final GameCategoryRepository gameCategoryRepository;
    private final GameCopyRepository gameCopyRepository;

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public List<Game> searchByTitle(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title);
    }

    public Game findById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hra nenalezena"));
    }

    @Transactional
    public Game save(String title, Integer minPlayers, Integer maxPlayers,
                     Integer minAge, Integer durationMinutes,
                     Long publisherId, Set<Long> categoryIds, Integer copyCount) {

        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("Vydavatel nenalezen"));

        Set<GameCategory> categories = categoryIds.stream()
                .map(id -> gameCategoryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Kategorie nenalezena")))
                .collect(Collectors.toSet());

        Game game = new Game();
        game.setTitle(title);
        game.setMinPlayers(minPlayers);
        game.setMaxPlayers(maxPlayers);
        game.setMinAge(minAge);
        game.setDurationMinutes(durationMinutes);
        game.setPublisher(publisher);
        game.setCategories(categories);

        Game savedGame = gameRepository.save(game);

        for (int i = 1; i <= copyCount; i++) {
            GameCopy copy = new GameCopy();
            copy.setGame(savedGame);
            copy.setCondition(GameCopy.CopyCondition.NEW);
            copy.setAvailable(true);
            copy.setInventoryCode(String.format("%d-%02d", savedGame.getId(), i));
            gameCopyRepository.save(copy);
        }

        return savedGame;
    }

    @Transactional
    public void delete(Long id) {
        gameRepository.deleteById(id);
    }

    public List<Publisher> findAllPublishers() {
        return publisherRepository.findAll();
    }

    public List<GameCategory> findAllCategories() {
        return gameCategoryRepository.findAll();
    }
}
