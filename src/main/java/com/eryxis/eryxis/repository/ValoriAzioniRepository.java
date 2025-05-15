package com.eryxis.eryxis.repository;

import com.eryxis.eryxis.model.ValoriAzioni;
import com.eryxis.eryxis.model.ValoriAzioniId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValoriAzioniRepository extends JpaRepository<ValoriAzioni, ValoriAzioniId> {
}
