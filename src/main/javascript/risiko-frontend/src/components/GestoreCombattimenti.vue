<template>
  <div>
    <h4 class="text-h6 ma-4">
      COMBATTIMENTI
    </h4>
    <v-btn v-if="!combattimentoInCorso"
           @click="iniziaAttacco"
           color="primary" block rounded>
      Inizia combattimento
    </v-btn>

    <div v-if="combattimentoInCorso">

      <span class="text-body-2 d-block">Seleziona sulla mappa lo stato attaccante e scegli con quante truppe attaccare</span>

      <span class="d-block text-subtitle-2 mt-2">Attaccante: {{ statoAttaccante ? statoAttaccante.nome : "" }}</span>

      <v-select :disabled="!statoAttaccante"
                v-model="$store.state.gioco.combattimento.armateAttaccante"
                label="Seleziona il numero di armate attaccanti"
                :items="possibiliArmateAttaccanti" />

      <span class="d-block text-subtitle-2">Difensore: {{ statoDifensore ? statoDifensore.nome : "" }}</span>

      <v-select :disabled="!statoDifensore"
                v-model="$store.state.gioco.combattimento.armateDifensore"
                label="Fai selezionare al difensore il numero di armate con cui intende difendersi"
                :items="possibiliArmateDifensori" />

      <v-row>
        <v-spacer />
        <v-tooltip top>
          <template v-slot:activator="{ on, attrs }">
            <v-btn color="primary" icon @click="annullaAttacco" v-on="on" v-bind="attrs" class="mx-3">
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </template>
          <span>Annulla</span>
        </v-tooltip>

        <v-tooltip top>
          <template v-slot:activator="{ on, attrs }">
            <v-btn color="primary" v-on="on" v-bind="attrs" icon
                   @click="lanciaAttacco"
                   :disabled="!statoDifensore || !statoAttaccante || !$store.state.gioco.combattimento.armateAttaccante ||
                        !$store.state.gioco.combattimento.armateDifensore">
              <v-icon>mdi-check</v-icon>
            </v-btn>
          </template>
          <span>Conferma attacco</span>
        </v-tooltip>


      </v-row>
    </div>

    <v-dialog v-model="showCombattimentoDialog" max-width="700px" persistent>
      <combattimento-dialog @close="showCombattimentoDialog = false"/>
    </v-dialog>
  </div>
</template>

<script>

import utils from "@/store/utils";
import CombattimentoDialog from "@/components/CombattimentoDialog";
import {mapActions, mapGetters, mapMutations} from "vuex";

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
    ...mapMutations(["clearCombattimento", "iniziaAttacco", "setStatoAttaccante", "setStatoDifensore"]),
    ...mapActions(["confermaAttacco"]),
    annullaAttacco() {
      this.clearCombattimento()
    },
    onNodeSelected({ id }) {
      let stato = utils.trovaStatoId(this.mappaGioco, id)
      if (this.statoAttaccante === null) {
        if (stato.proprietario === this.giocatoreAttivo && stato.armate > 1) {
          this.setStatoAttaccante(stato)
        }
      } else if (this.statoDifensore === null) {
        if (stato.proprietario !== this.giocatoreAttivo && utils.confinanti(this.statoAttaccante, stato)) {
          this.setStatoDifensore(stato)
        }
      } // else do nothing
    },
    async lanciaAttacco() {
      await this.confermaAttacco()
      this.showCombattimentoDialog = true
    }
  },
  computed: {
    ...mapGetters(["statoAttaccante", "statoDifensore", "combattimentoInCorso", "giocatoreAttivo", "mappaGioco"]),
    possibiliArmateAttaccanti() {
      let ris = []
      if (!this.statoAttaccante)
        return ris
      let max = Math.min(3, this.statoAttaccante.armate - 1)
      for (let i = 1; i <= max; i++)
        ris.push(i)
      return ris
    },
    possibiliArmateDifensori() {
      let ris = []
      if (!this.statoDifensore)
        return ris
      let max = Math.min(3, this.statoDifensore.armate)
      for (let i = 1; i <= max; i++)
        ris.push(i)
      return ris
    }
  }
}
</script>

<style scoped>

</style>