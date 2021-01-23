import _axios from "@/plugins/axios";

export default {
    getListaMappe,
    inserisciMappa
};

function getListaMappe() {
    return _axios.get("api/mappe");
}

function inserisciMappa(mappa) {
    return _axios.post("api/mappe", mappa);
}
