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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GameCopyRepository gameCopyRepository;

    private static final List<Reservation.ReservationStatus> ACTIVE_STATUSES =
            List.of(Reservation.ReservationStatus.PENDING);

    @Transactional
    public Reservation createReservation(User user, Long gameCopyId) {
        if (reservationRepository.existsByUserIdAndGameCopyIdAndStatusIn(user.getId(), gameCopyId, ACTIVE_STATUSES)) {
            throw new IllegalStateException("Tato kopie hry je již rezervována");
        }

        GameCopy gameCopy = gameCopyRepository.findById(gameCopyId)
                .orElseThrow(() -> new IllegalArgumentException("Kopie hry nenalezena"));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setGameCopy(gameCopy);
        reservation.setReservedDate(LocalDateTime.now());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        return reservationRepository.save(reservation);
    }

    public Set<Long> getReservedCopyIds(User user) {
        return reservationRepository.findByUserIdAndStatusIn(user.getId(), ACTIVE_STATUSES)
                .stream()
                .map(r -> r.getGameCopy().getId())
                .collect(Collectors.toSet());
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
