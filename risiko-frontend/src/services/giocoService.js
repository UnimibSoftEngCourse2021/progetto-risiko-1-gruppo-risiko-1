import _axios from "@/plugins/axios";

export default {
    nuovoGioco,
    inviaRinforzi,
    nuovoTurno,
    attacco,
    difesa,
    spostamento,
    fineTurno,
    giocaTris
};

/**
 * @param configurazione
 */
function nuovoGioco(configurazione) {
    return _axios.post("api/gioco", configurazione);
}

/**
 * @param rinforzi
 */
function inviaRinforzi(rinforzi) {
    return _axios.post("api/rinforzi", rinforzi);
}

/**
 *
 */
function nuovoTurno() {
    return _axios.post("api/inizia-turno");
}

/**
 * @param payload
 */
function attacco(payload) {
    return _axios.post("api/attacco", payload);
}

/**
 * @param payload
 */
function difesa(payload) {
    return _axios.post("api/difesa", payload);
}

/**
 * @param payload
 */
function spostamento(payload) {
    return _axios.post("api/spostamento", payload);
}

/**
 *
 */
function fineTurno() {
    return _axios.post("api/fine-turno");
}

/**
 * @param payload
 */
function giocaTris(payload) {
    return _axios.post("api/tris", payload);
}
