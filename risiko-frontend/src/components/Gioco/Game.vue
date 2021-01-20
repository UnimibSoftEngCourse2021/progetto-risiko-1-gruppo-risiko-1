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

    <!-- Vittoria dialog -->
    <vittoria-dialog />
  </v-container>

</template>

<script>
import Board from "@/components/Gioco/Board";
import GameInfo from "@/components/Gioco/GameInfo";
import AzioniGiocatore from "@/components/Gioco/AzioniGiocatore";
import VittoriaDialog from "@/components/Gioco/VittoriaDialog";

export default {
    name: "Game",
    components: {VittoriaDialog, AzioniGiocatore, Board, GameInfo },

    methods: {
        evidenziaContinente(id) {
            this.$refs.board.evidenziaStatiContinente(id);
        },
        onNodeSelected({ id }) {
            this.$refs.azioniGiocatore.onNodeSelected({ id });
        }
    }
};
</script>
