<template>
  <v-dialog v-model="showDialog" fullscreen hide-overlay transition="dialog-bottom-transition">
    <v-card>
      <v-app-bar color="primary" dark>
        <v-icon class="mr-3" @click="showDialog = false">mdi-close</v-icon>
        <v-app-bar-title>Nuovo gioco</v-app-bar-title>
        <v-spacer/>
        <v-btn :disabled="!formValid || !coloriValidi" text @click="nuovoGioco">
          Gioca
        </v-btn>
      </v-app-bar>

      <v-card-text class="pa-12">
        <v-form v-model="formValid" class="pt-5">
          <v-row>
            <v-col cols="6">
              <v-select
                  v-model="selected"
                  :items="mappe"
                  :rule="[v => !!v]"
                  item-text="nome"
                  item-value="id"
                  label="Scegli la mappa con cui giocare"
                  return-object
              />
            </v-col>

            <v-col cols="6">
              <v-checkbox v-model="unicoObiettivo" color="primary"
                          label="Tutti i giocatori hanno come obiettivo quello di conquistare lo stesso numero di territori"
              />
            </v-col>

            <v-col cols="12">
              <v-alert v-if="selected" color="primary" dark type="info">
                {{ selected.descrizione }}
              </v-alert>
            </v-col>

            <v-col cols="6">
              <v-combobox v-model="elencoGiocatori"
                          :disabled="!selected"
                          :items="giocatoriDefault"
                          :label="'Scrivi i nomi dei giocatori' + (this.selected ? ' (da ' + selected.numMinGiocatori +
                                  ' a ' + selected.numMaxGiocatori + ')' : '' )"
                          :rules="[validaGiocatori]"
                          color="primary"
                          deletable-chips
                          multiple
                          small-chips
              />
            </v-col>

            <v-col cols="6">
              <v-select v-model="selectedMode"
                        :items="modeItems"
                        :rules="[ v => !!v ]"
                        label="Seleziona la modalità di gioco"/>
            </v-col>
          </v-row>

          <h6 class="text-h6 black--text">Colori giocatori</h6>
          <span v-if="!coloriValidi" class="text-caption red--text">
            Devi scegliere un colore per ogni giocatore e non possono esserci due giocatori con lo stesso colore
          </span>
          <v-row v-for="nomeGiocatore in elencoGiocatori" :key="nomeGiocatore" class="align-center">
            <v-col cols="1">
              <span class="text-body-1 black--text text-align-end">{{ nomeGiocatore }}</span>
            </v-col>
            <v-col cols="3">
              <v-select :ref="'color_' + nomeGiocatore"
                        v-model="colors[nomeGiocatore]"
                        :items="groups"
                        label="Scegli un colore"
                        return-object
                        @change="checkColoriValidi"
              >
                <template v-slot:item="{ item, on, attrs }">
                  <v-list-item v-bind="{attrs}" v-on="on">
                    <v-list-item-avatar :color="item.background"/>
                    {{ item.nome }}
                  </v-list-item>
                </template>

                <template v-slot:selection="{ item }">
                  <v-list-item>
                    <v-list-item-avatar :color="item.background"/>
                    {{ item.nome }}
                  </v-list-item>
                </template>
              </v-select>
            </v-col>
          </v-row>


        </v-form>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>

import {mapActions, mapGetters} from "vuex";
import networkUtils from "@/utils/networkUtils";

export default {
  name: "NuovoGiocoDialog",
  data() {
    return {
      showDialog: false,
      selected: null,
      giocatoriDefault: [],
      elencoGiocatori: [],
      modeItems: ["COMPLETA", "RIDOTTA", "VELOCE"],
      selectedMode: "",
      formValid: false,
      groups: networkUtils.groups,
      coloriValidi: false,
      colors: {},
      unicoObiettivo: false
    };
  },
  computed: {
    ...mapGetters(["mappe"])
  },
  watch: {
    elencoGiocatori() {
      this.checkColoriValidi();
    }
  },
  methods: {
    ...mapActions(["startGame"]),
    show() {
      this.showDialog = true
    },
    validaGiocatori(elenco) {
      if (!this.selected) {
        return false;
      }

      return !(elenco.length < this.selected.numMinGiocatori || elenco.length > this.selected.numMaxGiocatori);
    },
    async nuovoGioco() {
      const config = {
        giocatori: this.elencoGiocatori,
        mappaId: this.selected.id,
        mod: this.selectedMode,
        colors: this.colors,
        unicoObiettivo: this.unicoObiettivo
      };

      await this.startGame(config);
      this.showDialog = false
      this.elencoGiocatori = [];
      this.selected = null;
      this.selectedMode = "";
      this.unicoObiettivo = false;
      this.colors = {};
      this.$emit("gameStarted");
    },
    checkColoriValidi() {
      for (const nomeGiocatore of this.elencoGiocatori) {
        if (!this.colors[nomeGiocatore]) {
          this.coloriValidi = false;
          return;
        }
      }

      for (const nomeGiocatore of this.elencoGiocatori) {
        const sameColorPlayer = this.elencoGiocatori.find(giocatore =>
            giocatore !== nomeGiocatore && this.colors[nomeGiocatore].background === this.colors[giocatore].background);

        if (sameColorPlayer) {
          this.coloriValidi = false;
          return;
        }
      }

      this.coloriValidi = true;
    }
  }
};
</script>
