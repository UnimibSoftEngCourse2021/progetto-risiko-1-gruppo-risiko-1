<template>
  <v-card>
    <v-app-bar color="primary" dark class="d-flex align-center">
      <v-icon large class="mx-3">mdi-fencing</v-icon>
      <v-app-bar-title>
        ESITO COMBATTIMENTO
      </v-app-bar-title>
    </v-app-bar>

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

      <span class="d-block text-body-1">L'attaccante perde {{ combattimento.vittimeAtt }} armate mentre il difensore perde
        {{ combattimento.vittimeDif }} armate</span>
    </v-card-text>


    <div v-if="combattimento.vittoriaAtt" >
      <v-card-text >
        <v-alert type="success">Complimenti! Hai conquistato lo stato</v-alert>
        <div id="seleziona-truppe">
          <v-select
              label="Scegli quante truppe spostare"
              :items="truppeSpostabili"
              v-model="truppeDaSpostare"/>
        </div>

      </v-card-text>
      <v-card-actions>
        <v-spacer/>
        <v-btn color="primary" text @click="spostaTruppe" :disabled="!truppeDaSpostare">Sposta armate</v-btn>
      </v-card-actions>
    </div>
    <v-card-actions v-else>
      <v-spacer />
      <v-btn color="primary" text @click="chiudi">OK</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>

import utils from "@/store/utils";
import {mapActions, mapGetters, mapMutations} from "vuex";

export default {
  name: "CombattimentoDialog",
  data() {
    return {
      truppeDaSpostare: null
    }
  },
  computed: {
    ...mapGetters(["combattimento", "mappaGioco", "giocatoreAttivo"]),
    truppeSpostabili() {
      let ris = []
      if (!this.combattimento.vittoriaAtt)
        return ris
      let min = this.combattimento.armateAttaccante - this.combattimento.vittimeAtt
      let max = utils.trovaStatoId(this.mappaGioco, this.combattimento.attaccante).armate - 1
      for (let i = min; i <= max; i++)
        ris.push(i)
      return ris
    }
  },
  methods: {
    ...mapMutations(["clearCombattimento"]),
    ...mapActions(["spostamento"]),
    chiudi() {
      this.clearCombattimento()
      this.$emit("close")
    },
    async spostaTruppe() {
      let spostamento = {
        giocatore: this.giocatoreAttivo,
        statoPartenza: this.combattimento.attaccante,
        statoArrivo: this.combattimento.difensore,
        armate: this.truppeDaSpostare
      }
      await this.spostamento(spostamento)
      this.chiudi()
    }
  }
}
</script>

<style scoped>
  #seleziona-truppe {
    width: 50%;
    margin: auto;
  }
</style>