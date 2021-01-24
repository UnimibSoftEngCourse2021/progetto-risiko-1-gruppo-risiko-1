<template>
  <v-container fluid>
    <v-row>
      <!-- Barra con le informazioni generali sulla partita -->
      <v-col class="white d-flex flex-column" cols="3">
        <game-info @evidenziaContinente="evidenziaContinente"/>
      </v-col>

      <!-- Mappa -->
      <v-col class="pa-1" cols="6">
        <Board ref="board" :onNodeSelected="onNodeSelected"/>
      </v-col>

      <!-- Azioni del giocatore -->
      <v-col class="white" cols="3">
        <azioni-giocatore ref="azioniGiocatore"/>
      </v-col>
    </v-row>

    <!-- Vittoria dialog -->
    <vittoria-dialog/>
  </v-container>

</template>

<script>
import Board from "@/components/Gioco/Board";
import GameInfo from "@/components/Gioco/GameInfo";
import AzioniGiocatore from "@/components/Gioco/AzioniGiocatore";
import VittoriaDialog from "@/components/Gioco/VittoriaDialog";

export default {
  name: "Game",
  components: {VittoriaDialog, AzioniGiocatore, Board, GameInfo},

  methods: {
    onNodeSelected({id}) {
      this.$refs.azioniGiocatore.onNodeSelected({id})
    },
    evidenziaContinente(id) {
      this.$refs.board.evidenziaStatiContinente(id)
    }
  }
};
</script>
