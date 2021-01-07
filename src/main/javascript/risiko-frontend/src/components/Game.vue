<template>
  <v-container fluid>
    <v-row>
      <v-col cols="3" class="white d-flex flex-column">
        <h3 class="text-h5 text-center">Informazioni sulla partita</h3>

        <v-btn color="red" dark @click="showObiettiviDialog=true" class="mb-5" width="wrap-content">
          Mostra obiettivi
        </v-btn>
      </v-col>
      <v-col cols="6">
        <Board @nodeSelected="onNodeSelected"/>
      </v-col>

      <v-col cols="3" class="white">
        <h3 class="text-h5 text-center">Azioni</h3>

        <gestore-rinforzi ref="gestoreRinforzi" v-if="fasePreparazione" class="mt-5" />

      </v-col>
    </v-row>

    <v-dialog v-model="showObiettiviDialog" max-width="700px">
      <obiettivi-dialog/>
    </v-dialog>
  </v-container>

</template>

<script>
import Board from "@/components/Board";
import ObiettiviDialog from "@/components/ObiettiviDialog";
// import utils from "@/store/utils";
import GestoreRinforzi from "@/components/GestoreRinforzi";
export default {
  name: "Game",
  components: {GestoreRinforzi, ObiettiviDialog, Board},
  data() {
    return {
      showObiettiviDialog: true,
      // rinforzi: []
    }
  },

  computed: {
    fasePreparazione() {
      return this.$store.getters.getFasePreparazione;
    },
  },

  methods: {
    onNodeSelected({ id }) {
      if (this.fasePreparazione) {
        this.$refs.gestoreRinforzi.onNodeSelected({ id })
      }
    },
  }
}
</script>
