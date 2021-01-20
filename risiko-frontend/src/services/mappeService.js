import _axios from "@/plugins/axios";

export default {
    getListaMappe,
    getMappa,
    inserisciMappa
};

/**
 *
 */
function getListaMappe() {
    return _axios.get("api/mappe");
}

/**
 * @param id
 */
function getMappa(id) {
    return _axios.get(`api/mappe/${id}`);
}

/**
 * @param mappa
 */
function inserisciMappa(mappa) {
    return _axios.post("api/mappe", mappa);
}
