package it.engsoft.risiko.repository;

import it.engsoft.risiko.model.Mappa;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MappaRepository extends CrudRepository<Mappa, Long> {
    List<Mappa> findAll();
}
