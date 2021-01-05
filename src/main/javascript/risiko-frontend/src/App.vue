<template>
  <v-app>
    <v-app-bar
      app
      color="white"
      max-height="4rem"
    >
      <v-img src="logo.png" max-height="3rem" max-width="10rem"></v-img>

      <v-spacer/>
      <v-item-group>
        <v-btn text @click="openNewGameDialog">
          Nuovo gioco
        </v-btn>

        <v-btn text>
          Inserisci mappa
        </v-btn>
      </v-item-group>
    </v-app-bar>

    <v-main class="black-background">
      <Game v-if="gameActive">

      </Game>
    </v-main>

    <v-dialog v-model="showNuovoGiocoDialog" max-width="700px">
      <nuovo-gioco-dialog @close="showNuovoGiocoDialog = false"/>
    </v-dialog>
  </v-app>
</template>

<script>

import Game from "@/components/Game";
import NuovoGiocoDialog from "@/components/NuovoGiocoDialog";

export default {
  name: 'App',

  components: {
    NuovoGiocoDialog,
    Game
  },

  data() {
    return {
      showNuovoGiocoDialog: false
    }
  },

  methods: {
    async newGame() {
      await this.$store.dispatch("downloadMappa", 1);
      console.log(this.$store.getters.mapNetwork)

      this.$store.commit("startGame")
    },
    async openNewGameDialog() {
      await this.$store.dispatch("downloadMappe");
      this.showNuovoGiocoDialog = true
    }
  },

  computed: {
    gameActive() {
      return this.$store.getters.gameActive
    }
  }
};
</script>

<style>
  .black-background {
    background-color: #000;
  }
</style>
