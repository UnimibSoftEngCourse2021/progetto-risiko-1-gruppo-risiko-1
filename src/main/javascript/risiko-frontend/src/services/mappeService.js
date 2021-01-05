import _axios from "@/plugins/axios";

export default {
    getListaMappe,
    getMappa
}

function getListaMappe() {
    return _axios.get("api/mappe")
}

function getMappa(id) {
    return _axios.get("api/mappe/" + id)
}