<template>
  <div>
    <v-banner color="primary" dark class="text-h5 text-center mb-5">Informazioni sulla partita</v-banner>

    <!-- Obiettivi -->
    <v-row>
      <v-col class="d-flex align-center flex-column" cols="6">
        <span class="text-subtitle-2">OBIETTIVI</span>
        <v-btn color="primary" rounded x-large @click="showObiettiviDialog = true" class="mb-5" width="wrap-content">
          <v-icon x-large >mdi-bullseye-arrow</v-icon>
        </v-btn>
      </v-col>

      <v-col class="d-flex align-center flex-column" cols="6">
        <!-- Giocatori -->
        <span class="text-subtitle-2">GIOCATORI</span>
        <v-btn color="primary" rounded x-large @click="showGiocatoriDialog = true">
          <v-icon x-large>mdi-account-multiple</v-icon>
        </v-btn>

      </v-col>
    </v-row>


    <!-- Continenti -->
    <v-subheader class="black--text">CONTINENTI</v-subheader>
    <span class="d-block text-body-2 ml-5">Clicca un continente per evidenziarlo sulla mappa</span>
    <v-list class="lista-continenti">
      <v-list-item-group v-model="selectedContinente" color="primary" >
        <v-list-item v-for="c in continenti" :key="c.id">
            <v-list-item-title>{{ c.nome }}</v-list-item-title>
            <v-list-item-icon>
              <v-icon class="mx-3">mdi-tank</v-icon>
              {{c.armateBonus}}
            </v-list-item-icon>
        </v-list-item>
      </v-list-item-group>
    </v-list>

    <!-- Tris carte territorio -->
    <v-subheader class="black--text">LEGENDA TRIS CARTE TERRITORIO</v-subheader>
    <v-container class="px-6 ma-0">
      <v-row v-for="tris in legenda" :key="tris.tris" class="my-1">
        <span class="text-subtitle-1">{{tris.tris}}</span>
        <v-spacer/>
        <span class="text-subtitle-1">{{tris.armate}}</span>
        <v-icon class="ml-3">mdi-tank</v-icon>
      </v-row>
    </v-container>

    <v-dialog v-model="showObiettiviDialog" max-width="700px">
      <obiettivi-dialog/>
    </v-dialog>

    <v-dialog v-model="showGiocatoriDialog" max-width="700px">
      <giocatori-dialog />
    </v-dialog>
  </div>
</template>

<script>

import ObiettiviDialog from "@/components/Gioco/ObiettiviDialog";
import GiocatoriDialog from "@/components/Gioco/GiocatoriDialog";
import {mapGetters} from "vuex";

export default {
  name: "Info",
  components: {GiocatoriDialog, ObiettiviDialog },
  data() { return {
    showObiettiviDialog: true,
    selectedContinente: null,
    showGiocatoriDialog: false,
    legenda: [
      {
        tris: "3 cannoni",
        armate: 4
      },
      {
        tris: "3 fanti",
        armate: 6
      },
      {
        tris: "3 cavalieri",
        armate: 8
      },
      {
        tris: "Un cannone, un fante, un cavaliere",
        armate: 10
      },
      {
        tris: "Due figure uguali e un jolly",
        armate: 12
      },
    ]
  }},

  watch: {
    selectedContinente(id) {
      this.$emit("evidenziaContinente", id + 1)
    }
  },

  computed: {
    ...mapGetters(["mappaGioco"]),
    continenti() {
      return this.mappaGioco.continenti
    }
  }
}
</script>

<style scoped>
 .lista-continenti {
   max-height: 30rem;
   overflow: auto;
 }
</style>