<template>
  <div>
    <div>
      <h3 class="text-h5 text-center my-6">Informazioni sulla partita</h3>

      <!-- Obiettivi -->
      <v-btn color="red" text @click="showObiettiviDialog = true" class="mb-5" width="wrap-content">
        Mostra obiettivi
      </v-btn>

      <!-- Giocatori -->
      <v-btn color="red" text @click="showGiocatoriDialog = true">Mostra info giocatori</v-btn>

      <!-- Continenti -->
      <v-list class="lista-continenti">
        <v-subheader>CONTINENTI</v-subheader>
        <v-list-item-group v-model="selectedContinente" color="red" >
          <v-list-item v-for="c in continenti" :key="c.id" two-line>
            <v-list-item-content>
              <v-list-item-title>{{ c.nome }}</v-list-item-title>
              <v-list-item-subtitle>{{ c.armateBonus }} armate bonus</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </div>

    <v-dialog v-model="showObiettiviDialog" max-width="700px">
      <obiettivi-dialog/>
    </v-dialog>

    <v-dialog v-model="showGiocatoriDialog" max-width="700px">
      <giocatori-dialog />
    </v-dialog>
  </div>
</template>

<script>

import ObiettiviDialog from "@/components/ObiettiviDialog";
import GiocatoriDialog from "@/components/GiocatoriDialog";

export default {
  name: "Info",
  components: {GiocatoriDialog, ObiettiviDialog },
  data() { return {
    showObiettiviDialog: true,
    selectedContinente: null,
    showGiocatoriDialog: false
  }},

  watch: {
    selectedContinente(id) {
      this.$emit("evidenziaContinente", id + 1)
    }
  },

  computed: {
    continenti() {
      return this.$store.getters.getMappaGioco.continenti
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