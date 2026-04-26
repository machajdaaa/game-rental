package cz.gamerental.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="games")
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer minAge;
    private Integer durationMinutes;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(
            name = "game_game_category",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<GameCategory> categories = new HashSet<>();

    @OneToMany(mappedBy = "game")
    private Set<GameCopy> copies = new HashSet<>();
}
