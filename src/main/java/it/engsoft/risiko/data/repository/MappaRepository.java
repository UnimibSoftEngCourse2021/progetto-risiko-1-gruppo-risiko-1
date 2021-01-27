package it.engsoft.risiko.data.repository;

import it.engsoft.risiko.data.model.Mappa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Il repository che consente di interrogare il database ed effettuare operazioni di inserimento e interrogazione delle
 * mappe esistenti.
 */
@Repository
public interface MappaRepository extends CrudRepository<Mappa, Long> {
    List<Mappa> findAll();
}
