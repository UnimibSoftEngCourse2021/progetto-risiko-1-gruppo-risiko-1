<template>
  <div>
    <v-subheader>COMBATTIMENTI</v-subheader>

    <v-btn v-if="!combattimentoInCorso"
           @click="iniziaAttacco"
           color="red" text>
      Inizia combattimento
    </v-btn>

    <div v-if="combattimentoInCorso">
      <v-btn color="red" text @click="annullaAttacco">Annulla</v-btn>
      <span class="text-caption d-block">Seleziona sulla mappa lo stato attaccante e scegli con quante truppe attaccare</span>

      <span class="d-block">Attaccante: {{statoAttaccante ? statoAttaccante.nome : ""}}</span>

      <v-select v-if="!!statoAttaccante"
                v-model="$store.state.gioco.combattimento.armateAttaccante"
                label="Seleziona il numero di armate attaccanti"
                :items="possibiliArmateAttaccanti" />

      <span class="d-block">Difensore: {{statoDifensore ? statoDifensore.nome : ""}}</span>

      <v-select v-if="!!statoDifensore"
                v-model="$store.state.gioco.combattimento.armateDifensore"
                label="Fai selezionare al difensore il numero di armate con cui intende difendersi"
                :items="possibiliArmateDifensori" />

      <v-btn color="red"
             text
             @click="confermaAttacco"
             :disabled="!statoDifensore || !statoAttaccante">Conferma attacco</v-btn>
    </div>

    <v-dialog v-model="showCombattimentoDialog" max-width="700px">
      <combattimento-dialog @close="showCombattimentoDialog = false"/>
    </v-dialog>
  </div>
</template>

<script>

import utils from "@/store/utils";
import CombattimentoDialog from "@/components/CombattimentoDialog";

export default {
  name: "GestoreCombattimenti",
  components: {CombattimentoDialog},
  data() {
    return {
      sceltaAttaccanteInCorso: false,
      sceltaBersaglioInCorso: false,
      showCombattimentoDialog: false
    }
  },
  methods: {
    annullaAttacco() {
      this.$store.commit("clearCombattimento")
    },
    iniziaAttacco() {
      this.$store.commit("iniziaAttacco")
    },
    onNodeSelected({ id }) {
      if (this.combattimentoInCorso) {
        let stato = utils.trovaStatoId(this.mappa, id)
        if (this.statoAttaccante === null) {
          if (stato.proprietario === this.activePlayer && stato.armate > 1) {
            this.$store.commit("setStatoAttaccante", stato)
          }
        } else if (this.statoDifensore === null) {
          if (stato.proprietario !== this.activePlayer && utils.confinanti(this.statoAttaccante, stato)) {
            this.$store.commit("setStatoDifensore", stato)
          }
        } // else do nothing
      }
    },
    async confermaAttacco() {
      await this.$store.dispatch("confermaAttacco")
      this.showCombattimentoDialog = true
    }
  },
  computed: {
    statoAttaccante() {
      return this.$store.getters.getStatoAttaccante
    },
    combattimentoInCorso() {
      return this.$store.getters.getCombattimentoInCorso
    },
    statoDifensore() {
      return this.$store.getters.getStatoDifensore
    },
    activePlayer() {
      return this.$store.getters.getActivePlayer
    },
    possibiliArmateAttaccanti() {
      if (!this.statoAttaccante)
        return []
      return Math.min(3, this.statoAttaccante.armate - 1)
    },
    possibiliArmateDifensori() {
      if (!this.statoDifensore)
        return []
      return Math.min(3, this.statoDifensore.armate)
    }
  }
}
</script>

<style scoped>

</style>