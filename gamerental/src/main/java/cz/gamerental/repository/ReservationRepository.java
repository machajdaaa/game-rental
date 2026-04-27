package cz.gamerental.repository;

import cz.gamerental.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);
    List<Reservation> findAllByGameCopyIdIn(List<Long> gameCopyIds);

    List<Reservation> findByUserIdAndStatus(Long userId, Reservation.ReservationStatus status);

    boolean existsByUserIdAndGameCopyIdAndStatus(Long userId, Long gameCopyId, Reservation.ReservationStatus status);
}
