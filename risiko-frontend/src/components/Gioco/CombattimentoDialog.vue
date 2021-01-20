<template>
  <v-dialog v-model="showDialog" max-width="700px" persistent>
  <v-card>
    <v-app-bar color="primary" dark class="d-flex align-center">
      <v-icon large class="mx-3">mdi-fencing</v-icon>
      <v-app-bar-title>
        ESITO COMBATTIMENTO
      </v-app-bar-title>
    </v-app-bar>

    <!-- Esito dadi -->
    <v-card-text class="black--text">
      <v-row class="align-center mt-5">
        <span class="d-block text-subtitle-1 mx-3">Tiri attaccante: </span>
        <v-icon v-for="(dado, index) in combattimento.dadoAtt" :key="index" class="mx-3">
          mdi-dice-{{dado}}
        </v-icon>
      </v-row>

      <v-row class="align-center my-5">
        <span class="d-block text-subtitle-1 mx-3">Tiri difensore: </span>
        <v-icon v-for="(dado, index) in combattimento.dadoDif" :key="index" class="mx-3">
          mdi-dice-{{dado}}
        </v-icon>
      </v-row>

      <!-- Vittime -->
      <span class="d-block text-body-1">L'attaccante perde {{ combattimento.vittimeAtt }} armate mentre il difensore perde
        {{ combattimento.vittimeDif }} armate</span>
    </v-card-text>

    <!-- Conquista ? -->
    <v-card-text v-if="combattimento.vittoriaAtt">
      <v-alert type="success">Complimenti! Hai conquistato lo stato</v-alert>
    </v-card-text>

    <!-- Spostamento post-conquista (se non è finita la partita) -->
    <div v-if="combattimento.vittoriaAtt && !winner" >
      <v-card-text >
        <!-- Selezione truppe -->
        <div id="seleziona-truppe">
          <v-select
              label="Scegli quante truppe spostare"
              :items="truppeSpostabili"
              v-model="truppeDaSpostare"/>
        </div>
      </v-card-text>
      <!-- Conferma spostamento -->
      <v-card-actions>
        <v-spacer/>
        <v-btn color="primary" text @click="spostaTruppe" :disabled="!truppeDaSpostare">Sposta armate</v-btn>
      </v-card-actions>
    </div>

    <!-- Chiudi -->
    <v-card-actions v-else>
      <v-spacer />
      <v-btn color="primary" text @click="chiudi">OK</v-btn>
    </v-card-actions>
  </v-card>
  </v-dialog>
</template>

<script>

import { mapActions, mapGetters, mapMutations } from "vuex";

export default {
    name: "CombattimentoDialog",
    data() {
        return {
          truppeDaSpostare: null,
          showDialog: false
        };
    },
    computed: {
        ...mapGetters(["combattimento", "mappaGioco", "giocatoreAttivo", "winner"]),
        truppeSpostabili() {
            if (!this.combattimento.vittoriaAtt)
                return [];
            const min = this.combattimento.armateAttaccante - this.combattimento.vittimeAtt;
            const max = this.mappaGioco.trovaStatoId(this.combattimento.attaccante).armate - 1;
            return [...Array(max + 1 - min).keys()].map(i => i + min)
        }
    },
    methods: {
        ...mapMutations(["clearCombattimento"]),
        ...mapActions(["spostamento"]),
        chiudi() {
            this.truppeDaSpostare = null;
            this.clearCombattimento();
            this.showDialog = false;
        },
        async spostaTruppe() {
            const spostamento = {
                giocatore: this.giocatoreAttivo,
                statoPartenza: this.combattimento.attaccante,
                statoArrivo: this.combattimento.difensore,
                armate: this.truppeDaSpostare
            };

            await this.spostamento(spostamento);
            this.chiudi();
        },
      show() {
          this.showDialog = true
      }
    }
};
</script>

<style scoped>
  #seleziona-truppe {
    width: 50%;
    margin: auto;
  }
</style>
