<template>
  <v-container fluid>
    <v-row>
      <!-- Barra con le informazioni generali sulla partita -->
      <v-col cols="3" class="white d-flex flex-column">
        <game-info @evidenziaContinente="evidenziaContinente"/>
      </v-col>

      <!-- Mappa -->
      <v-col cols="6" class="pa-1">
        <Board ref="board" @nodeSelected="onNodeSelected"/>
      </v-col>

      <!-- Azioni del giocatore -->
      <v-col cols="3" class="white">
        <azioni-giocatore ref="azioniGiocatore"/>
      </v-col>
    </v-row>

    <v-dialog max-width="700px" v-model="showWinnerDialog" persistent>
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
  </v-container>

</template>

<script>
import Board from "@/components/Gioco/Board";
import GameInfo from "@/components/Gioco/GameInfo";
import AzioniGiocatore from "@/components/Gioco/AzioniGiocatore";
import {mapGetters, mapMutations} from "vuex";

export default {
  name: "Game",
  components: {AzioniGiocatore, Board, GameInfo},
  data() {
    return {
      showWinnerDialog: false
    }
  },

  computed: {
    ...mapGetters(["winner"])
  },

  watch: {
    winner(winner) {
      if (winner)
        this.showWinnerDialog = true
    }
  },

  methods: {
    ...mapMutations(["terminaPartita"]),
    evidenziaContinente(id) {
      this.$refs.board.evidenziaStatiContinente(id)
    },
    onNodeSelected({id}) {
      this.$refs.azioniGiocatore.onNodeSelected({id})
    }
  }
}
</script>
