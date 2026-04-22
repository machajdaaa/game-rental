package cz.gamerental.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_copies")
@Getter
@Setter
@NoArgsConstructor
public class GameCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyCondition condition;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(nullable = false, unique = true)
    private String inventoryCode;

    public enum CopyCondition {
        NEW, GOOD, DAMAED
    }
}
