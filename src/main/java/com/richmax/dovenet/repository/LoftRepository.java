package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.Loft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoftRepository extends JpaRepository<Loft, Long> {

    List<Loft> findByOwnerId(Long ownerId);

}
