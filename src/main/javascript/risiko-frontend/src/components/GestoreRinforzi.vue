<template>
  <div>
    <v-subheader>RINFORZI</v-subheader>

    <div v-if="fasePreparazione || rinforziConsentiti > 0">
      <v-list v-if="!fasePreparazione">
        <span class="text-caption d-block">Devi piazzare {{rinforziConsentiti}} armate: </span>
        <v-list-item>
          <v-list-item-content>{{turno.armateStati}} per gli stati che possiedi</v-list-item-content>
        </v-list-item>
        <v-list-item>
          <v-list-item-content>{{turno.armateContinenti}} per i continenti conquistati</v-list-item-content>
        </v-list-item>
        <v-list-item>
          <v-list-item-content>{{turno.armateTris}} per il tris eventualmente giocato</v-list-item-content>
        </v-list-item>
      </v-list>
      <span class="text-caption d-block" v-else>Devi piazzare {{rinforziConsentiti}} armate per la fase di preparazione</span>

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
      <v-btn text color="red" :disabled="rinforziConsentiti !== totaleRinforzi"
             @click="inviaRinforzi">Piazza rinforzi
      </v-btn>
    </div>

    <span class="d-block text-caption" v-if="!fasePreparazione && turno.tris && rinforziConsentiti === 0">
      Hai già effettuato rinforzi in questo turno
    </span>

    <v-btn color="red"
           text @click="showTrisDialog = true"
           v-if="!fasePreparazione && !turno.tris">Gioca tris</v-btn>

    <v-dialog v-model="showTrisDialog" max-width="700px">
      <tris-dialog @close="showTrisDialog = false"/>
    </v-dialog>
  </div>
</template>

<script>
import utils from "@/store/utils";
import TrisDialog from "@/components/TrisDialog";

export default {
  name: "GestoreRinforzi",
  components: {TrisDialog},
  data() {
    return {
      rinforzi: [],
      showTrisDialog: false
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