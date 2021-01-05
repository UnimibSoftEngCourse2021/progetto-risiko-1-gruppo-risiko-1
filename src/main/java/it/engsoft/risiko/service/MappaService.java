package it.engsoft.risiko.service;

import it.engsoft.risiko.dao.*;
import it.engsoft.risiko.model.Mappa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.engsoft.risiko.repository.MappaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: MappaService
@Service
public class MappaService {
    private MappaRepository mappaRepository;

    @Autowired
    public MappaService(MappaRepository mappaRepository) {
        this.mappaRepository = mappaRepository;
    }

    public List<CompactMappaDAO> mappe() {
        List<Mappa> mappe = mappaRepository.findAll();

        List<CompactMappaDAO> compactMappe = mappe.stream()
                .map(CompactMappaDAO::new)
                .collect(Collectors.toList());

        return compactMappe;
    }

    public MappaDAO mappa(Long mappaId) {
        Mappa mappa = mappaRepository.findById(mappaId);
        return new MappaDAO(mappa);
    }

    // public void nuovaMappa(MappaDTO mappaDTO) {}
}
