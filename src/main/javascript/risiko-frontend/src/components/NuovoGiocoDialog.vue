<template>
    <v-card>
        <v-app-bar color="primary" dark>
          <v-app-bar-title>Nuovo gioco</v-app-bar-title>
        </v-app-bar>

      <v-card-text>
        <v-form class="pt-5" v-model="formValid">
          <v-select
              v-model="selected"
              :items="mappe"
              item-value="id"
              item-text="nome"
              return-object
              label="Scegli la mappa con cui giocare"
              :rule="[v => !!v]"
          />

          <v-alert type="info" color="primary" dark v-if="selected">
            {{selected.descrizione}}
          </v-alert>

          <v-combobox multiple small-chips deletable-chips color="primary"
              :disabled="!selected"
              :items="giocatoriDefault"
              v-model="elencoGiocatori"
              :rules="[validaGiocatori]"
              :label="'Scrivi i nomi dei giocatori' + (this.selected ? ' (da ' + selected.numMinGiocatori + ' a '
                        + selected.numMaxGiocatori + ')' : '' )"
          />
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer/>
        <v-btn text color="red" @click="$emit('close')">
          Annulla
        </v-btn>
        <v-btn text color="red" :disabled="!formValid" @click="nuovoGioco">
          Gioca
        </v-btn>
      </v-card-actions>
    </v-card>
</template>

<script>

import {mapActions, mapGetters} from "vuex";

export default {
name: "NuovoGiocoDialog",
  data() {
    return {
      show: true,
      selected: null,
      giocatoriDefault: [  ],
      elencoGiocatori: [],
      formValid: false
    }
  },
  computed: {
    ...mapGetters(["mappe"])
  },
  methods: {
  ...mapActions(["startGame"]),
    validaGiocatori(elenco) {
      if (!this.selected)
        return false;

      return !(elenco.length < this.selected.numMinGiocatori || elenco.length > this.selected.numMaxGiocatori);
    },
    async nuovoGioco() {
      let config = {
        giocatori: this.elencoGiocatori,
        mappaId: this.selected.id,
        mod: "COMPLETA"
      }
      await this.startGame(config)
      this.$emit("gameStarted")
      this.elencoGiocatori = []
      this.selected = null
    }
  }
}
</script>

<style scoped>

</style>