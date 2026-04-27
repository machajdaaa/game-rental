package cz.gamerental.service;

import cz.gamerental.model.Fine;
import cz.gamerental.model.GameCopy;
import cz.gamerental.model.Loan;
import cz.gamerental.model.User;
import cz.gamerental.repository.FineRepository;
import cz.gamerental.repository.GameCopyRepository;
import cz.gamerental.repository.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LoanService {


    private final LoanRepository loanRepository;
    private final GameCopyRepository gameCopyRepository;
    private final FineRepository fineRepository;

    private final NotificationService notificationService;

    private static final BigDecimal FINE_PER_DAY = new BigDecimal("10.00");
    private static final int LOAN_DURATION_DAYS = 14;

    public LoanService(LoanRepository loanRepository,
                       GameCopyRepository gameCopyRepository,
                       FineRepository fineRepository,
                       @Lazy NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.gameCopyRepository = gameCopyRepository;
        this.fineRepository = fineRepository;
        this.notificationService = notificationService;
    }


    @Transactional
    public Loan createLoan(User user, Long gameCopyId) {
        GameCopy gameCopy = gameCopyRepository.findById(gameCopyId)
                .orElseThrow(() -> new RuntimeException("Hra nenalezena"));

        if (!gameCopy.getAvailable()) {
            throw new RuntimeException("Hra není dostupná.");
        }

        gameCopy.setAvailable(false);
        gameCopyRepository.save(gameCopy);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setGameCopy(gameCopy);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(LOAN_DURATION_DAYS));

        notificationService.createNotification(user, "Vypůčil jste si hru: " + gameCopy.getGame().getTitle() + ". Vrátit do: " + loan.getDueDate());

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnGame(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Výpůjčka nenalezena"));

        loan.setReturnDate(LocalDate.now());

        GameCopy gameCopy = loan.getGameCopy();
        gameCopy.setAvailable(true);
        gameCopyRepository.save(gameCopy);

        if (LocalDate.now().isAfter(loan.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
            BigDecimal amount = FINE_PER_DAY.multiply(BigDecimal.valueOf(daysLate));

            Fine fine = new Fine();
            fine.setLoan(loan);
            fine.setAmount(amount);
            fine.setPaid(false);
            fine.setCreatedAt(LocalDateTime.now());
            fineRepository.save(fine);

            notificationService.createNotification(loan.getUser(), "Byla vám udělena pokuta " + amount + " Kč za pozdní vrácení hry " + loan.getGameCopy().getGame().getTitle());
        }

        notificationService.createNotification(loan.getUser(), "Hra " + loan.getGameCopy().getGame().getTitle() + "byla úspěšně vrácena.");

        return loanRepository.save(loan);
    }

    public List<Loan> findByUserId(Long userId) {
        return loanRepository.findAllByUserId(userId);
    }

    public Set<Long> getActiveLoanCopyIds(User user) {
        return loanRepository.findByUserIdAndReturnDateIsNull(user.getId())
                .stream()
                .map(l -> l.getGameCopy().getId())
                .collect(Collectors.toSet());
    }

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Loan findById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Výpůjčka nenalezena"));
    }


}
