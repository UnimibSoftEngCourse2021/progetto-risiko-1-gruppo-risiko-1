<template>
  <v-card>
    <v-app-bar color="primary" dark class="d-flex align-center">
      <v-icon large class="mx-3">mdi-account-multiple</v-icon>
      <v-app-bar-title>
        GIOCATORI
      </v-app-bar-title>
    </v-app-bar>

    <span class="text-body-1 ma-5 d-block">Clicca il nome di un giocatore per visualizzarne le informazioni principali</span>

    <v-expansion-panels>
      <v-expansion-panel v-for="giocatore in giocatori" :key="giocatore.nome">
        <v-expansion-panel-header class="text-subtitle-1">{{giocatore.nome}}</v-expansion-panel-header>
        <v-expansion-panel-content class="ml-5 mb-5">
          <v-list>
            <v-list-item>
              <v-list-item-icon>
                <v-icon>mdi-tank</v-icon>
              </v-list-item-icon>
              <v-list-item-content>Armate disponibili da posizionare: {{giocatore.armateDisponibili}}</v-list-item-content>
            </v-list-item>

            <v-list-item>
              <v-list-item-icon>
                <v-icon>mdi-earth</v-icon>
              </v-list-item-icon>
              <v-list-item-content>Continenti conquistati: {{continentiDelGiocatore(giocatore.nome)}}</v-list-item-content>
            </v-list-item>
          </v-list>

<!--          <v-list >-->
<!--            <v-subheader>CARTE TERRITORIO</v-subheader>-->
<!--            <v-list-item-group v-if="giocatore.carteTerritorio.length > 0" >-->
<!--              <v-list-item v-for="carta in giocatore.carteTerritorio" :key="carta.id" >-->
<!--                <v-list-item-title>{{carta.figura}}</v-list-item-title>-->
<!--                <v-list-item-subtitle>{{carta.statoRappresentato}}</v-list-item-subtitle>-->
<!--              </v-list-item>-->
<!--            </v-list-item-group>-->
<!--            <span v-else class="d-block text-caption">Nessuna carta territorio</span>-->
<!--          </v-list>-->

          <v-subheader>CARTE TERRITORIO</v-subheader>
          <v-slide-group show-arrows v-if="giocatore.carteTerritorio.length > 0">
            <v-slide-item v-for="carta in giocatore.carteTerritorio" :key="carta.id">
              <v-chip color="primary" dark>
                {{carta.figura + (carta.statoRappresentato ? (": " + carta.statoRappresentato) : "")}}
              </v-chip>
            </v-slide-item>
          </v-slide-group>
          <span v-else class="d-block text-body-2">Non possiedi nessuna carta territorio</span>

        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-card>
</template>

<script>

import utils from "@/store/utils";
import {mapGetters} from "vuex";

export default {
  name: "GiocatoriDialog",
  computed: {
    ...mapGetters(["giocatori", "mappaGioco"])
  },
  methods: {
    continentiDelGiocatore(nomeGiocatore) {
      let continenti = utils.continentiConquistati(this.mappaGioco, nomeGiocatore)
      return continenti.length === 0 ?
          "Nessuno" :
          continenti.join(", ")
    }
  }
}
</script>

<style scoped>

</style>