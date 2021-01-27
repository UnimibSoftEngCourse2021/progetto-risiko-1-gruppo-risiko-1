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

        <v-btn text @click="inserisciMappa" v-if="!gameActive">
          Inserisci mappa
        </v-btn>
        <v-btn v-else text @click="terminaPartita">Chiudi partita</v-btn>
      </v-item-group>
    </v-app-bar>

    <v-main class="black-background">
      <Game v-if="gameActive" :key="gameKey" />
      <v-img v-else src="board.png" max-height="50rem" contain />
    </v-main>

    <nuovo-gioco-dialog ref="nuovoGiocoDialog" @gameStarted="gameKey++" />
    <inserimento-mappa-dialog ref="inserimentoMappaDialog"/>

    <loader/>
    <error-dialog />
  </v-app>
</template>

<script>

import Game from "@/components/Gioco/Game";
import NuovoGiocoDialog from "@/components/Gioco/NuovoGiocoDialog";
import Loader from "@/components/Common/Loader";
import {mapGetters, mapMutations} from "vuex";
import ErrorDialog from "@/components/Common/ErrorDialog";
import InserimentoMappaDialog from "@/components/InserimentoMappa/InserimentoMappaDialog";

export default {
    name: "App",

    components: {
        InserimentoMappaDialog,
        ErrorDialog,
        Loader,
        NuovoGiocoDialog,
        Game
    },

    data() {
        return {
            gameKey: 0
        };
    },

    methods: {
      ...mapMutations(["terminaPartita"]),
        async openNewGameDialog() {
            await this.$store.dispatch("downloadMappe");
            this.$refs.nuovoGiocoDialog.show(true);
        },
        inserisciMappa() {
            this.$refs.inserimentoMappaDialog.show()
        }
    },

    computed: {
        ...mapGetters(["gameActive", "fasePreparazione", "turno", "giocatoreAttivo"]),

        gameSituation() {
            let ris;

            if (this.fasePreparazione) {
                ris = "Fase di preparazione - ";
            } else {
                ris = `Turno di `;
            }
            return ris + this.giocatoreAttivo;
        }
    }
};
</script>

<style>
  .black-background {
    background-color: #000;
  }
</style>
