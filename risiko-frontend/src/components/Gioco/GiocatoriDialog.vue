<template>
  <v-dialog v-model="showDialog" max-width="700px">
    <v-card>
      <v-app-bar color="primary" dark>
        <v-icon class="mx-3" large>mdi-account-multiple</v-icon>
        <v-app-bar-title>
          GIOCATORI
        </v-app-bar-title>
        <v-spacer/>
        <v-btn large icon @click="showDialog = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-app-bar>

      <span
          class="text-body-1 ma-5 d-block">Clicca il nome di un giocatore per visualizzarne le informazioni principali</span>

      <v-expansion-panels>
        <v-expansion-panel v-for="giocatore in infoGiocatori" :key="giocatore.nome">
          <v-expansion-panel-header class="text-subtitle-1">{{ giocatore.nome }}</v-expansion-panel-header>
          <v-expansion-panel-content class="ml-5 mb-5">
            <v-list>
              <v-list-item>
                <v-list-item-icon>
                  <v-icon>mdi-tank</v-icon>
                </v-list-item-icon>
                <v-list-item-content>Armate totali: {{ giocatore.armateTotali }} (+ {{ giocatore.armateDisponibili }} da
                  posizionare)
                </v-list-item-content>
              </v-list-item>

              <v-list-item>
                <v-list-item-icon>
                  <v-icon>mdi-map-marker</v-icon>
                </v-list-item-icon>
                <v-list-item-content>Stati conquistati: {{ giocatore.statiConquistati }}</v-list-item-content>
              </v-list-item>

              <v-list-item>
                <v-list-item-icon>
                  <v-icon>mdi-earth</v-icon>
                </v-list-item-icon>
                <v-list-item-content>Continenti conquistati: {{ continentiDelGiocatore(giocatore.nome) }}
                </v-list-item-content>
              </v-list-item>
            </v-list>

            <v-subheader>CARTE TERRITORIO</v-subheader>
            <v-slide-group v-if="giocatore.carteTerritorio.length > 0" show-arrows>
              <v-slide-item v-for="carta in giocatore.carteTerritorio" :key="carta.id">
                <v-chip color="primary" dark>
                  {{ carta.figura + (carta.statoRappresentato ? (": " + carta.statoRappresentato) : "") }}
                </v-chip>
              </v-slide-item>
            </v-slide-group>
            <span v-else class="d-block text-body-2">Non possiedi nessuna carta territorio</span>

          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-card>
  </v-dialog>
</template>

<script>

import {mapGetters} from "vuex";

export default {
  name: "GiocatoriDialog",
  data() {
    return {
      showDialog: false
    }
  },
  computed: {
    ...mapGetters(["infoGiocatori", "mappaGioco"])
  },
  methods: {
    continentiDelGiocatore(nomeGiocatore) {
      const continenti = this.mappaGioco.continentiConquistati(nomeGiocatore);

      return continenti.length === 0
          ? "Nessuno"
          : continenti.map(c => c.nome).join(", ");
    },
    show() {
      this.showDialog = true
    }
  }
};
</script>
