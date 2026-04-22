package cz.gamerental.repository;

import cz.gamerental.model.GameCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameCopyRepository extends JpaRepository<GameCopy, Long> {

    List<GameCopy> findAllByGameId(Long gameId);
    List<GameCopy> findByAvailableTrue();
}
