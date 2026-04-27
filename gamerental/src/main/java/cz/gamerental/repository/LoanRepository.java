package cz.gamerental.repository;

import cz.gamerental.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByUserId(Long userId);
    List<Loan> findByUserIdAndReturnDateIsNull(Long userId);
    List<Loan> findAllByGameCopyIdIn(List<Long> gameCopyIds);
}
