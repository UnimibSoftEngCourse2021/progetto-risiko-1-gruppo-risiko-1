<template>
  <v-card>
    <v-app-bar color="primary" dark class="d-flex align-center">
      <v-icon large class="mx-3">mdi-close-octagon</v-icon>
      <v-app-bar-title>
        ERRORE
      </v-app-bar-title>
    </v-app-bar>

    <v-card-text class="black--text text-subtitle-1 mt-5">
      Sfortunatamente c'è stato un errore nel nostro server. Contatta gli amministratori per ricevere ulteriori
      informazioni. Ora verrai riportato alla home page.
    </v-card-text>

    <v-card-actions>
      <v-spacer/>
      <v-btn color="primary" text @click="closeErrorDialog">
        CLOSE
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import {mapMutations} from "vuex";

export default {
  name: "ErrorDialog",
  methods: {
    ...mapMutations(["setError", "terminaPartita"]),
    closeErrorDialog() {
      this.terminaPartita()
      this.setError(false)
    }
  }
}
</script>

<style scoped>

</style>