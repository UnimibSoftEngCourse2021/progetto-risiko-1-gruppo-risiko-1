import Vue from 'vue'
import Vuex from 'vuex'
import mappeService from "@/services/mappeService";
import giocoService from "@/services/giocoService";
import utils from "@/store/utils";

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        gioco: {
            on: false,
            mappa: {},
            giocatori: [],
            turno: {
                num: 0
            },
            preparazione: false,
            activePlayerIndex: "",
            primoGiocatoreIndex: "",
            combattimento: { inCorso: false, attaccante: null, difensore: null }
        },
        mappe: [],
        mappa: {
            continenti: [],
        }
    },
    mutations: {
        setListaMappe(state, mappe) {
            state.mappe = mappe;
        },
        setMappa(state, mappa) {
            state.mappa = utils.formattaMappa(mappa)
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
            state.gioco = gioco
        },
        addRinforzi(state, rinforzi) {
            let totale = 0
            let mappa = {...state.gioco.mappa}
            rinforzi.forEach(r => {
                let stato = mappa.stati.find(s => s.id === r.id)
                stato.armate += r.quantity
                totale += r.quantity
            })
            let giocatore = state.gioco.giocatori[state.gioco.activePlayerIndex]
            giocatore.armateDisponibili -= totale
            state.gioco.mappa = mappa
        },
        setTurno(state, turno) {
            state.gioco.activePlayerIndex = state.gioco.giocatori.findIndex(g => g.nome === turno.giocatore)
            state.gioco.turno = {
                num: turno.numeroTurno,
                giocatore: turno.giocatore,
                armateStati: turno.armateStati,
                armateContinenti: turno.armateContinenti,
                fase: "rinforzi"
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
        setEsitoCombattimento(state, { dadoAtt, dadoDif, vittimeAtt, vittimeDif, vittoriaAtt }) {
            state.gioco.turno.fase = "combattimento"
            state.gioco.combattimento = { ...state.gioco.combattimento, dadoAtt, dadoDif, vittimeAtt, vittimeDif, vittoriaAtt }
            let statoAttaccante = utils.trovaStatoId(state.gioco.mappa, state.gioco.combattimento.attaccante)
            statoAttaccante.armate -= vittimeAtt
            let statoDifensore = utils.trovaStatoId(state.gioco.mappa, state.gioco.combattimento.difensore)
            statoDifensore.armate -= vittimeDif
            if (vittoriaAtt) {
                Vue.set(statoDifensore, "proprietario", statoAttaccante.proprietario)
            } else {
                state.gioco.combattimento.inCorso = false
            }
        },
        clearCombattimento(state) {
            state.gioco.combattimento = { inCorso: false, attaccante: null, difensore: null, armateAttaccante: null, armateDifensore: null }
        },
        setSpostamento(state, spostamento) {
            let statoPartenza = utils.trovaStatoId(state.gioco.mappa, spostamento.statoPartenza)
            let statoArrivo = utils.trovaStatoId(state.gioco.mappa, spostamento.statoArrivo)
            statoPartenza.armate -= spostamento.armate
            statoArrivo.armate += spostamento.armate
        }
    },
    actions: {
        async downloadMappe({commit, state}) {
            if (state.mappe.length === 0) {
                let {data} = await mappeService.getListaMappe();
                commit("setListaMappe", data)
            }
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
            let {data} = await giocoService.inviaRinforzi({
                giocatore: state.gioco.giocatori[state.gioco.activePlayerIndex].nome,
                rinforzi: rinforziMap
            })
            commit("addRinforzi", rinforzi)

            // se in fase preparazione, bisogna valutare se essa è terminata e qual è il prossimo giocatore attivo
            if (state.gioco.preparazione && !data.preparazione) {
                commit("finePreparazione")
                let {data} = await giocoService.nuovoTurno()
                commit("setTurno", data)
            } else if (data.preparazione) {
                commit("setActivePlayer", data.giocatore)
            }
        },
        async nuovoTurno({state, commit}) {
            let {data} = await giocoService.nuovoTurno()
            if (data.giocatore !== state.gioco.giocatori[state.gioco.activePlayerIndex].nome) {
                commit("setTurno", data)
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
            await giocoService.spostamento(spostamento)
            commit("setSpostamento", spostamento)
        }
    },
    getters: {
        gameActive(state) {
            return state.gioco.on;
        },
        mapNetwork(state) {
            let nodes = state.gioco.mappa.stati.map(stato => {
                let proprietarioIndex = state.gioco.giocatori.findIndex(g => g.nome === stato.proprietario)
                return {
                    id: stato.id,
                    title: stato.nome + "<br/>" + state.gioco.giocatori[proprietarioIndex].nome,
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
        getMappe(state) {
            return state.mappe
        },
        getGiocatori(state) {
            return state.gioco.on ? state.gioco.giocatori : []
        },
        getFasePreparazione(state) {
            return state.gioco.on && state.gioco.preparazione
        },
        getTurno(state) {
            return state.gioco.on ? state.gioco.turno : null
        },
        getActivePlayer(state) {
            return state.gioco.on ? state.gioco.giocatori[state.gioco.activePlayerIndex].nome : ""
        },
        getMappaGioco(state) {
            return state.gioco.on ? state.gioco.mappa : null
        },
        getArmateDisponibili(state) {
            return state.gioco.on ? state.gioco.giocatori[state.gioco.activePlayerIndex].armateDisponibili : 0
        },
        getBloccaRinforzi(state) {
            return !state.gioco.on || (!state.gioco.preparazione && state.gioco.turno.fase !== "rinforzi")
        },
        getBloccaCombattimenti(state) {
            return !state.gioco.on ||
                state.gioco.preparazione ||
                state.gioco.giocatori[state.gioco.activePlayerIndex].armateDisponibili > 0 || // non ha ancora effettuato i rinforzi obbligatori
                state.gioco.turno.fase === "spostamento"
        },
        getStatoAttaccante(state) {
            if (!state.gioco.on || !state.gioco.combattimento.inCorso)
                return null
            let idAttaccante = state.gioco.combattimento.attaccante
            if (!idAttaccante)
                return null
            return state.gioco.mappa.stati.find(s => s.id === idAttaccante)
        },
        getCombattimentoInCorso(state) {
            return state.gioco.on && state.gioco.combattimento.inCorso
        },
        getStatoDifensore(state) {
            if (!state.gioco.on || !state.gioco.combattimento.inCorso)
                return null
            let idDifensore = state.gioco.combattimento.difensore
            if (!idDifensore)
                return null
            return state.gioco.mappa.stati.find(s => s.id === idDifensore)
        },
        getCombattimento(state) {
            return state.gioco.on ? state.gioco.combattimento : null
        }
    }
})
