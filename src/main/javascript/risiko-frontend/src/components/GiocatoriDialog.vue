<template>
  <v-card>
    <v-card-title>Informazioni giocatori</v-card-title>
    <v-expansion-panels>
      <v-expansion-panel v-for="giocatore in giocatori" :key="giocatore.nome">
        <v-expansion-panel-header>{{giocatore.nome}}</v-expansion-panel-header>
        <v-expansion-panel-content>
          <span class="d-block">Armate disponibili: {{giocatore.armateDisponibili}}</span>
          <span class="d-block">Continenti conquistati: {{continentiDelGiocatore(giocatore.nome)}}</span>
          <v-list >
            <v-subheader>CARTE TERRITORIO</v-subheader>
            <v-list-item-group v-if="giocatore.carteTerritorio.length > 0" >
              <v-list-item v-for="carta in giocatore.carteTerritorio" :key="carta.id" >
                <v-list-item-title>{{carta.figura}}</v-list-item-title>
                <v-list-item-subtitle>{{carta.statoRappresentato}}</v-list-item-subtitle>
              </v-list-item>
            </v-list-item-group>
            <span v-else class="d-block text-caption">Nessuna carta territorio</span>
          </v-list>

        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-card>
</template>

<script>

import utils from "@/store/utils";

export default {
  name: "GiocatoriDialog",
  computed: {
    giocatori() {
      return this.$store.getters.getGiocatori
    },
    mappa() {
      return this.$store.getters.getMappaGioco
    }
  },
  methods: {
    continentiDelGiocatore(nomeGiocatore) {
      let continenti = utils.continentiConquistati(this.mappa, nomeGiocatore)
      return continenti.length === 0 ?
          "Nessuno" :
          continenti.join(", ")
    }
  }
}
</script>

<style scoped>

</style>