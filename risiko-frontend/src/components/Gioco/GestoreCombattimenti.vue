<template>
  <div>
    <h4 class="text-h6 ma-4">COMBATTIMENTI</h4>

    <!-- Inizia combattimento -->
    <v-btn v-if="!combattimentoInCorso"
           @click="iniziaAttacco"
           color="primary" block rounded>
      Inizia combattimento
    </v-btn>

    <!-- Combattimento in corso -->
    <div v-if="combattimentoInCorso">
      <span class="text-body-2 d-block">Seleziona sulla mappa lo stato attaccante, lo stato difensore e scegli con
        quante armate attaccare. Poi, fai scegliere al difensore con quante armate difendersi.</span>

      <!-- Stato attaccante -->
      <span class="d-block text-subtitle-2 mt-2">Attaccante: {{ statoAttaccante ? statoAttaccante.nome : "" }}</span>

      <!-- Armate stato attaccante -->
      <v-select :disabled="!statoAttaccante"
                v-model="combattimento.armateAttaccante"
                label="Numero armate attaccante"
                :items="possibiliArmateAttaccanti" />

      <!-- Stato difensore -->
      <span class="d-block text-subtitle-2">Difensore: {{ statoDifensore ? statoDifensore.nome : "" }}</span>

      <!-- Armate stato difensore -->
      <v-select :disabled="!statoDifensore"
                v-model="combattimento.armateDifensore"
                label="Numero armate difensore"
                :items="possibiliArmateDifensori" />

      <!-- Azioni -->
      <v-row>
        <v-spacer />
        <!-- Annulla -->
        <v-tooltip top>
          <template v-slot:activator="{ on, attrs }">
            <v-btn color="primary" icon @click="annullaAttacco" v-on="on" v-bind="attrs" class="mx-3">
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </template>
          <span>Annulla</span>
        </v-tooltip>

        <!-- Conferma attacco -->
        <v-tooltip top>
          <template v-slot:activator="{ on, attrs }">
            <v-btn color="primary" v-on="on" v-bind="attrs" icon
                   @click="lanciaAttacco"
                   :disabled="!statoDifensore || !statoAttaccante || !combattimento.armateAttaccante ||
                        !combattimento.armateDifensore">
              <v-icon>mdi-check</v-icon>
            </v-btn>
          </template>
          <span>Conferma attacco</span>
        </v-tooltip>
      </v-row>
    </div>

    <combattimento-dialog ref="combatDialog" />
  </div>
</template>

<script>

import CombattimentoDialog from "@/components/Gioco/CombattimentoDialog";
import { mapActions, mapGetters, mapMutations } from "vuex";

export default {
    name: "GestoreCombattimenti",
    components: { CombattimentoDialog },
    methods: {
        ...mapMutations(["clearCombattimento", "iniziaAttacco", "setStatoAttaccante", "setStatoDifensore"]),
        ...mapActions(["confermaAttacco"]),
        annullaAttacco() {
            this.clearCombattimento();
        },
        onNodeSelected({ id }) {
            const stato = this.mappaGioco.trovaStatoId(id);

            if (!this.statoAttaccante) {
                if (stato.proprietario === this.giocatoreAttivo && stato.armate > 1) {
                    this.setStatoAttaccante(stato);
                }
            } else if (!this.statoDifensore) {
                if (stato.proprietario !== this.giocatoreAttivo && this.mappaGioco.confinanti(this.statoAttaccante, stato)) {
                    this.setStatoDifensore(stato);
                }
            } // else do nothing
        },
        async lanciaAttacco() {
            await this.confermaAttacco();
            this.$refs.combatDialog.show();
        }
    },
    computed: {
        ...mapGetters(["statoAttaccante", "statoDifensore", "combattimentoInCorso", "giocatoreAttivo", "mappaGioco",
          "combattimento"]),
        possibiliArmateAttaccanti() {
            if (!this.statoAttaccante)
                return [];
            const max = Math.min(3, this.statoAttaccante.armate - 1);
            return [...Array(max).keys()].map(i => i + 1)
        },
        possibiliArmateDifensori() {
            if (!this.statoDifensore)
                return [];
            const max = Math.min(3, this.statoDifensore.armate);
          return [...Array(max).keys()].map(i => i + 1);
        }
    }
};
</script>

<style scoped>

</style>
