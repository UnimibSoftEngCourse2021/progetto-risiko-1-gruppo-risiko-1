<template>
  <v-card>
    <v-app-bar color="primary" dark class="d-flex align-center">
      <v-icon large class="mx-3">mdi-bullseye-arrow</v-icon>
      <v-app-bar-title>
        OBIETTIVI
      </v-app-bar-title>
    </v-app-bar>

    <v-card-text>
      <v-alert type="warning" icon="mdi-alert" class="pa-3 mt-3">
        Attenzione! Cliccando su un giocatore, verrà mostrato il suo obiettivo segreto. Fai in modo che gli altri giocatori
        siano girati mentre lo leggi
      </v-alert>
    </v-card-text>

    <v-expansion-panels>
      <v-expansion-panel v-for="ob in giocatori" :key="ob.nome">
        <v-expansion-panel-header class="text-subtitle-2">Obiettivo di {{ob.nome}}</v-expansion-panel-header>
        <v-expansion-panel-content class="text-body-2">{{ob.obiettivo}}</v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-card>
</template>

<script>
import {mapGetters} from "vuex";

export default {
  name: "ObiettiviDialog",
  computed: {
    ...mapGetters(["giocatori"])
  }
}
</script>

<style scoped>
</style>