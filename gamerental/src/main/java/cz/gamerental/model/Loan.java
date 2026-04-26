package cz.gamerental.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_copy_id")
    private GameCopy gameCopy;

    @Column(nullable = false)
    private LocalDate loanDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @OneToOne(mappedBy = "loan")
    private Fine fine;


}
