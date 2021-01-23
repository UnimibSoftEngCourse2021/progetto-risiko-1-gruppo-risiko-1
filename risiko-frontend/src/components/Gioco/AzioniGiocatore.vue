<template>
  <v-container>
    <v-banner color="primary" dark class="text-h5 text-center">Azioni</v-banner>

    <!-- Giocatore attivo -->
    <h3 class="text-h6 text-center my-4">Giocatore attivo: {{ giocatoreAttivo }}</h3>

    <!-- Azioni principali: rinforzi, combattimenti, spostamento -->
    <gestore-rinforzi ref="gestoreRinforzi" v-if="!bloccaRinforzi"/>
    <gestore-combattimenti class="my-5" ref="gestoreCombattimenti" v-if="!bloccaCombattimenti"/>
    <gestore-spostamento-strategico ref="gestoreSpostamento" v-if="!bloccaSpostamento" />

    <!-- termina turno -->
    <v-btn color="primary" block rounded class="my-12"
           @click="confermaTerminaTurno"
           :disabled="armateDisponibili > 0 || combattimentoInCorso || spostamentoInCorso"
    >TERMINA TURNO</v-btn>

    <!-- dialog fine turno -->
    <fine-turno-dialog  ref="fineTurnoDialog"/>
  </v-container>
</template>

<script>
import GestoreRinforzi from "@/components/Gioco/GestoreRinforzi";
import GestoreCombattimenti from "@/components/Gioco/GestoreCombattimenti";
import GestoreSpostamentoStrategico from "@/components/Gioco/GestoreSpostamentoStrategico";

import { mapActions, mapGetters } from "vuex";
import FineTurnoDialog from "@/components/Gioco/FineTurnoDialog";

export default {
    name: "AzioniGiocatore",
    components: { FineTurnoDialog, GestoreSpostamentoStrategico, GestoreCombattimenti, GestoreRinforzi },
    computed: {
        ...mapGetters(["bloccaSpostamento", "spostamentoInCorso", "bloccaRinforzi", "giocatoreAttivo", "bloccaCombattimenti",
            "combattimentoInCorso", "armateDisponibili"])
    },
    methods: {
        ...mapActions(["terminaTurno"]),
        onNodeSelected({ id }) {
            if (!this.bloccaRinforzi && this.armateDisponibili > 0) {
                this.$refs.gestoreRinforzi.onStatoSelezionato({ id });
            } else if (this.combattimentoInCorso) {
                this.$refs.gestoreCombattimenti.onStatoSelezionato({ id });
            } else if (this.spostamentoInCorso) {
                this.$refs.gestoreSpostamento.onStatoSelezionato({ id });
            }
        },
        async confermaTerminaTurno() {
            await this.terminaTurno();
            this.$refs.fineTurnoDialog.show();
        }
    }
};
</script>
