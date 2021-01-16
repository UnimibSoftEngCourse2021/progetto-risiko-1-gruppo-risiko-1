export default {
    formattaMappa(mappa) {
        let stati = [];
        mappa.continenti.forEach(continente => {
            let statiCont = continente.stati.map(stato => {
                return {...stato, continente: continente.id}
            })
            stati.push(...statiCont)
        })
        let ris = { stati: [], continenti: [] }
        ris.stati = stati;
        ris.continenti = mappa.continenti.map(continente => {
            let {id, armateBonus, nome} = continente
            let statiId = continente.stati.map(stato => stato.id)
            return {id, armateBonus, nome, stati: statiId}
        });
        return ris
    },

    trovaStatoId(mappa, id) {
        return mappa.stati.find(stato => stato.id === id)
    },

    confinanti(stato1, stato2) {
        return stato1.confinanti.findIndex(statoId => statoId === stato2.id) !== -1
    },

    continentiConquistati(mappa, nomeGiocatore) {
        let ris = []
        mappa.continenti.forEach(continente => {
            let index = continente.stati.findIndex(statoId => {
                let stato = this.trovaStatoId(mappa, statoId)
                return stato.proprietario !== nomeGiocatore
            })
            if (index === -1) { // nessuno stato del continente appartiene ad un altro giocatore
                ris.push(continente)
            }
        })
        return ris
    }
}