package com.example.backend.repository.side;

import com.example.backend.entity.side.Sidedatum;
import com.example.backend.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


public interface SidedatumRepository extends JpaRepository<Sidedatum, Integer> {

    List<Sidedatum> findAllByUser(User user);

    Optional<Sidedatum> findByIdAndUser(Integer id, User user);

    Long countByUser(User user);


    @Modifying
    @Query("DELETE FROM Sidedatum s WHERE s.status IN :statuses AND s.expiredAt <= :now")
    int deleteByStatusInAndExpiredAtBefore(
            @Param("statuses") List<Sidedatum.Status> statuses,
            @Param("now") Instant now);

    List<Sidedatum> findByStatus(Sidedatum.Status status);

    Page<Sidedatum> findAll(Specification<Sidedatum> spec, Pageable pageable);


    List<Sidedatum> findAllByStatusAndExpiredAtLessThanEqual(Sidedatum.Status status, Instant now);
}