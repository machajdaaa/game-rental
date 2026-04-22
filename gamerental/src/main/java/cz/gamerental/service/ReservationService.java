package cz.gamerental.service;

import cz.gamerental.model.GameCopy;
import cz.gamerental.model.Reservation;
import cz.gamerental.model.User;
import cz.gamerental.repository.GameCopyRepository;
import cz.gamerental.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GameCopyRepository gameCopyRepository;

    @Transactional
    public Reservation createReservation(User user, Long gameCopyId) {
        GameCopy gameCopy = gameCopyRepository.findById(gameCopyId)
                .orElseThrow(() -> new IllegalArgumentException("Kopie hry nenalezena"));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setGameCopy(gameCopy);
        reservation.setReservedDate(LocalDateTime.now());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Rezervace nenalezena"));

        reservation.setStatus(Reservation.ReservationStatus.CANCELED);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> findByUser(User user) {
        return reservationRepository.findByUserId(user.getId());
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}
