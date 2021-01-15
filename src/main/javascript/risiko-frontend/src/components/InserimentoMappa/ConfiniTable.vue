<template>
  <div class="d-flex flex-column align-center">
    <v-data-table
        :headers="headers"
        :items="confini"
        :items-per-page="5"
    >
      <template v-slot:top>
        <v-row class="my-4">
          <h4 class="text-h6">Confini</h4>
          <v-spacer/>
          <v-btn color="primary" @click="showDialog = true">Inserisci</v-btn>
        </v-row>
      </template>

      <template v-slot:item.actions="{ item }">
        <v-btn icon @click="mappaInCostruzione.removeConfine(item.from, item.to)">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </template>
    </v-data-table>

    <v-dialog max-width="700px" v-model="showDialog">
      <v-card>
        <v-card-title>Aggiungi nuovo confine</v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="6">
              <v-select label="Primo stato" :items="mappaInCostruzione.stati.map(s => s.nome)"
                        v-model="nuovoConfine.nomeStato1"/>
            </v-col>
            <v-col cols="6">
              <v-select label="Secondo stato" :items="mappaInCostruzione.stati.map(s => s.nome)"
                        v-model="nuovoConfine.nomeStato2"/>

            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" text @click="showDialog = false">
            Annulla
          </v-btn>
          <v-btn color="primary" text @click="aggiungiConfine" :disabled="!confineValido">
            OK
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import {mapGetters} from "vuex";

export default {
  name: "ConfiniTable",
  data() {
    return {
      headers: [
        {
          text: "Primo stato",
          value: "from"
        },
        {
          text: "Secondo stato",
          value: "to"
        },
        {
          text: "Operazioni",
          value: "actions"
        }
      ],
      showDialog: false,
      nuovoConfine: { nomeStato1: "", nomeStato2: "" }
    }
  },
  computed: {
    ...mapGetters(["anteprimaNetwork", "mappaInCostruzione"]),
    confini() {
      return this.anteprimaNetwork.edges
    },
    confineValido() {
      return !!this.nuovoConfine.nomeStato1 && !!this.nuovoConfine.nomeStato2 &&
          this.nuovoConfine.nomeStato1 !== this.nuovoConfine.nomeStato2 &&
          !this.mappaInCostruzione.confinanti(this.nuovoConfine.nomeStato1, this.nuovoConfine.nomeStato2)
    }
  },
  methods: {
    aggiungiConfine() {
      this.mappaInCostruzione.addConfine(this.nuovoConfine.nomeStato1, this.nuovoConfine.nomeStato2)
      this.nuovoConfine = { nomeStato1: "", nomeStato2: "" }
      this.showDialog = false
    }
  }
}
</script>

<style scoped>

</style>