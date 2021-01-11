<template>
  <div>
    <v-subheader>SPOSTAMENTO STRATEGICO</v-subheader>
    <div v-if="getTurno.fase !== 'spostamento'">
      <v-btn color="red" text @click="iniziaSpostamento" v-if="!spostamentoInCorso">INIZIA SPOSTAMENTO</v-btn>

      <div v-else>
        <v-btn color="red" text @click="chiudi">Annulla</v-btn>

        <span class="d-block text-caption">Seleziona sulla mappa lo stato di partenza</span>
        <span class="d-block">Stato di partenza: {{statoPartenza ? statoPartenza.nome : ""}}</span>

        <span class="d-block text-caption">Seleziona sulla mappa lo stato di arrivo</span>
        <span class="d-block">Stato di arrivo: {{statoArrivo ? statoArrivo.nome : ""}}</span>

        <v-select label="Armate da spostare"
                  v-model="armate"
                  :items="armateSpostabili"
                  :disabled="!statoPartenza" />

        <v-btn color="red" text :disabled="!statoPartenza || !statoArrivo || !armate" @click="confermaSpostamento">
          OK
        </v-btn>
      </div>

    </div>
    <span class="d-block text-caption">Hai già effettuato uno spostamento in questo turno</span>
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
    ...mapGetters(["getTurno", "getActivePlayer", "spostamentoInCorso", "getMappaGioco"]),
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
        giocatore: this.getActivePlayer
      }
      await this.spostamento(spostamentoData)
      this.chiudi()
    },
    onNodeSelected({ id }) {
      if (!this.statoPartenza) {
        let stato = utils.trovaStatoId(this.getMappaGioco, id)
        if (stato.proprietario === this.getActivePlayer && stato.armate > 1) {
          this.statoPartenza = stato
        }
      } else if (!this.statoArrivo) {
        let stato = utils.trovaStatoId(this.getMappaGioco, id)
        if (stato.proprietario === this.getActivePlayer && utils.confinanti(stato, this.statoPartenza)) {
          this.statoArrivo = stato
        }
      }
    }
  }
}
</script>

<style scoped>

</style>