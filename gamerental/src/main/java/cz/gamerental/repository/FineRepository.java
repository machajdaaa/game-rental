package cz.gamerental.repository;

import cz.gamerental.model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByLoanId(Long id);
    List<Fine> findByPaidFalse();
    List<Fine> findAllByLoanIdIn(List<Long> loanIds);
}
