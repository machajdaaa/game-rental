package cz.gamerental.service;

import cz.gamerental.model.Game;
import cz.gamerental.model.GameCopy;
import cz.gamerental.model.Loan;
import cz.gamerental.model.User;
import cz.gamerental.repository.FineRepository;
import cz.gamerental.repository.GameCopyRepository;
import cz.gamerental.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private GameCopyRepository gameCopyRepository;

    @Mock
    private FineRepository fineRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanService loanService;

    private User user;
    private GameCopy gameCopy;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Game game = new Game();
        game.setTitle("Test hra");

        gameCopy = new GameCopy();
        gameCopy.setId(1L);
        gameCopy.setAvailable(true);
        gameCopy.setInventoryCode("INV001");
        gameCopy.setGame(game);
    }

    @Test
    void createLoan_uspesne() {
        when(gameCopyRepository.findById(1L)).thenReturn(Optional.of(gameCopy));
        when(gameCopyRepository.save(any())).thenReturn(gameCopy);
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan loan = loanService.createLoan(user, 1L);

        assertNotNull(loan);
        assertEquals(user, loan.getUser());
        assertEquals(gameCopy, loan.getGameCopy());
        assertEquals(LocalDate.now(), loan.getLoanDate());
        assertFalse(gameCopy.getAvailable());
        verify(loanRepository, times(1)).save(any());
    }

    @Test
    void createLoan_nedostupnyVytisk_vyhodiVyjimku() {
        gameCopy.setAvailable(false);
        when(gameCopyRepository.findById(1L)).thenReturn(Optional.of(gameCopy));

        assertThrows(RuntimeException.class, () -> loanService.createLoan(user, 1L));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void returnGame_vcas_bezPokuty() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(user);
        loan.setGameCopy(gameCopy);
        loan.setLoanDate(LocalDate.now().minusDays(5));
        loan.setDueDate(LocalDate.now().plusDays(9));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(gameCopyRepository.save(any())).thenReturn(gameCopy);
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan returned = loanService.returnGame(1L);

        assertNotNull(returned.getReturnDate());
        assertTrue(gameCopy.getAvailable());
        verify(fineRepository, never()).save(any());
    }

    @Test
    void returnGame_sOpozdenim_vyhodiPokutu() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(user);
        loan.setGameCopy(gameCopy);
        loan.setLoanDate(LocalDate.now().minusDays(20));
        loan.setDueDate(LocalDate.now().minusDays(6));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(gameCopyRepository.save(any())).thenReturn(gameCopy);
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        loanService.returnGame(1L);

        verify(fineRepository, times(1)).save(any());
    }
}