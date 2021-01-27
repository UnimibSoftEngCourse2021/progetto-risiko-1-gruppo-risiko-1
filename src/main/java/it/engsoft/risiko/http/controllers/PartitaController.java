package it.engsoft.risiko.http.controllers;

import it.engsoft.risiko.exceptions.MossaIllegaleException;
import it.engsoft.risiko.service.PartitaService;
import it.engsoft.risiko.http.dto.*;
import it.engsoft.risiko.data.model.Partita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Gestisce le richieste Http relative ad una partita, salvando le informazioni relative alla partita stessa nella
 * sessione.
 */
@RestController
@RequestMapping("api")
public class PartitaController {
    private final PartitaService partitaService;
    private final String partitaKey;
    private static final String exMessage = "Mossa illegale: partita null";

    /**
     * Crea un'istanza di PartitaController con dependency injection.
     * @param partitaService il service che gestisce la partita
     */
    @Autowired
    public PartitaController(PartitaService partitaService) {
        this.partitaService = partitaService;
        this.partitaKey = "partita";
    }

    /**
     * Dà inizio ad una nuova partita basata sulle configurazioni ricevute in ingresso, poi la salva nella sessione
     * per un uso futuro.
     * @param nuovoGiocoRequest la configurazione della partita
     * @param httpSession la sessione http in cui salvare la partita
     * @return le informazioni necessarie per inizializzare la partita nel browser
     */
    @PostMapping(path = "/gioco")
    public NuovoGiocoResponse nuovoGioco(@RequestBody NuovoGiocoRequest nuovoGiocoRequest, HttpSession httpSession) {
        Partita partita = partitaService.nuovoGioco(nuovoGiocoRequest);
        httpSession.setAttribute(partitaKey, partita);

        return new NuovoGiocoResponse(partita);
    }

    /**
     * Gestisce una richiesta http relativa ai rinforzi. Se posizionando i rinforzi il giocatore raggiunge il proprio
     * obiettivo, la partita viene terminata ed eliminata dalla sessione.
     * @param rinforzoRequest le informazioni sui rinforzi da effettuare
     * @param httpSession la sessione http da cui recuperare la partita
     * @return informazioni sull'esito dei rinforzi
     */
    @PostMapping(path = "/rinforzi")
    public RinforzoResponse rinforzi(@RequestBody RinforzoRequest rinforzoRequest, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        RinforzoResponse response = partitaService.rinforzo(rinforzoRequest, partita);
        if (response.isVittoria())  {
            httpSession.setAttribute(partitaKey, null);
        }
        return response;
    }

    /**
     * Gestisce una richiesta http di inizio turno.
     * @param httpSession la sessione http da cui recuperare la partita
     * @return informazioni necessarie a inizializzare il turno nel browser
     */
    @PostMapping(path = "/inizia-turno")
    public IniziaTurnoDTO iniziaTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        return partitaService.iniziaTurno(partita);
    }

    /**
     * Gestisce una richiesta http relativa al gioco di un tris di carte territorio.
     * @param trisDTO informazioni su che carte intende usare il giocatore
     * @param httpSession la sessione http da cui recuperare la partita
     * @return il numero di carte bonus ottenute
     */
    @PostMapping(path = "/tris")
    public int giocaTris(@RequestBody TrisDTO trisDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        return partitaService.giocaTris(trisDTO, partita);
    }

    /**
     * Gestisce una richiesta http di attacco.
     * @param attaccoDTO informazioni relative all'attacco
     * @param httpSession la sessione http da cui recuperare la partita
     */
    @PostMapping(path = "/attacco")
    public void attacco(@RequestBody AttaccoDTO attaccoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        partitaService.attacco(attaccoDTO, partita);
    }

    /**
     * Gestisce una richiesta http per la difesa di un territorio. Se in seguito al combattimento l'attaccante ha
     * raggiunto il proprio obiettivo, la partita viene terminata ed eliminata dalla sessione.
     * @param difesaRequest informazioni relative a come deve svolgersi la difesa
     * @param httpSession la sessione http da cui recuperare la partita
     * @return l'esito del combattimento
     */
    @PostMapping(path = "/difesa")
    public DifesaResponse difesa(@RequestBody DifesaRequest difesaRequest, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        DifesaResponse difesa = partitaService.difesa(difesaRequest, partita);
        if(difesa.isObiettivoRaggiuntoAtt())
            httpSession.setAttribute(partitaKey, null);

        return difesa;
    }

    /**
     * Gestisce una richiesta http relativa ad uno spostamento di armate tra due territori. Se tramite lo spostamento
     * il giocatore raggiunge il proprio obiettivo, la partita viene terminata ed eliminata dalla sessione.
     * @param spostamentoDTO le informazioni riguardanti lo spostamento richiesto
     * @param httpSession la sessione http da cui recuperare la partita
     * @return true se tramite questo spostamento è stato raggiunto l'obiettivo del giocatore che lo ha effettuato
     */
    @PostMapping(path = "/spostamento")
    public boolean spostamento(@RequestBody SpostamentoDTO spostamentoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        boolean obiettivoRaggiunto = partitaService.spostamentoStrategico(spostamentoDTO, partita);
        if (obiettivoRaggiunto)
            httpSession.setAttribute(partitaKey, null);
        return obiettivoRaggiunto;
    }

    /**
     * Gestisce le richieste http relative alla fine di un turno.
     * @param httpSession la sessione http da cui recuperare la partita
     * @return la carta territorio pescata, se ce n'è una
     */
    @PostMapping(path = "/fine-turno")
    public CartaTerritorioDTO fineTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        return partitaService.fineTurno(partita);
    }
}
