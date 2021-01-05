import Vue from 'vue'
import Vuex from 'vuex'
import mappeService from "@/services/mappeService";
import giocoService from "@/services/giocoService";

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        gioco: {
            on: false,
            mappa: {}
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
            let stati = [];
            mappa.continenti.forEach(continente => {
                let statiCont = continente.stati.map(stato => {
                    return {...stato, continente: continente.id}
                })
                stati.push(...statiCont)
            })

            state.mappa.stati = stati;
            state.mappa.continenti = mappa.continenti.map(continente => {
                let {id, armateBonus, nome} = continente
                let statiId = continente.stati.map(stato => stato.id)
                return {id, armateBonus, nome, stati: statiId}
            });
        },
        startGame(state, data) {
            state.gioco.on = true
            console.log(data)
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
            commit("startGame", data)
        }
    },
    getters: {
        gameActive(state) {
            return state.gioco.on;
        },
        mapNetwork(state) {
            let nodes = state.mappa.stati.map(stato => { return {
                id: stato.id,
                title: stato.nome,
                group: state.mappa.continenti.findIndex(c => c.id === stato.continente)
            }})

            let edges = []
            for (let stato of state.mappa.stati) {
                for (let idConfinante of stato.confinanti) {
                    if (!edges.find(edge => edge.from === idConfinante && edge.to === stato.id)) {
                        edges.push({ from: stato.id, to: idConfinante })
                    }
                }
            }
            return { nodes, edges }
        },
        getMappe(state) { return state.mappe }
    }
})
