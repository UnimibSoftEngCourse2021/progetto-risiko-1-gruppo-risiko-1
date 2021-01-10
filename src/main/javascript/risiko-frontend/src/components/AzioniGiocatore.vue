<template>
  <div>
    <h3 class="text-h5 text-center mt-6">Azioni</h3>
    <h3 class="text-h6 text-center">{{giocatore}}</h3>

    <gestore-rinforzi ref="gestoreRinforzi" v-if="!bloccaRinforzi"/>

    <v-divider class="my-5" />

    <gestore-combattimenti ref="gestoreCombattimenti" v-if="!bloccaCombattimenti"/>
  </div>
</template>

<script>
import GestoreRinforzi from "@/components/GestoreRinforzi";
import GestoreCombattimenti from "@/components/GestoreCombattimenti";

export default {
  name: "AzioniGiocatore",
  components: {GestoreCombattimenti, GestoreRinforzi},
  computed: {
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
    onNodeSelected({ id }) {
      if (!this.bloccaRinforzi && this.truppeDisponibili > 0) {
        this.$refs.gestoreRinforzi.onNodeSelected({ id })
      } else if (!this.bloccaCombattimenti && this.combattimentoInCorso) {
        this.$refs.gestoreCombattimenti.onNodeSelected({ id })
      }
    }
  }
}
</script>

<style scoped>

</style>