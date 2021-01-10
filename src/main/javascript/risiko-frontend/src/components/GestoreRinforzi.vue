<template>
  <div>
    <v-subheader>RINFORZI</v-subheader>
    <span class="text-caption">Devi piazzare {{rinforziConsentiti}} armate
      {{fasePreparazione ? "" : (" (" + turno.armateStati + " per bonus territori, " + turno.armateContinenti
          + " per bonus continenti)")}}. Clicca sulla mappa i territori da rinforzare</span>

    <v-list>
      <v-list-item v-for="rinf in rinforzi" :key="rinf.id">
        <v-row>
          <v-badge :content="rinf.quantity" color="red">
            <v-list-item-title>{{ rinf.nome }}</v-list-item-title>
          </v-badge>

          <v-spacer/>
          <v-list-item-action>
            <v-row>
              <v-btn icon :disabled="totaleRinforzi === rinforziConsentiti" @click="rinf.quantity++">
                <v-icon>mdi-plus</v-icon>
              </v-btn>
              <v-btn icon @click="diminuisciRinforzo(rinf)">
                <v-icon>mdi-minus</v-icon>
              </v-btn>
            </v-row>
          </v-list-item-action>
        </v-row>
      </v-list-item>
    </v-list>
    <v-btn text color="red" :disabled="rinforziConsentiti !== totaleRinforzi" @click="inviaRinforzi">Piazza rinforzi
    </v-btn>
  </div>
</template>

<script>
import utils from "@/store/utils";

export default {
  name: "GestoreRinforzi",
  data() {
    return {
      rinforzi: []
    }
  },
  computed: {
    fasePreparazione() {
      return this.$store.getters.getFasePreparazione;
    },
    activePlayer() {
      return this.$store.getters.getActivePlayer
    },
    mappa() {
      return this.$store.getters.getMappaGioco
    },
    totaleRinforzi() {
      let ris = 0
      this.rinforzi.forEach(r => { ris = ris + r.quantity })
      return ris
    },
    rinforziConsentiti() {
      if (this.fasePreparazione)
        return Math.min(3, this.$store.getters.getArmateDisponibili)
      return this.$store.getters.getArmateDisponibili
    },
    turno() {
      return this.$store.getters.getTurno
    }
  },
  methods: {
    onNodeSelected({ id }) {
      let stato = utils.trovaStatoId(this.mappa, id)
      if (stato.proprietario === this.activePlayer && this.totaleRinforzi < this.rinforziConsentiti) {
        let rinforzoIndex = this.rinforzi.findIndex(rinf => rinf.id === id)
        if (rinforzoIndex === -1) {
          this.rinforzi.push({ id, nome: stato.nome, quantity: 0 })
          rinforzoIndex = this.rinforzi.length - 1
        }
        this.rinforzi[rinforzoIndex].quantity++
      }
    },
    diminuisciRinforzo(rinf) {
      rinf.quantity--
      if (rinf.quantity <= 0) {
        let index = this.rinforzi.findIndex(el => {
          return el.id === rinf.id
        })
        this.rinforzi.splice(index, 1)
      }
    },
    async inviaRinforzi() {
      await this.$store.dispatch("inviaRinforzi", this.rinforzi)
      this.rinforzi = []
    }
  }
}
</script>

<style scoped>

</style>