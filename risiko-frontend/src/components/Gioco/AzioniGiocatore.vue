<template>
  <v-container>
    <v-banner color="primary" dark class="text-h5 text-center">Azioni</v-banner>

    <h3 class="text-h6 text-center my-4">Giocatore attivo: {{ giocatoreAttivo }}</h3>

    <gestore-rinforzi ref="gestoreRinforzi" v-if="!bloccaRinforzi"/>


    <gestore-combattimenti class="my-5" ref="gestoreCombattimenti" v-if="!bloccaCombattimenti"/>


    <gestore-spostamento-strategico ref="gestoreSpostamento" v-if="!bloccaSpostamento" />

    <v-btn color="primary" block rounded class="my-12" @click="confermaTerminaTurno"
           :disabled="armateDisponibili > 0 || combattimentoInCorso">termina turno</v-btn>

    <v-dialog v-model="fineTurnoDialog" max-width="700px">
      <fine-turno-dialog  @close="fineTurnoDialog = false"/>
    </v-dialog>
  </v-container>
</template>

<script>
import GestoreRinforzi from "@/components/Gioco/GestoreRinforzi";
import GestoreCombattimenti from "@/components/Gioco/GestoreCombattimenti";
import GestoreSpostamentoStrategico from "@/components/Gioco/GestoreSpostamentoStrategico";

import {mapActions, mapGetters} from "vuex"
import FineTurnoDialog from "@/components/Gioco/FineTurnoDialog";

export default {
  name: "AzioniGiocatore",
  data() {
    return {
      fineTurnoDialog: false
    }
  },
  components: {FineTurnoDialog, GestoreSpostamentoStrategico, GestoreCombattimenti, GestoreRinforzi},
  computed: {
    ...mapGetters(["bloccaSpostamento", "spostamentoInCorso", "bloccaRinforzi", "giocatoreAttivo", "bloccaCombattimenti",
    "combattimentoInCorso", "armateDisponibili"])
  },
  methods: {
    ...mapActions(["terminaTurno"]),
    onNodeSelected({ id }) {
      if (!this.bloccaRinforzi && this.armateDisponibili > 0) {
        this.$refs.gestoreRinforzi.onNodeSelected({ id })
      } else if (!this.bloccaCombattimenti && this.combattimentoInCorso) {
        this.$refs.gestoreCombattimenti.onNodeSelected({ id })
      } else if (this.spostamentoInCorso) {
        this.$refs.gestoreSpostamento.onNodeSelected({ id })
      }
    },
    async confermaTerminaTurno() {
      await this.terminaTurno()
      this.fineTurnoDialog = true
    }
  }
}
</script>

<style scoped>

</style>