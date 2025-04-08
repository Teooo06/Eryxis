package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.Permessi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermessiRepository extends JpaRepository<Permessi, Integer> {
    Permessi findByIdPermesso(int idPermesso);
}