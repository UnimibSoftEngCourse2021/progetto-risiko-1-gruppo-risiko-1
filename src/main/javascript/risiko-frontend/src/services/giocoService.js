import _axios from "@/plugins/axios";

export default {
    nuovoGioco,
    inviaRinforzi,
    nuovoTurno,
    attacco,
    difesa,
    spostamento
}

function nuovoGioco(configurazione) {
    return _axios.post("api/gioco", configurazione)
}

function inviaRinforzi(rinforzi) {
    return _axios.post("api/rinforzi", rinforzi)
}

function nuovoTurno() {
    return _axios.post("api/inizia-turno")
}

function attacco(payload) {
    return _axios.post("api/attacco", payload)
}

function difesa(payload) {
    return _axios.post("api/difesa", payload)
}

function spostamento(payload) {
    return _axios.post("api/spostamento", payload)
}