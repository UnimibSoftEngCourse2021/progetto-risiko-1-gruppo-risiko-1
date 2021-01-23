import { MappaGioco } from "@/utils/MappaGioco";

export class Gioco {
    constructor(giocatori, coloriGiocatori, strutturaMappa, giocatoreAttivo) {
        this.on = true;
        this.colors = coloriGiocatori;
        this.mappa = new MappaGioco(strutturaMappa);
        this.giocatori = giocatori.map(g => ({
            stati: g.idStati,
            armateDisponibili: g.truppeDisponibili,
            nome: g.nome,
            obiettivo: g.obiettivo,
            eliminato: false,
            carteTerritorio: []
        }));

        this.preparazione = true;
        this.turno = { num: 0 };
        this.activePlayerIndex = this.giocatori.findIndex(g => g.nome === giocatoreAttivo);
        this.primoGiocatoreIndex = this.activePlayerIndex;
        this.combattimento = { inCorso: false, attaccante: null, difensore: null, armateAttaccante: null, armateDifensore: null };
        this.spostamentoInCorso = false;
        this.winner = null;


        // piazza un'armata iniziale per ogni stato
        this.giocatori.forEach(giocatore => {
            giocatore.stati.forEach(idStato => {
                const stato = this.mappa.trovaStatoId(idStato);

                stato.proprietario = giocatore.nome;
                stato.armate = 1;
            });
        });
    }

    getGiocatoreAttivo() {
        return this.giocatori[this.activePlayerIndex];
    }

    effettuaRinforzi(elencoRinforzi) {
        elencoRinforzi.forEach(rinforzo => {
            this.mappa.modificaArmateStato(rinforzo.id, rinforzo.quantity);
        });
        const giocatore = this.getGiocatoreAttivo();

        if (this.preparazione) {
            const totaleRinforzi = elencoRinforzi.reduce(
                (acc, value) => acc + value.quantity, 0
            );

            giocatore.armateDisponibili -= totaleRinforzi;
        } else {
            if (this.turno.tris) {
                this.turno.fase = "combattimento";
            }
            this.turno.armateStati = 0;
            this.turno.armateContinenti = 0;
            this.turno.armateTris = 0;
            giocatore.armateDisponibili = 0;
        }
    }
}
