<template>
  <v-dialog max-width="700px" v-model="show" persistent>
    <v-card v-if="winner">
      <v-app-bar color="primary" dark class="d-flex align-center">
        <v-icon large class="mx-3">mdi-seal</v-icon>
        <v-app-bar-title>
          VITTORIA!
        </v-app-bar-title>
      </v-app-bar>

      <v-card-text class="black--text text-body-1 mt-5">
        <span class="d-block">{{ winner.nome }} ha vinto la partita, raggiungendo il suo obiettivo:</span>
        <v-card-text class="d-block mt-2">{{ winner.obiettivo }}</v-card-text>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" text @click="terminaPartita">Termina partita</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import {mapGetters, mapMutations} from "vuex";

export default {
name: "VittoriaDialog",
  data() {
    return {
      show: false
    }
  },
  watch: {
    winner(winner) {
      if (winner) {
        this.show = true;
      }
    }
  },
  computed: {
    ...mapGetters(["winner"])
  },
  methods: {
  ...mapMutations(["terminaPartita"])
  }
}
</script>

