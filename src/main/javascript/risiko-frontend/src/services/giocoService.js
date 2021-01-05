import _axios from "@/plugins/axios";

export default {
    nuovoGioco
}

function nuovoGioco(configurazione) {
    return _axios.post("api/gioco", configurazione)
}