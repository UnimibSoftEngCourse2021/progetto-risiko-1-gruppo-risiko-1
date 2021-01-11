<template>
  <v-card>
    <v-card-title>Esito combattimento</v-card-title>

    <v-card-text>
      <span class="d-block">Tiri attaccante: {{ combattimento.dadoAtt }}</span>
      <span class="d-block">Tiri difensore: {{ combattimento.dadoDif }}</span>

      <span class="d-block">L'attaccante perde {{ combattimento.vittimeAtt }} mentre il difensore perde
        {{ combattimento.vittimeDif }} armate</span>
    </v-card-text>

    <div v-if="combattimento.vittoriaAtt">
      <v-card-text>
        <h6 class="text-h6">Complimenti! Hai conquistato lo stato</h6>
        <v-select
            label="Scegli quante truppe spostare nello stato conquistato"
            :items="truppeSpostabili"
            v-model="truppeDaSpostare"/>

      </v-card-text>

      <v-card-actions>
        <v-btn color="red" text @click="spostaTruppe" :disabled="!truppeDaSpostare">Sposta armate</v-btn>
      </v-card-actions>
    </div>
    <v-card-actions v-else>
      <v-btn color="red" text @click="chiudi">OK</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>

import utils from "@/store/utils";

export default {
  name: "CombattimentoDialog",
  data() {
    return {
      truppeDaSpostare: null
    }
  },
  computed: {
    combattimento() {
      return this.$store.getters.getCombattimento
    },
    mappa() {
      return this.$store.getters.getMappaGioco
    },
    truppeSpostabili() {
      let ris = []
      if (!this.combattimento.vittoriaAtt)
        return ris
      let min = this.combattimento.armateAttaccante - this.combattimento.vittimeAtt
      let max = utils.trovaStatoId(this.mappa, this.combattimento.attaccante).armate - 1
      for (let i = min; i <= max; i++)
        ris.push(i)
      return ris
    },
    activePlayer() {
      return this.$store.getters.getActivePlayer
    }
  },
  methods: {
    chiudi() {
      this.$store.commit("clearCombattimento")
      this.$emit("close")
    },
    async spostaTruppe() {
      let spostamento = {
        giocatore: this.activePlayer,
        statoPartenza: this.combattimento.attaccante,
        statoArrivo: this.combattimento.difensore,
        armate: this.truppeDaSpostare
      }
      await this.$store.dispatch("spostamento", spostamento)
      this.chiudi()
    }
  }
}
</script>

<style scoped>

</style>