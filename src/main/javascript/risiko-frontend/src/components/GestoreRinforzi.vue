<template>
  <div v-if="fasePreparazione">
    <h4>
      Rinforzi di {{ activePlayer }} ({{ rinforziConsentiti }} consentiti)
    </h4>

    <span>Clicca sulla mappa i territori da rinforzare</span>

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
    }
  },
  methods: {
    onNodeSelected({ id }) {
      if (this.fasePreparazione) {
        let stato = utils.trovaStatoId(this.mappa, id)
        if (stato.proprietario === this.activePlayer && this.totaleRinforzi < this.rinforziConsentiti) {
          let rinforzoIndex = this.rinforzi.findIndex(rinf => rinf.id === id)
          if (rinforzoIndex === -1) {
            this.rinforzi.push({ id, nome: stato.nome, quantity: 0 })
            rinforzoIndex = this.rinforzi.length - 1
          }
          this.rinforzi[rinforzoIndex].quantity++
        }
      }
    },
    diminuisciRinforzo(rinf) {
      rinf.quantity--
      if (rinf.quantity === 0) {
        let index = this.rinforzi.indexOf(el => el.id === rinf.id)
        this.rinforzi.splice(index)
      }
    },
    async inviaRinforzi() {
      await this.$store.dispatch("inviaRinforzi", this.rinforzi)
      this.rinforzi = []
      await this.$store.dispatch("nuovoTurno")
    }
  }
}
</script>

<style scoped>

</style>