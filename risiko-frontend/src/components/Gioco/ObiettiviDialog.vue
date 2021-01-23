<template>
  <v-dialog v-model="showDialog" max-width="700px">
    <v-card>
      <v-app-bar color="primary" dark>
        <v-icon class="mx-3" large>mdi-bullseye-arrow</v-icon>
        <v-app-bar-title>
          OBIETTIVI
        </v-app-bar-title>
        <v-spacer/>
        <v-btn icon large @click="showDialog = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-app-bar>

      <v-card-text>
        <v-alert class="pa-3 mt-3" icon="mdi-alert" type="warning">
          Attenzione! Cliccando su un giocatore, verrà mostrato il suo obiettivo segreto. Fai in modo che gli altri
          giocatori
          siano girati mentre lo leggi
        </v-alert>
      </v-card-text>

      <v-expansion-panels>
        <v-expansion-panel v-for="ob in giocatori" :key="ob.nome">
          <v-expansion-panel-header class="text-subtitle-2">Obiettivo di {{ ob.nome }}</v-expansion-panel-header>
          <v-expansion-panel-content class="text-body-2">{{ ob.obiettivo }}</v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-card>
  </v-dialog>
</template>

<script>
import {mapGetters} from "vuex";

export default {
  name: "ObiettiviDialog",
  data() {
    return {
      showDialog: true, // all'inizio della partita viene mostrato
    }
  },
  methods: {
    show() {
      this.showDialog = true
    }
  },
  computed: {
    ...mapGetters(["giocatori"])
  }
};
</script>