<template>
  <div>
    <h4 class="text-h6 ma-4">
      SPOSTAMENTO STRATEGICO
    </h4>

    <div v-if="turno.fase !== 'spostamento'">
      <v-btn color="primary" block rounded @click="iniziaSpostamento" v-if="!spostamentoInCorso">INIZIA SPOSTAMENTO</v-btn>

      <div v-else>

        <span class="d-block text-body-2 mb-3">Seleziona sulla mappa lo stato di partenza e quello di arrivo</span>
        <span class="d-block text-subtitle-2">Stato di partenza: {{statoPartenza ? statoPartenza.nome : ""}}</span>
        <span class="d-block text-subtitle-2">Stato di arrivo: {{statoArrivo ? statoArrivo.nome : ""}}</span>

        <v-select label="Armate da spostare"
                  v-model="armate"
                  :items="armateSpostabili"
                  :disabled="!statoPartenza" />

        <v-row>
          <v-spacer />

          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" icon @click="chiudi" v-on="on" v-bind="attrs">
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </template>
            Annulla
          </v-tooltip>

          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }" v-on="on" v-bind="attrs">
              <v-btn color="primary" icon :disabled="!statoPartenza || !statoArrivo || !armate" @click="confermaSpostamento">
                <v-icon>mdi-check</v-icon>
              </v-btn>
            </template>
            Conferma spostamento
          </v-tooltip>
        </v-row>
      </div>

    </div>
    <span v-else class="d-block text-body-2">Hai già effettuato uno spostamento in questo turno</span>
  </div>
</template>

<script>
import {mapGetters, mapMutations, mapActions} from "vuex";
import utils from "@/store/utils";

export default {
  name: "GestoreSpostamentoStrategico",
  data() {
    return {
      statoPartenza: null,
      statoArrivo: null,
      armate: null
    }
  },
  computed: {
    ...mapGetters(["turno", "giocatoreAttivo", "spostamentoInCorso", "mappaGioco"]),
    armateSpostabili() {
      let ris = []
      if (this.statoPartenza) {
        for (let i = 1; i < this.statoPartenza.armate; i++) {
          ris.push(i)
        }
      }
      return ris
    }
  },
  methods: {
    ...mapMutations(["setSpostamentoInCorso"]),
    ...mapActions(["spostamento"]),
    iniziaSpostamento() {
      this.setSpostamentoInCorso(true)
    },
    chiudi() {
      this.statoArrivo = null
      this.statoPartenza = null
      this.armate = null
      this.setSpostamentoInCorso(false)
    },
    async confermaSpostamento() {
      let spostamentoData = {
        statoPartenza: this.statoPartenza.id,
        statoArrivo: this.statoArrivo.id,
        armate: this.armate,
        giocatore: this.giocatoreAttivo
      }
      await this.spostamento(spostamentoData)
      this.chiudi()
    },
    onNodeSelected({ id }) {
      if (!this.statoPartenza) {
        let stato = utils.trovaStatoId(this.mappaGioco, id)
        if (stato.proprietario === this.giocatoreAttivo && stato.armate > 1) {
          this.statoPartenza = stato
        }
      } else if (!this.statoArrivo) {
        let stato = utils.trovaStatoId(this.mappaGioco, id)
        if (stato.proprietario === this.giocatoreAttivo && utils.confinanti(stato, this.statoPartenza)) {
          this.statoArrivo = stato
        }
      }
    }
  }
}
</script>

<style scoped>

</style>