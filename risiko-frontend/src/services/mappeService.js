import _axios from "@/plugins/axios";

export default {
    getListaMappe,
    getMappa,
    inserisciMappa
}

function getListaMappe() {
    return _axios.get("api/mappe")
}

function getMappa(id) {
    return _axios.get("api/mappe/" + id)
}

function inserisciMappa(mappa) {
    return _axios.post("api/mappe", mappa)
}