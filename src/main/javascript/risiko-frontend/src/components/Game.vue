<template>
  <v-container fluid>
    <v-row>
      <!-- Barra con le informazioni generali sulla partita -->
      <v-col cols="3" class="white d-flex flex-column">
        <game-info @evidenziaContinente="evidenziaContinente"/>
      </v-col>

      <!-- Mappa -->
      <v-col cols="6">
        <Board ref="board" @nodeSelected="onNodeSelected"/>
      </v-col>

      <!-- Azioni del giocatore -->
      <v-col cols="3" class="white">
        <azioni-giocatore ref="azioniGiocatore"/>
      </v-col>
    </v-row>

    <v-dialog max-width="700px" v-model="showWinnerDialog" persistent>
      <v-card v-if="winner">
        <v-card-title>Vittoria!</v-card-title>
        <v-card-text>{{ winner.nome }} ha vinto la partita, raggiungendo il suo obiettivo:</v-card-text>
        <v-card-text>{{ winner.obiettivo }}</v-card-text>

        <v-card-actions>
          <v-btn color="red" text @click="terminaPartita">Termina partita</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>

</template>

<script>
import Board from "@/components/Board";
import GameInfo from "@/components/GameInfo";
import AzioniGiocatore from "@/components/AzioniGiocatore";
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
