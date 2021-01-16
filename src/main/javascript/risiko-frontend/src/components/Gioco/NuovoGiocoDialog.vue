<template>
    <v-card>
        <v-app-bar color="primary" dark>
          <v-icon class="mr-3" @click="$emit('close')">mdi-close</v-icon>
          <v-app-bar-title>Nuovo gioco</v-app-bar-title>
          <v-spacer/>
          <v-btn text :disabled="!formValid || !coloriValidi" @click="nuovoGioco">
            Gioca
          </v-btn>
        </v-app-bar>
      
      <v-card-text class="pa-12">
        <v-form class="pt-5" v-model="formValid">
          <v-row>
            <v-col cols="6">
              <v-select
                  v-model="selected"
                  :items="mappe"
                  item-value="id"
                  item-text="nome"
                  return-object
                  label="Scegli la mappa con cui giocare"
                  :rule="[v => !!v]"
              />
            </v-col>

            <v-col cols="6">
              <v-checkbox v-model="unicoObiettivo" color="primary"
                          label="Tutti i giocatori hanno come obiettivo quello di conquistare lo stesso numero di territori"
                          />
            </v-col>

            <v-col cols="12">
              <v-alert type="info" color="primary" dark v-if="selected">
                {{selected.descrizione}}
              </v-alert>
            </v-col>

            <v-col cols="6">
              <v-combobox multiple small-chips deletable-chips color="primary"
                          :disabled="!selected"
                          :items="giocatoriDefault"
                          v-model="elencoGiocatori"
                          :rules="[validaGiocatori]"
                          :label="'Scrivi i nomi dei giocatori' + (this.selected ? ' (da ' + selected.numMinGiocatori + ' a '
                        + selected.numMaxGiocatori + ')' : '' )"
              />
            </v-col>

            <v-col cols="6">
              <v-select label="Seleziona la modalità di gioco"
                        :items="modeItems"
                        v-model="selectedMode"
                        :rules="[ v => !!v ]"/>
            </v-col>
          </v-row>

          <h6 class="text-h6 black--text">Colori giocatori</h6>
          <span class="text-caption red--text" v-if="!coloriValidi">
            Devi scegliere un colore per ogni giocatore e non possono esserci due giocatori con lo stesso colore
          </span>
          <v-row v-for="nomeGiocatore in elencoGiocatori" :key="nomeGiocatore" class="align-center">
            <v-col cols="1">
              <span class="text-body-1 black--text text-align-end">{{nomeGiocatore}}</span>
            </v-col>
            <v-col cols="3">
              <v-select label="Scegli un colore"
                        :items="groups"
                        :ref="'color_' + nomeGiocatore"
                        return-object
                        v-model="colors[nomeGiocatore]"
                        @change="checkColoriValidi"
              >
                <template v-slot:item="{ item, on, attrs }">
                  <v-list-item v-on="on" v-bind="{attrs}">
                    <v-list-item-avatar :color="item.background"/>
                    {{item.nome}}
                  </v-list-item>
                </template>

                <template v-slot:selection="{ item }">
                  <v-list-item>
                    <v-list-item-avatar :color="item.background"/>
                    {{item.nome}}
                  </v-list-item>
                </template>
              </v-select>
            </v-col>
          </v-row>


        </v-form>
      </v-card-text>
    </v-card>
</template>

<script>

import {mapActions, mapGetters} from "vuex";
import networkUtils from "@/store/networkUtils";

export default {
name: "NuovoGiocoDialog",
  data() {
    return {
      show: true,
      selected: null,
      giocatoriDefault: [  ],
      elencoGiocatori: [],
      modeItems: [ "COMPLETA", "RIDOTTA", "VELOCE" ],
      selectedMode: "",
      formValid: false,
      groups: networkUtils.groups,
      coloriValidi: false,
      colors: {},
      unicoObiettivo: false
    }
  },
  computed: {
    ...mapGetters(["mappe"])
  },
  watch: {
    elencoGiocatori() {
      this.checkColoriValidi()
    }
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
        mod: this.selectedMode,
        colors: this.colors,
        unicoObiettivo: this.unicoObiettivo
      }
      await this.startGame(config)
      this.$emit("gameStarted")
      this.elencoGiocatori = []
      this.selected = null
      this.selectedMode = ""
      this.unicoObiettivo = false
      this.colors = {}
    },
    checkColoriValidi() {
      for (let nomeGiocatore of this.elencoGiocatori) {
        if (!this.colors[nomeGiocatore]) {
          this.coloriValidi = false
          return
        }
      }

      for (let nomeGiocatore of this.elencoGiocatori) {
        let sameColorPlayer = this.elencoGiocatori.find(giocatore =>
            giocatore !== nomeGiocatore && this.colors[nomeGiocatore].background === this.colors[giocatore].background
        )
        if (sameColorPlayer) {
          this.coloriValidi = false
          return
        }
      }

      this.coloriValidi = true
    }
  }
}
</script>

<style scoped>

</style>