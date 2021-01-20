import Vue from "vue";
import Vuex from "vuex";
import mappeService from "@/services/mappeService";
import giocoService from "@/services/giocoService";
import { MappaInCostruzione } from "@/store/MappaInCostruzione";
import { Gioco } from "@/store/Gioco";

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        gioco: {
            on: false,
            winner: null
        },
        mappe: [],
        mappaInCostruzione: null,
        loading: false,
        error: false
    },
    mutations: {
        setError(state, error) {
            state.error = error;
        },
        setLoading(state, isLoading) {
            state.loading = isLoading;
        },
        setListaMappe(state, mappe) {
            state.mappe = mappe;
        },
        setNuovaMappaInCostruzione(state) {
            state.mappaInCostruzione = new MappaInCostruzione();
        },
        startGame(state, data) {
            const { giocatori, colors, mappa, giocatoreAttivo } = data;

            state.gioco = new Gioco(giocatori, colors, mappa, giocatoreAttivo);
        },
        addRinforzi(state, rinforzi) {
            state.gioco.effettuaRinforzi(rinforzi);
        },
        setTurno(state, { turno, pescato }) {
            state.gioco.activePlayerIndex = state.gioco.giocatori.findIndex(g => g.nome === turno.giocatore);
            state.gioco.turno = {
                num: turno.numeroTurno,
                giocatore: turno.giocatore,
                armateStati: turno.armateStati,
                armateContinenti: turno.armateContinenti,
                armateTris: 0,
                fase: "rinforzi",
                tris: false,
                giocatorePrecedentePescato: pescato
            };
            state.gioco.giocatori[state.gioco.activePlayerIndex].armateDisponibili = turno.armateStati + turno.armateContinenti;
        },
        setActivePlayer(state, nome) {
            state.gioco.activePlayerIndex = state.gioco.giocatori.findIndex(g => g.nome === nome);
        },
        finePreparazione(state) {
            state.gioco.preparazione = false;
        },
        iniziaAttacco(state) {
            state.gioco.combattimento.inCorso = true;
        },
        setStatoAttaccante(state, stato) {
            state.gioco.combattimento.attaccante = stato.id;
        },
        setStatoDifensore(state, stato) {
            state.gioco.combattimento.difensore = stato.id;
        },
        setEsitoCombattimento(state, { dadoAtt, dadoDif, vittimeAtt, vittimeDif, vittoriaAtt, obiettivoRaggiuntoAtt }) {
            state.gioco.turno.fase = "combattimento";
            state.gioco.combattimento = { ...state.gioco.combattimento, dadoAtt, dadoDif, vittimeAtt, vittimeDif, vittoriaAtt };
            const statoAttaccante = state.gioco.mappa.trovaStatoId(state.gioco.combattimento.attaccante);

            statoAttaccante.armate -= vittimeAtt;
            const statoDifensore = state.gioco.mappa.trovaStatoId(state.gioco.combattimento.difensore);

            statoDifensore.armate -= vittimeDif;
            if (obiettivoRaggiuntoAtt) {
                state.gioco.winner = state.gioco.giocatori[state.gioco.activePlayerIndex];
                state.gioco.combattimento.inCorso = false;
            } else if (vittoriaAtt) {
                Vue.set(statoDifensore, "proprietario", statoAttaccante.proprietario);
            } else {
                state.gioco.combattimento.inCorso = false;
            }
        },
        clearCombattimento(state) {
            state.gioco.combattimento = { inCorso: false, attaccante: null, difensore: null, armateAttaccante: null, armateDifensore: null };
        },
        setSpostamento(state, spostamento) {
            if (!state.gioco.combattimento.inCorso) {
                state.gioco.turno.fase = "spostamento";
            }
            const statoPartenza = state.gioco.mappa.trovaStatoId(spostamento.statoPartenza);
            const statoArrivo = state.gioco.mappa.trovaStatoId(spostamento.statoArrivo);

            statoPartenza.armate -= spostamento.armate;
            statoArrivo.armate += spostamento.armate;
        },
        setSpostamentoInCorso(state, inCorso) {
            state.gioco.spostamentoInCorso = inCorso;
        },
        aggiungiCartaTerritorio(state, carta) {
            const giocatoreAttivo = state.gioco.giocatori[state.gioco.activePlayerIndex];

            giocatoreAttivo.carteTerritorio.push(carta);
        },
        tris(state, { tris, armate }) {
            const giocatoreAttivo = state.gioco.giocatori[state.gioco.activePlayerIndex];

            giocatoreAttivo.armateDisponibili += armate;
            tris.forEach(idCarta => {
                const index = giocatoreAttivo.carteTerritorio.findIndex(c => c.id === idCarta);

                giocatoreAttivo.carteTerritorio.splice(index, 1);
            });
            state.gioco.turno.tris = true;
            state.gioco.turno.armateTris = armate;
        },
        terminaPartita(state) {
            state.gioco.on = false;
        },
        clearMappaInCostruzione(state) {
            state.mappaInCostruzione = null;
        },
        setWinner(state) {
            state.gioco.winner = state.gioco.giocatori[state.gioco.activePlayerIndex];
        }
    },
    actions: {
        async downloadMappe({ commit }) {
            const { data } = await mappeService.getListaMappe();

            commit("setListaMappe", data);
        },
        async startGame({ commit }, config) {
            const { giocatori, mappaId, mod, unicoObiettivo, colors } = config;
            const { data } = await giocoService.nuovoGioco({ giocatori, mappaId, mod, unicoObiettivo });

            commit("startGame", { ...data, colors });
        },
        async inviaRinforzi({ commit, state }, rinforzi) {

            // effettua i rinforzi
            const rinforziMap = {};

            rinforzi.forEach(r => rinforziMap[String(r.id)] = r.quantity);
            const { data } = await giocoService.inviaRinforzi({
                giocatore: state.gioco.giocatori[state.gioco.activePlayerIndex].nome,
                rinforzi: rinforziMap
            });

            commit("addRinforzi", rinforzi);

            const { giocatore, preparazione, vittoria } = data;

            if (vittoria) {
                commit("setWinner");
            } else if (state.gioco.preparazione && !preparazione) {

                // se in fase preparazione, bisogna valutare se essa è terminata e qual è il prossimo giocatore attivo
                commit("finePreparazione");
                const { data } = await giocoService.nuovoTurno();

                commit("setTurno", { turno: data, pescato: false });
            } else if (preparazione) {
                commit("setActivePlayer", giocatore);
            }
        },
        async confermaAttacco({ commit, state }) {
            const attaccoPayload = {
                giocatore: state.gioco.giocatori[state.gioco.activePlayerIndex].nome,
                attaccante: state.gioco.combattimento.attaccante,
                difensore: state.gioco.combattimento.difensore,
                armate: state.gioco.combattimento.armateAttaccante
            };

            await giocoService.attacco(attaccoPayload);
            const proprietarioDif = state.gioco.mappa.trovaStatoId(state.gioco.combattimento.difensore).proprietario;
            const difesaPayload = {
                giocatore: proprietarioDif,
                armate: state.gioco.combattimento.armateDifensore
            };
            const { data } = await giocoService.difesa(difesaPayload);

            commit("setEsitoCombattimento", data);
        },
        async spostamento({ commit }, spostamento) {
            const response = await giocoService.spostamento(spostamento);

            commit("setSpostamento", spostamento);
            const vittoria = response.data;

            if (vittoria) {
                commit("setWinner");
            }
        },
        async terminaTurno({ commit }) {
            const { data } = await giocoService.fineTurno();

            if (data) {
                commit("aggiungiCartaTerritorio", data);
            }
            const ris = await giocoService.nuovoTurno();

            commit("setTurno", { turno: ris.data, pescato: !!data.id });
        },
        async giocaTris({ commit, state }, tris) {
            const payload = {
                giocatore: state.gioco.giocatori[state.gioco.activePlayerIndex].nome,
                tris
            };
            const { data } = await giocoService.giocaTris(payload);

            commit("tris", { tris, armate: data });
        },
        async inserisciMappa({ commit }, mappa) {
            await mappeService.inserisciMappa(mappa);
            commit("clearMappaInCostruzione");
        }
    },
    getters: {
        mappaInCostruzione(state) {
            return state.mappaInCostruzione;
        },
        loading(state) {
            return state.loading;
        },
        gameActive(state) {
            return state.gioco.on;
        },
        gameColors(state) {
            return state.gioco.colors;
        },
        mapNetwork(state) {
            const nodes = state.gioco.mappa.stati.map(stato => ({
                id: stato.id,
                group: stato.proprietario,
                label: String(stato.armate)
            }));

            const edges = [];

            for (const stato of state.gioco.mappa.stati) {
                for (const idConfinante of stato.confinanti) {
                    if (!edges.find(edge => edge.from === idConfinante && edge.to === stato.id)) {
                        edges.push({ from: stato.id, to: idConfinante });
                    }
                }
            }
            return { nodes, edges };
        },
        anteprimaNetwork(state) {
            return state.mappaInCostruzione.asNetwork();
        },
        mappe(state) {
            return state.mappe;
        },
        giocatori(state) {
            return state.gioco.on ? state.gioco.giocatori : [];
        },
        fasePreparazione(state) {
            return state.gioco.on && state.gioco.preparazione;
        },
        turno(state) {
            return state.gioco.on ? state.gioco.turno : null;
        },
        giocatoreAttivo(state) {
            return state.gioco.on
                ? state.gioco.getGiocatoreAttivo().nome
                : "";
        },
        mappaGioco(state) {
            return state.gioco.on ? state.gioco.mappa : null;
        },
        armateDisponibili(state) {
            return state.gioco.on
                ? state.gioco.getGiocatoreAttivo().armateDisponibili
                : 0;
        },
        bloccaRinforzi(state) {
            return !state.gioco.on ||
          (!state.gioco.preparazione && state.gioco.turno.fase !== "rinforzi");
        },
        bloccaCombattimenti(state) {
            return !state.gioco.on ||
                state.gioco.preparazione ||
                state.gioco.getGiocatoreAttivo().armateDisponibili > 0 ||
                state.gioco.turno.fase === "spostamento";
        },
        statoAttaccante(state) {
            if (!state.gioco.on || !state.gioco.combattimento.inCorso) {
                return null;
            }
            const idAttaccante = state.gioco.combattimento.attaccante;

            if (!idAttaccante) {
                return null;
            }
            return state.gioco.mappa.stati.find(s => s.id === idAttaccante);
        },
        combattimentoInCorso(state) {
            return state.gioco.on && state.gioco.combattimento.inCorso;
        },
        statoDifensore(state) {
            if (!state.gioco.on || !state.gioco.combattimento.inCorso) {
                return null;
            }
            const idDifensore = state.gioco.combattimento.difensore;

            if (!idDifensore) {
                return null;
            }
            return state.gioco.mappa.stati.find(s => s.id === idDifensore);
        },
        combattimento(state) {
            return state.gioco.on ? state.gioco.combattimento : null;
        },
        bloccaSpostamento(state) {
            return !state.gioco.on ||
                state.gioco.getGiocatoreAttivo().armateDisponibili > 0 ||
                state.gioco.combattimento.inCorso;
        },
        spostamentoInCorso(state) {
            return state.gioco.spostamentoInCorso;
        },
        carteTerritorio(state) {
            return state.gioco.on
                ? state.gioco.getGiocatoreAttivo().carteTerritorio
                : [];
        },
        winner(state) {
            return state.gioco.on ? state.gioco.winner : null;
        },
        infoGiocatori(state) {
            return state.gioco.giocatori.map(giocatore => {
                const statiConquistati = state.gioco.mappa.stati.filter(
                    s => s.proprietario === giocatore.nome
                );
                let armateTotali = 0;

                statiConquistati.forEach(s => armateTotali += s.armate);
                return {
                    ...giocatore,
                    statiConquistati: statiConquistati.length,
                    armateTotali
                };
            });
        },
        error(state) {
            return state.error
        }
    }
});
