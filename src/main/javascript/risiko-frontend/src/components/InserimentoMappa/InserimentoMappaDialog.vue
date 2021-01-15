<template>
  <v-card>
    <v-app-bar color="primary" dark width="100%">
      <v-btn icon @click="$emit('close')">
        <v-icon>mdi-close</v-icon>
      </v-btn>

      <v-app-bar-title id="title">
        INSERIMENTO NUOVA MAPPA
      </v-app-bar-title>

      <v-spacer/>

      <v-btn :disabled="!valida" text @click="salvaMappa">SALVA</v-btn>
    </v-app-bar>

    <v-card-text class="black--text mt-3">
      <v-row>
        <v-col cols="5">
          <h4 class="text-h6">Informazioni Generali</h4>
          <v-row>
            <v-col cols="4" class="pb-0">
              <v-text-field v-model="mappaInCostruzione.nome" label="Nome mappa"/>
            </v-col>
            <v-col cols="8" class="pb-0">
              <v-text-field v-model="mappaInCostruzione.descrizione" label="Descrizione mappa"/>
            </v-col>
            <v-col cols="6" class="py-0">
              <v-select v-model="mappaInCostruzione.numMinGiocatori" label="Numero minimo giocatori"
                :items="[2, 3, 4, 5, 6, 7, 8]"/>
            </v-col>
            <v-col cols="6" class="py-0">
              <v-select v-model="mappaInCostruzione.numMaxGiocatori" label="Numero massimo giocatori"
                        :items="numMaxGiocatoriItems"
              />
            </v-col>
          </v-row>

          <continenti-table />
          <stati-table />
          <confini-table />
        </v-col>
        <v-col cols="7" class="d-flex flex-column align-center">
          <h5 class="text-h5 my-3">Anteprima mappa</h5>
          <anteprima-mappa/>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script>
import AnteprimaMappa from "@/components/InserimentoMappa/AnteprimaMappa";
import {mapActions, mapGetters} from "vuex";
import ContinentiTable from "@/components/InserimentoMappa/ContinentiTable";
import StatiTable from "@/components/InserimentoMappa/StatiTable";
import ConfiniTable from "@/components/InserimentoMappa/ConfiniTable";

export default {
  name: "InserimentoMappaDialog",
  components: {ConfiniTable, StatiTable, ContinentiTable, AnteprimaMappa},
  computed: {
    ...mapGetters(["mappaInCostruzione"]),
    valida() {
      return this.mappaInCostruzione.isValid()
    },
    numMaxGiocatoriItems() {
      let ris = []
      for (let i = this.mappaInCostruzione.numMinGiocatori; i <= 8; i++)
        ris.push(i)
      return ris
    }
  },
  methods: {
    ...mapActions(["inserisciMappa"]),
    async salvaMappa() {
      await this.inserisciMappa(this.mappaInCostruzione.asHierarchy())
      this.$emit("close")
    }
  }
}
</script>

<style scoped>
#title {
  width: fit-content !important;
}
</style>