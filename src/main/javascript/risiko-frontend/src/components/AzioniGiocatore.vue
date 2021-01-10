<template>
  <div>
    <h3 class="text-h5 text-center mt-6">Azioni</h3>
    <h3 class="text-h6 text-center">{{giocatore}}</h3>

    <gestore-rinforzi ref="gestoreRinforzi" v-if="!bloccaRinforzi"/>

    <v-divider class="my-5" />

    <gestore-combattimenti v-if="!bloccaCombattimenti"/>
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
    }
  },
  methods: {
    onNodeSelected({ id }) {
      if (!this.bloccaRinforzi) {
        this.$refs.gestoreRinforzi.onNodeSelected({ id })
      }
    }
  }
}
</script>

<style scoped>

</style>