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
            activePlayer: "",
            primoGiocatore: ""
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
            state.gioco.on = true
            state.gioco.mappa = utils.formattaMappa(data.mappa)
            state.gioco.giocatori = data.giocatori.map(giocatore => { return {
                ...giocatore, armateDisponibili: data.nArmateIniziali, eliminato: false } })
            state.gioco.giocatori.forEach(giocatore => {
                giocatore.stati.forEach(idStato => {
                    giocatore.armateDisponibili--
                    let stato = utils.trovaStatoId(state.gioco.mappa, idStato)
                    stato.proprietario = giocatore.nome
                    stato.armate = 1
                })
            })
            state.gioco.preparazione = true
            state.gioco.turno = { num: 0 }
            state.gioco.activePlayer = data.primoGiocatore
            state.gioco.primoGiocatore = data.primoGiocatore
        },
        addRinforzi(state, rinforzi) {
            let totale = 0
            let mappa = { ...state.gioco.mappa }
            rinforzi.forEach(r => {
                let stato = mappa.stati.find(s => s.id === r.id)
                stato.armate += r.quantity
                totale += r.quantity
            })
            let giocatore = state.gioco.giocatori.find(g => g.nome === state.gioco.activePlayer)
            giocatore.armateDisponibili -= totale
            state.gioco.mappa = mappa
        },
        nuovoTurno(state, turno) {
            state.gioco.turno = {
                num: turno.numero,
                giocatore: turno.giocatore,
                armateStati: turno.armateStati,
                armateContinenti: turno.armateContinenti
            }
            state.gioco.giocatori.find(g => g.nome === turno.giocatore).armateDisponibili = turno.armateStati + turno.armateContinenti
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
        async startGame({ commit }, config) {
            let { data } = await giocoService.nuovoGioco(config);
            console.log(data)
            commit("startGame", data)
        },
        async inviaRinforzi({ commit, state }, rinforzi) {
            let rinforziMap = {}
            rinforzi.forEach(r => rinforziMap[String(r.id)] = r.quantity)
            await giocoService.inviaRinforzi({ giocatore: state.gioco.activePlayer, rinforzi: rinforziMap })
            commit("addRinforzi", rinforzi)
        },
        async nuovoTurno({ state, commit }) {
            let activeIndex = state.gioco.giocatori.findIndex(g => g.nome === state.gioco.activePlayer)
            let nextActiveIndex = (activeIndex + 1) % state.gioco.giocatori.length
            while (state.gioco.giocatori[nextActiveIndex].eliminato) {
                nextActiveIndex = (nextActiveIndex + 1) % state.gioco.giocatori.length
            }
            let giocatore = state.gioco.giocatori[nextActiveIndex]
            state.gioco.activePlayer = giocatore.nome
            if (!state.gioco.preparazione || giocatore.armateDisponibili === 0) {
                state.gioco.preparazione = false
                let { data } = giocoService.nuovoTurno();
                commit("nuovoTurno", data)
            }
        }
    },
    getters: {
        gameActive(state) {
            return state.gioco.on;
        },
        mapNetwork(state) {
            let nodes = state.gioco.mappa.stati.map(stato => { return {
                id: stato.id,
                title: stato.nome,
                group: state.gioco.mappa.continenti.findIndex(c => c.id === stato.continente),
                label: stato.proprietario + " " + String(stato.armate)
            }})

            let edges = []
            for (let stato of state.gioco.mappa.stati) {
                for (let idConfinante of stato.confinanti) {
                    if (!edges.find(edge => edge.from === idConfinante && edge.to === stato.id)) {
                        edges.push({ from: stato.id, to: idConfinante })
                    }
                }
            }
            return { nodes, edges }
        },
        getMappe(state) { return state.mappe },
        getGiocatori(state) { return state.gioco.on ? state.gioco.giocatori : [] },
        getFasePreparazione(state) { return state.gioco.on && state.gioco.preparazione },
        getTurno(state) { return state.gioco.on ? state.gioco.turno.num : 0 },
        getActivePlayer(state) { return state.gioco.on ? state.gioco.activePlayer : "" },
        getMappaGioco(state) { return state.gioco.on ? state.gioco.mappa : null },
        getArmateDisponibili(state) {
            return state.gioco.giocatori.find(g => g.nome === state.gioco.activePlayer).armateDisponibili
        }
    }
})
