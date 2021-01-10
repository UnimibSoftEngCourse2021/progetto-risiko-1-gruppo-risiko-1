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
  </v-container>

</template>

<script>
import Board from "@/components/Board";
import GameInfo from "@/components/GameInfo";
import AzioniGiocatore from "@/components/AzioniGiocatore";

export default {
  name: "Game",
  components: {AzioniGiocatore, Board, GameInfo},
  data() {
    return {
      showObiettiviDialog: true,
    }
  },

  computed: {
    fasePreparazione() {
      return this.$store.getters.getFasePreparazione;
    },
  },

  methods: {
    evidenziaContinente(id) {
      this.$refs.board.evidenziaStatiContinente(id)
    },
    onNodeSelected({id}) {
      this.$refs.azioniGiocatore.onNodeSelected({id})
    }
  }
}
</script>
