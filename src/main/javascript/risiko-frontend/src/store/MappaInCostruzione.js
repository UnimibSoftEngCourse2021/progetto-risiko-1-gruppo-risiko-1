function MappaInCostruzione() {
    this.nome = ""
    this.descrizione = ""
    this.numMinGiocatori = 3
    this.numMaxGiocatori = 6
    this.continenti = []
    this.stati = []
}

MappaInCostruzione.prototype.continentePresente = function continentePresente(nome) {
    return this.continenti.findIndex(c => c.nome === nome) !== -1
}

MappaInCostruzione.prototype.statoPresente = function statoPresente(nome) {
    return this.stati.findIndex(s => s.nome === nome) !== -1
}

MappaInCostruzione.prototype.addStato = function addStato(nome, nomeContinente) {
    if (this.continentePresente(nomeContinente) && !this.statoPresente(nome) && !this.continentePresente(nome)) {
        this.stati.push({ nome, confinanti: [], continente: nomeContinente })
    }
}

MappaInCostruzione.prototype.addContinente = function addContinente(nome, armateBonus) {
    if (!this.continentePresente(nome) && !this.statoPresente(nome)) {
        this.continenti.push({ nome, armateBonus })
    }
}

MappaInCostruzione.prototype.addConfine = function addConfine(nomeStato1, nomeStato2) {
    let stato1 = this.stati.find(s => s.nome === nomeStato1)
    let stato2 = this.stati.find(s => s.nome === nomeStato2)
    let giaConfinanti = stato1 &&  stato1.confinanti.findIndex(s => s === nomeStato2) !== -1
    if (stato1 && stato2 && !giaConfinanti) {
        stato1.confinanti.push(nomeStato2)
        stato2.confinanti.push(nomeStato1)
    }
}

MappaInCostruzione.prototype.confinanti = function confinanti(nomeStato1, nomeStato2) {
    let stato1 = this.stati.find(s => s.nome === nomeStato1)
    return stato1 && stato1.confinanti.findIndex(c => c === nomeStato2) !== -1
}

MappaInCostruzione.prototype.contieneStati = function contieneStati(nome) {
    return this.stati.findIndex(s => s.continente === nome) !== -1
}

MappaInCostruzione.prototype.removeContinente = function removeContinente(nome) {
    let continenteIndex = this.continenti.findIndex(c => c.nome === nome)
    let contieneStati = this.contieneStati(nome)
    if (continenteIndex !== -1 && !contieneStati) {
        this.continenti.splice(continenteIndex, 1)
    }
}

MappaInCostruzione.prototype.removeConfine = function removeConfine(nomeStato1, nomeStato2) {
    let stato1 = this.stati.find(s => s.nome === nomeStato1)
    let stato2 = this.stati.find(s => s.nome === nomeStato2)
    if (stato1 && stato2) {
        let indexStato2 = stato1.confinanti.findIndex(s => s === nomeStato2)
        let indexStato1 = stato2.confinanti.findIndex(s => s === nomeStato1)
        if (indexStato1 !== -1 && indexStato2 !== -1) {
            stato1.confinanti.splice(indexStato2, 1)
            stato2.confinanti.splice(indexStato1, 1)
        }
    }
}

MappaInCostruzione.prototype.removeStato = function removeStato(nome) {
    let index = this.stati.findIndex(s => s.nome === nome)
    if (index !== -1) {
        let stato = this.stati[index]
        stato.confinanti.forEach(nomeConf => {
            let conf = this.stati.find(s => s.nome === nomeConf)
            let indexStato = conf.confinanti.findIndex(c => c === nome)
            conf.confinanti.splice(indexStato, 1)
        })
        this.stati.splice(index, 1)
    }
}

MappaInCostruzione.prototype.asNetwork = function asNetwork() {
    let nodes = this.stati.map(s => { return {
        id: s.nome,
        title: s.continente.toUpperCase() + " - " + s.nome,
        group: s.continente
    }})

    let edges = []
    for (let stato of this.stati) {
        for (let nomeConfinante of stato.confinanti) {
            if (!edges.find(edge => edge.from === nomeConfinante && edge.to === stato.nome)) {
                edges.push({from: stato.nome, to: nomeConfinante})
            }
        }
    }

    return { nodes, edges }
}

MappaInCostruzione.prototype.isValid = function isValid() {
    let numMinContinenti = 4
    let numMaxContinenti = 8
    let numMinStatiPerContinente = 4
    let numMaxStatiPerContinente = 12

    if (!this.nome || !this.descrizione || !this.numMinGiocatori || !this.numMaxGiocatori || this.numMinGiocatori < 2 ||
        this.numMaxGiocatori > 8 || this.numMinGiocatori > this.numMaxGiocatori)
        return false

    if (this.continenti.length < numMinContinenti || this.continenti.length > numMaxContinenti)
        return false

    for (let c of this.continenti) {
        let stati = this.stati.filter(s => s.continente === c.nome)
        if (stati.length < numMinStatiPerContinente || stati.length > numMaxStatiPerContinente)
            return false
    }

    // verifica che il grafo sia connesso
    let vertici = this.stati.map(s => { return { ...s, visitato: false }})
    let verticiVisitati = 0
    vertici[0].visitato = true
    let queue = [ vertici[0] ]
    while (queue.length > 0) {
        verticiVisitati++
        let v = queue.shift()
        for (let nomeConf of v.confinanti) {
            let u = vertici.find(v => v.nome === nomeConf)
            if (!u.visitato) {
                u.visitato = true
                queue.push(u)
            }
        }
    }

    if (verticiVisitati < vertici.length)
        return false

    return true
}

MappaInCostruzione.prototype.asHierarchy = function asHierarchy() {
    let { nome, descrizione, numMinGiocatori, numMaxGiocatori } = this
    let mappa = { nome, descrizione, numMinGiocatori, numMaxGiocatori, continenti: [] }

    this.continenti.forEach(continente => {
        let stati = this.stati.filter(s => s.continente === continente.nome)
            .map(stato => { return {
                nome: stato.nome,
                confinanti: stato.confinanti
            } })
        mappa.continenti.push({
            nome: continente.nome,
            armateBonus: continente.armateBonus,
            stati
        })
    })

    return mappa
}

export default MappaInCostruzione