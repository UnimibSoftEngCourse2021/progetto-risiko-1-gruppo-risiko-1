<template>
  <v-app>
    <v-app-bar
      app
      color="white"
      max-height="4rem"
    >
      <v-img src="logo.png" max-height="3rem" max-width="10rem"></v-img>

      <v-spacer/>

      <h3 class="text--h3" v-if="gameActive">{{gameSituation}}</h3>

      <v-spacer/>
      <v-item-group>
        <v-btn color="primary" class="white--text" @click="openNewGameDialog">
          Nuovo gioco
        </v-btn>

        <v-btn text>
          Inserisci mappa
        </v-btn>
      </v-item-group>
    </v-app-bar>

    <v-main class="black-background">
      <Game v-if="gameActive" :key="gameKey">
      </Game>
      <v-img v-else src="board.png" max-height="50rem" contain>

      </v-img>
    </v-main>

    <v-dialog v-model="showNuovoGiocoDialog" max-width="700px">
      <nuovo-gioco-dialog @gameStarted="prepareGame" @close="showNuovoGiocoDialog = false"/>
    </v-dialog>

    <loader v-if="loading"/>
    <v-dialog v-model="$store.state.error" max-width="700px" persistent>
      <error-dialog />
    </v-dialog>
  </v-app>
</template>

<script>

import Game from "@/components/Game";
import NuovoGiocoDialog from "@/components/NuovoGiocoDialog";
import Loader from "@/components/Loader";
import {mapGetters} from "vuex";
import ErrorDialog from "@/components/ErrorDialog";

export default {
  name: 'App',

  components: {
    ErrorDialog,
    Loader,
    NuovoGiocoDialog,
    Game
  },

  data() {
    return {
      showNuovoGiocoDialog: false,
      gameKey: 0
    }
  },

  methods: {
    async openNewGameDialog() {
      await this.$store.dispatch("downloadMappe");
      this.showNuovoGiocoDialog = true
    },
    prepareGame() {
      this.showNuovoGiocoDialog = false
      this.gameKey++
    }
  },

  computed: {
    ...mapGetters(["gameActive", "loading", "fasePreparazione", "turno", "giocatoreAttivo"]),

    gameSituation() {
      let ris
      if (this.fasePreparazione)
        ris = "Fase di preparazione - "
      else
        ris = "Turno " + this.turno.num + " - "
      return ris + this.giocatoreAttivo
    }
  }
};
</script>

<style>
  .black-background {
    background-color: #000;
  }
</style>
