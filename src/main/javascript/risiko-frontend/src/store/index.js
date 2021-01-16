import Vue from 'vue'
import Vuex from 'vuex'
import mappeService from "@/services/mappeService";
import giocoService from "@/services/giocoService";
import utils from "@/store/utils";
import MappaInCostruzione from "@/store/MappaInCostruzione";

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        gioco: {
            on: false,
            winner: null
        },
        mappe: [],
        mappaInCostruzione: {},
        loading: false,
        error: false
    },
    mutations: {
        setError(state, error) {
          state.error = error
        },
        setLoading(state, isLoading) {
          state.loading = isLoading
        },
        setListaMappe(state, mappe) {
            state.mappe = mappe;
        },
        setMappa(state, mappa) {
            state.mappa = utils.formattaMappa(mappa)
        },
        setNuovaMappaInCostruzione(state) {
            state.mappaInCostruzione = new MappaInCostruzione()
        },
        startGame(state, data) {
            let gioco = {}
            gioco.on = true
            gioco.mappa = utils.formattaMappa(data.mappa)
            gioco.giocatori = data.giocatori.map(giocatore => {
                return {
                    stati: giocatore.idStati,
                    armateDisponibili: giocatore.truppeDisponibili,
                    nome: giocatore.nome,
                    obiettivo: giocatore.obiettivo,
                    eliminato: false,
                    carteTerritorio: []
                }
            })
            gioco.giocatori.forEach(giocatore => {
                giocatore.stati.forEach(idStato => {
                    let stato = utils.trovaStatoId(gioco.mappa, idStato)
                    stato.proprietario = giocatore.nome
                    stato.armate = 1
                })
            })
            gioco.preparazione = true
            gioco.turno = {num: 0}
            gioco.activePlayerIndex = gioco.giocatori.findIndex(g => g.nome === data.giocatoreAttivo)
            gioco.primoGiocatoreIndex = gioco.activePlayerIndex
            gioco.combattimento = { inCorso: false, attaccante: null, difensore: null }
            gioco.spostamentoInCorso = false
            gioco.winner = null
            state.gioco = gioco
        },
        addRinforzi(state, rinforzi) {
            let totale = 0
            rinforzi.forEach(r => {
                let stato = state.gioco.mappa.stati.find(s => s.id === r.id)
                stato.armate += r.quantity
                totale += r.quantity
            })
            let giocatore = state.gioco.giocatori[state.gioco.activePlayerIndex]
            giocatore.armateDisponibili -= totale
            if (state.gioco.turno.tris) {
                state.gioco.turno.fase = "combattimento"
            }
            state.gioco.turno.armateStati = 0
            state.gioco.turno.armateContinenti = 0
            state.gioco.turno.armateTris = 0
        },
        setTurno(state, {turno, pescato}) {
            state.gioco.activePlayerIndex = state.gioco.giocatori.findIndex(g => g.nome === turno.giocatore)
            state.gioco.turno = {
                num: turno.numeroTurno,
                giocatore: turno.giocatore,
                armateStati: turno.armateStati,
                armateContinenti: turno.armateContinenti,
                armateTris: 0,
                fase: "rinforzi",
                tris: false,
                giocatorePrecedentePescato: pescato
            }
            state.gioco.giocatori[state.gioco.activePlayerIndex].armateDisponibili = turno.armateStati + turno.armateContinenti
        },
        setActivePlayer(state, nome) {
            state.gioco.activePlayerIndex = state.gioco.giocatori.findIndex(g => g.nome === nome)
        },
        finePreparazione(state) {
            state.gioco.preparazione = false
        },
        iniziaAttacco(state) {
            state.gioco.combattimento.inCorso = true
        },
        setStatoAttaccante(state, stato) {
            state.gioco.combattimento.attaccante = stato.id
        },
        setStatoDifensore(state, stato) {
            state.gioco.combattimento.difensore = stato.id
        },
        setEsitoCombattimento(state, { dadoAtt, dadoDif, vittimeAtt, vittimeDif, vittoriaAtt, obiettivoRaggiuntoAtt }) {
            state.gioco.turno.fase = "combattimento"
            state.gioco.combattimento = { ...state.gioco.combattimento, dadoAtt, dadoDif, vittimeAtt, vittimeDif, vittoriaAtt }
            let statoAttaccante = utils.trovaStatoId(state.gioco.mappa, state.gioco.combattimento.attaccante)
            statoAttaccante.armate -= vittimeAtt
            let statoDifensore = utils.trovaStatoId(state.gioco.mappa, state.gioco.combattimento.difensore)
            statoDifensore.armate -= vittimeDif
            if (obiettivoRaggiuntoAtt) {
                state.gioco.winner = state.gioco.giocatori[state.gioco.activePlayerIndex]
                state.gioco.combattimento.inCorso = false
            } else if (vittoriaAtt) {
                Vue.set(statoDifensore, "proprietario", statoAttaccante.proprietario)
            } else {
                state.gioco.combattimento.inCorso = false
            }
            
        },
        clearCombattimento(state) {
            state.gioco.combattimento = { inCorso: false, attaccante: null, difensore: null, armateAttaccante: null, armateDifensore: null }
        },
        setSpostamento(state, spostamento) {
            if (!state.gioco.combattimento.inCorso) {
                state.gioco.turno.fase = "spostamento"
            }
            let statoPartenza = utils.trovaStatoId(state.gioco.mappa, spostamento.statoPartenza)
            let statoArrivo = utils.trovaStatoId(state.gioco.mappa, spostamento.statoArrivo)
            statoPartenza.armate -= spostamento.armate
            statoArrivo.armate += spostamento.armate
        },
        setSpostamentoInCorso(state, inCorso) {
            state.gioco.spostamentoInCorso = inCorso
        },
        aggiungiCartaTerritorio(state, carta) {
            let giocatoreAttivo = state.gioco.giocatori[state.gioco.activePlayerIndex]
            giocatoreAttivo.carteTerritorio.push(carta)
        },
        tris(state, { tris, armate }) {
            let giocatoreAttivo = state.gioco.giocatori[state.gioco.activePlayerIndex]
            giocatoreAttivo.armateDisponibili += armate
            tris.forEach(idCarta => {
                let index = giocatoreAttivo.carteTerritorio.findIndex(c => c.id === idCarta)
                giocatoreAttivo.carteTerritorio.splice(index, 1)
            })
            state.gioco.turno.tris = true
            state.gioco.turno.armateTris = armate
        },
        terminaPartita(state) {
            state.gioco.on = false
        },
        clearMappaInCostruzione(state) {
            state.mappaInCostruzione = {}
        },
        setWinner(state) {
            state.gioco.winner = state.gioco.giocatori[state.gioco.activePlayerIndex]
        }
    },
    actions: {
        async downloadMappe({commit}) {
            let {data} = await mappeService.getListaMappe();
            commit("setListaMappe", data)
        },
        async downloadMappa({commit}, id) {
            let {data} = await mappeService.getMappa(id)
            commit("setMappa", data)
        },
        async startGame({commit}, config) {
            let {data} = await giocoService.nuovoGioco(config);
            commit("startGame", data)
        },
        async inviaRinforzi({commit, state}, rinforzi) {
            // effettua i rinforzi
            let rinforziMap = {}
            rinforzi.forEach(r => rinforziMap[String(r.id)] = r.quantity)
            let { data } = await giocoService.inviaRinforzi({
                giocatore: state.gioco.giocatori[state.gioco.activePlayerIndex].nome,
                rinforzi: rinforziMap
            })
            commit("addRinforzi", rinforzi)

            let { giocatore, preparazione, vittoria } = data;
            if (vittoria) {
                commit("setWinner")
            } else if (state.gioco.preparazione && !preparazione) {
                // se in fase preparazione, bisogna valutare se essa è terminata e qual è il prossimo giocatore attivo
                commit("finePreparazione")
                let { data } = await giocoService.nuovoTurno()
                commit("setTurno", { turno: data, pescato: false })
            } else if (preparazione) {
                commit("setActivePlayer", giocatore)
            }
        },
        async confermaAttacco({ commit, state }) {
            let attaccoPayload = {
                giocatore: state.gioco.giocatori[state.gioco.activePlayerIndex].nome,
                attaccante: state.gioco.combattimento.attaccante,
                difensore: state.gioco.combattimento.difensore,
                armate: state.gioco.combattimento.armateAttaccante
            }
            await giocoService.attacco(attaccoPayload)
            let proprietarioDif = utils.trovaStatoId(state.gioco.mappa, state.gioco.combattimento.difensore).proprietario
            let difesaPayload = {
                giocatore: proprietarioDif,
                armate: state.gioco.combattimento.armateDifensore
            }
            let { data } = await giocoService.difesa(difesaPayload)
            commit("setEsitoCombattimento", data)
        },
        async spostamento({ commit }, spostamento) {
            let response = await giocoService.spostamento(spostamento)
            commit("setSpostamento", spostamento)
            let vittoria = response.data
            if (vittoria) {
                commit("setWinner")
            }
        },
        async terminaTurno({ commit }) {
            let { data } = await giocoService.fineTurno()
            if (data) {
                commit("aggiungiCartaTerritorio", data)
            }
            let ris = await giocoService.nuovoTurno()
            commit("setTurno", {turno: ris.data, pescato: !!data.id })
        },
        async giocaTris({ commit, state }, tris) {
            let payload = {
                giocatore: state.gioco.giocatori[state.gioco.activePlayerIndex].nome,
                tris: tris
            }
            let { data } = await giocoService.giocaTris(payload)
            commit("tris", { tris, armate: data })
        },
        async inserisciMappa({ commit }, mappa) {
            await mappeService.inserisciMappa(mappa)
            commit("clearMappaInCostruzione")
        }
    },
    getters: {
        mappaInCostruzione(state) {
            return state.mappaInCostruzione
        },
        loading(state) {
          return state.loading
        },
        gameActive(state) {
            return state.gioco.on;
        },
        mapNetwork(state) {
            let nodes = state.gioco.mappa.stati.map(stato => {
                let proprietarioIndex = state.gioco.giocatori.findIndex(g => g.nome === stato.proprietario)
                return {
                    id: stato.id,
                    group: proprietarioIndex,
                    label: String(stato.armate)
                }
            })

            let edges = []
            for (let stato of state.gioco.mappa.stati) {
                for (let idConfinante of stato.confinanti) {
                    if (!edges.find(edge => edge.from === idConfinante && edge.to === stato.id)) {
                        edges.push({from: stato.id, to: idConfinante})
                    }
                }
            }
            return {nodes, edges}
        },
        anteprimaNetwork(state) {
            return state.mappaInCostruzione.asNetwork()
        },
        mappe(state) {
            return state.mappe
        },
        giocatori(state) {
            return state.gioco.on ? state.gioco.giocatori : []
        },
        fasePreparazione(state) {
            return state.gioco.on && state.gioco.preparazione
        },
        turno(state) {
            return state.gioco.on ? state.gioco.turno : null
        },
        giocatoreAttivo(state) {
            return state.gioco.on ? state.gioco.giocatori[state.gioco.activePlayerIndex].nome : ""
        },
        mappaGioco(state) {
            return state.gioco.on ? state.gioco.mappa : null
        },
        armateDisponibili(state) {
            return state.gioco.on ? state.gioco.giocatori[state.gioco.activePlayerIndex].armateDisponibili : 0
        },
        bloccaRinforzi(state) {
            return !state.gioco.on || (!state.gioco.preparazione && state.gioco.turno.fase !== "rinforzi")
        },
        bloccaCombattimenti(state) {
            return !state.gioco.on ||
                state.gioco.preparazione ||
                state.gioco.giocatori[state.gioco.activePlayerIndex].armateDisponibili > 0 || // non ha ancora effettuato i rinforzi obbligatori
                state.gioco.turno.fase === "spostamento"
        },
        statoAttaccante(state) {
            if (!state.gioco.on || !state.gioco.combattimento.inCorso)
                return null
            let idAttaccante = state.gioco.combattimento.attaccante
            if (!idAttaccante)
                return null
            return state.gioco.mappa.stati.find(s => s.id === idAttaccante)
        },
        combattimentoInCorso(state) {
            return state.gioco.on && state.gioco.combattimento.inCorso
        },
        statoDifensore(state) {
            if (!state.gioco.on || !state.gioco.combattimento.inCorso)
                return null
            let idDifensore = state.gioco.combattimento.difensore
            if (!idDifensore)
                return null
            return state.gioco.mappa.stati.find(s => s.id === idDifensore)
        },
        combattimento(state) {
            return state.gioco.on ? state.gioco.combattimento : null
        },
        bloccaSpostamento(state) {
            return !state.gioco.on ||
                state.gioco.giocatori[state.gioco.activePlayerIndex].armateDisponibili > 0 ||
                state.gioco.combattimento.inCorso
        },
        spostamentoInCorso(state) {
            return state.gioco.spostamentoInCorso
        },
        carteTerritorio(state) {
            return state.gioco.on ? state.gioco.giocatori[state.gioco.activePlayerIndex].carteTerritorio : []
        },
        winner(state) {
            return state.gioco.on ? state.gioco.winner : null
        },
        infoGiocatori(state) {
            return state.gioco.giocatori.map(giocatore => {
                let statiConquistati = state.gioco.mappa.stati.filter(s => s.proprietario === giocatore.nome)
                let armateTotali = 0
                statiConquistati.forEach(s => armateTotali += s.armate)
                return {
                    ...giocatore,
                    statiConquistati: statiConquistati.length,
                    armateTotali
                }
            })
        }
    }
})
