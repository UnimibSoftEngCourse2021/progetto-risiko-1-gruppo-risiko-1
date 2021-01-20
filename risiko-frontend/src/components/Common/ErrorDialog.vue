<template>
  <v-dialog v-model="error" max-width="700px" persistent>
  <v-card>
    <v-app-bar class="d-flex align-center" color="primary" dark>
      <v-icon class="mx-3" large>mdi-close-octagon</v-icon>
      <v-app-bar-title>
        ERRORE
      </v-app-bar-title>
    </v-app-bar>

    <v-card-text class="black--text text-subtitle-1 mt-5">
      Sfortunatamente c'è stato un errore nel nostro server.
       Contatta gli amministratori per ricevere ulteriori
      informazioni. Ora verrai riportato alla home page.
    </v-card-text>

    <v-card-actions>
      <v-spacer/>
      <v-btn color="primary" text @click="closeErrorDialog">
        CLOSE
      </v-btn>
    </v-card-actions>
  </v-card>
  </v-dialog>
</template>

<script>
import {mapGetters, mapMutations} from "vuex";

export default {
    name: "ErrorDialog",
    computed: {
      ...mapGetters(["error"]),
    },
    methods: {
        ...mapMutations(["setError", "terminaPartita"]),
        closeErrorDialog() {
            this.terminaPartita();
            this.setError(false);
        }
    }
};
</script>

<style scoped>

</style>
