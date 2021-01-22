package it.engsoft.risiko.data.repository;

import it.engsoft.risiko.data.model.Mappa;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MappaRepository extends CrudRepository<Mappa, Long> {
    List<Mappa> findAll();
}
