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
    }
}