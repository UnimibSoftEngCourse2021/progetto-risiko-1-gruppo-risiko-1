<template>
  <div>
    <h3 class="text-h5 text-center mt-6">Azioni</h3>
    <h3 class="text-h6 text-center">{{giocatore}}</h3>

    <gestore-rinforzi ref="gestoreRinforzi" v-if="!bloccaRinforzi"/>

    <v-divider class="my-5" />

    <gestore-combattimenti ref="gestoreCombattimenti" v-if="!bloccaCombattimenti"/>

    <v-divider class="my-5" />

    <gestore-spostamento-strategico ref="gestoreSpostamento" v-if="!bloccaSpostamento" />

    <v-btn color="red" text @click="terminaTurno" :disabled="truppeDisponibili > 0 || combattimentoInCorso">termina turno</v-btn>
  </div>
</template>

<script>
import GestoreRinforzi from "@/components/GestoreRinforzi";
import GestoreCombattimenti from "@/components/GestoreCombattimenti";
import GestoreSpostamentoStrategico from "@/components/GestoreSpostamentoStrategico";

import {mapActions, mapGetters} from "vuex"

export default {
  name: "AzioniGiocatore",
  components: {GestoreSpostamentoStrategico, GestoreCombattimenti, GestoreRinforzi},
  computed: {
    ...mapGetters(["bloccaSpostamento", "spostamentoInCorso"]),
    bloccaRinforzi() {
      return this.$store.getters.getBloccaRinforzi
    },
    giocatore() {
      return this.$store.getters.getActivePlayer
    },
    bloccaCombattimenti() {
      return this.$store.getters.getBloccaCombattimenti
    },
    combattimentoInCorso() {
      return this.$store.getters.getCombattimentoInCorso
    },
    truppeDisponibili() {
      return this.$store.getters.getArmateDisponibili
    }
  },
  methods: {
    ...mapActions(["terminaTurno"]),
    onNodeSelected({ id }) {
      if (!this.bloccaRinforzi && this.truppeDisponibili > 0) {
        this.$refs.gestoreRinforzi.onNodeSelected({ id })
      } else if (!this.bloccaCombattimenti && this.combattimentoInCorso) {
        this.$refs.gestoreCombattimenti.onNodeSelected({ id })
      } else if (this.spostamentoInCorso) {
        this.$refs.gestoreSpostamento.onNodeSelected({ id })
      }
    }
  }
}
</script>

<style scoped>

</style>