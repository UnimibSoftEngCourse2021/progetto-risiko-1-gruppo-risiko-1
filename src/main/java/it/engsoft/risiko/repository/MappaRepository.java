package it.engsoft.risiko.repository;

import it.engsoft.risiko.model.Mappa;
import org.springframework.data.repository.Repository;

public interface MappaRepository extends Repository<Mappa, Long> {
    Mappa findById(Long id);
}
