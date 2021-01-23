export class MappaInCostruzione {
    constructor() {
        this.nome = "";
        this.descrizione = "";
        this.numMinGiocatori = 3;
        this.numMaxGiocatori = 6;
        this.continenti = [];
        this.stati = [];
    }

    continentePresente(nome) {
        return this.continenti.findIndex(c => c.nome === nome) !== -1;
    }

    statoPresente(nome) {
        return this.stati.findIndex(s => s.nome === nome) !== -1;
    }

    addStato(nome, nomeContinente) {
        if (this.continentePresente(nomeContinente) && !this.statoPresente(nome) && !this.continentePresente(nome)) {
            this.stati.push({ nome, confinanti: [], continente: nomeContinente });
        }
    }

    addContinente(nome, armateBonus) {
        if (!this.continentePresente(nome) && !this.statoPresente(nome)) {
            this.continenti.push({ nome, armateBonus });
        }
    }

    addConfine(nomeStato1, nomeStato2) {
        const stato1 = this.stati.find(s => s.nome === nomeStato1);
        const stato2 = this.stati.find(s => s.nome === nomeStato2);
        const giaConfinanti = stato1 && stato1.confinanti.findIndex(s => s === nomeStato2) !== -1;

        if (stato1 && stato2 && !giaConfinanti) {
            stato1.confinanti.push(nomeStato2);
            stato2.confinanti.push(nomeStato1);
        }
    }

    confinanti(nomeStato1, nomeStato2) {
        const stato1 = this.stati.find(s => s.nome === nomeStato1);

        return stato1 && stato1.confinanti.findIndex(c => c === nomeStato2) !== -1;
    }

    contieneStati(nome) {
        return this.stati.findIndex(s => s.continente === nome) !== -1;
    }

    removeContinente(nome) {
        const continenteIndex = this.continenti.findIndex(c => c.nome === nome);
        const contieneStati = this.contieneStati(nome);

        if (continenteIndex !== -1 && !contieneStati) {
            this.continenti.splice(continenteIndex, 1);
        }
    }

    removeConfine(nomeStato1, nomeStato2) {
        const stato1 = this.stati.find(s => s.nome === nomeStato1);
        const stato2 = this.stati.find(s => s.nome === nomeStato2);

        if (stato1 && stato2) {
            const indexStato2 = stato1.confinanti.findIndex(s => s === nomeStato2);
            const indexStato1 = stato2.confinanti.findIndex(s => s === nomeStato1);

            if (indexStato1 !== -1 && indexStato2 !== -1) {
                stato1.confinanti.splice(indexStato2, 1);
                stato2.confinanti.splice(indexStato1, 1);
            }
        }
    }

    removeStato(nome) {
        const index = this.stati.findIndex(s => s.nome === nome);

        if (index !== -1) {
            const stato = this.stati[index];

            stato.confinanti.forEach(nomeConf => {
                const conf = this.stati.find(s => s.nome === nomeConf);
                const indexStato = conf.confinanti.findIndex(c => c === nome);

                conf.confinanti.splice(indexStato, 1);
            });
            this.stati.splice(index, 1);
        }
    }

    asNetwork() {
        const nodes = this.stati.map(s => ({
            id: s.nome,
            title: `${s.continente.toUpperCase()} - ${s.nome}`,
            group: s.continente
        }));

        const edges = [];

        for (const stato of this.stati) {
            for (const nomeConfinante of stato.confinanti) {
                if (!edges.find(edge => edge.from === nomeConfinante && edge.to === stato.nome)) {
                    edges.push({ from: stato.nome, to: nomeConfinante });
                }
            }
        }

        return { nodes, edges };
    }

    isValid() {
        const numMinContinenti = 4;
        const numMaxContinenti = 8;
        const numMinStatiPerContinente = 4;
        const numMaxStatiPerContinente = 12;

        if (!this.nome || !this.descrizione || !this.numMinGiocatori || !this.numMaxGiocatori || this.numMinGiocatori < 2 ||
            this.numMaxGiocatori > 8 || this.numMinGiocatori > this.numMaxGiocatori) {
            return false;
        }

        if (this.continenti.length < numMinContinenti || this.continenti.length > numMaxContinenti) {
            return false;
        }

        for (const c of this.continenti) {
            const stati = this.stati.filter(s => s.continente === c.nome);

            if (stati.length < numMinStatiPerContinente || stati.length > numMaxStatiPerContinente) {
                return false;
            }
        }

        // verifica che il grafo sia connesso
        const vertici = this.stati.map(s => ({ ...s, visitato: false }));
        let verticiVisitati = 0;

        vertici[0].visitato = true;
        const queue = [vertici[0]];

        while (queue.length > 0) {
            verticiVisitati++;
            const v = queue.shift();

            for (const nomeConf of v.confinanti) {
                const u = vertici.find(vert => vert.nome === nomeConf);

                if (!u.visitato) {
                    u.visitato = true;
                    queue.push(u);
                }
            }
        }

        return verticiVisitati >= vertici.length;
    }

    asHierarchy() {
        const { nome, descrizione, numMinGiocatori, numMaxGiocatori } = this;
        const mappa = { nome, descrizione, numMinGiocatori, numMaxGiocatori, continenti: [] };

        this.continenti.forEach(continente => {
            const stati = this.stati.filter(s => s.continente === continente.nome)
                .map(stato => ({
                    nome: stato.nome,
                    confinanti: stato.confinanti
                }));

            mappa.continenti.push({
                nome: continente.nome,
                armateBonus: continente.armateBonus,
                stati
            });
        });

        return mappa;
    }
}
