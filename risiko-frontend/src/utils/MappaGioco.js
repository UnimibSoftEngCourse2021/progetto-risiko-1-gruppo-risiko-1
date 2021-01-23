export class MappaGioco {
    constructor(strutturaMappa) {
        this.stati = [];
        strutturaMappa.continenti.forEach(continente => {
            const statiCont = continente.stati.map(stato => ({ ...stato, continente: continente.id }));

            this.stati.push(...statiCont);
        });

        this.continenti = strutturaMappa.continenti.map(continente => {
            const { id, armateBonus, nome } = continente;
            const statiId = continente.stati.map(stato => stato.id);

            return { id, armateBonus, nome, stati: statiId };
        });
    }

    trovaStatoId(id) {
        return this.stati.find(stato => stato.id === id);
    }

    confinanti(stato1, stato2) {
        return stato1.confinanti.find(statoId => statoId === stato2.id) !== undefined;
    }

    continentiConquistati(nomeGiocatore) {
        return this.continenti.filter(continente =>
            this.stati.find(s => (s.continente === continente.id && s.proprietario !== nomeGiocatore)) === undefined);
    }

    modificaArmateStato(statoId, armate) {
        const stato = this.trovaStatoId(statoId);
        stato.armate += armate;
    }
}
