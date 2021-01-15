<template>
  <div>
    <h4 class="text-h6 ma-4">
      RINFORZI
    </h4>

    <v-row class="align-center mb-2 ml-3">
      <v-icon large class="mr-3">mdi-tank</v-icon>
      <span class="text-subtitle-1">{{totaleRinforzi}}/{{rinforziConsentiti}}</span>
    </v-row>


    <div v-if="fasePreparazione || rinforziConsentiti > 0" class="ml-5">
      <div v-if="!fasePreparazione">
        <span class="text-subtitle-2 d-block mb-2">Devi piazzare {{rinforziConsentiti}} armate: </span>
        <span class="text-subtitle-2 d-block">{{ turno.armateStati }} per gli stati che possiedi</span>
        <span class="text-subtitle-2 d-block">{{ turno.armateContinenti }} per i continenti conquistati</span>
        <span class="text-subtitle-2 d-block">{{ turno.armateTris }} per il tris eventualmente giocato</span>
      </div>
      <span class="text-subtitle-2 d-block mb-2" v-else>Devi piazzare {{rinforziConsentiti}} armate per la fase di preparazione
       ({{armateDisponibili}} rimanenti in tutto)</span>

      <v-list class="rinforzi-list">
        <v-list-item v-for="rinf in rinforzi" :key="rinf.id">
          <v-row>
            <v-badge :content="rinf.quantity" color="primary" inline>
              <v-list-item-title>{{ rinf.nome }}</v-list-item-title>
            </v-badge>

            <v-spacer/>
            <v-list-item-action>
              <v-row class="mr-5">
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
    </div>

    <span class="d-block text-subtitle-2 mb-2" v-if="!fasePreparazione && turno.tris && rinforziConsentiti === 0">
      Hai già effettuato rinforzi in questo turno
    </span>

    <v-row>
      <v-spacer />
      <v-tooltip top>
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="primary" icon large class="mx-3" v-on="on" v-bind="attrs"
                 :disabled="rinforziConsentiti !== totaleRinforzi || rinforziConsentiti === 0"
                 @click="confermaInviaRinforzi">
            <v-icon>mdi-check</v-icon>
          </v-btn>
        </template>
        <span>Posiziona armate</span>
      </v-tooltip>

      <v-tooltip top>
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="primary" icon large class="mx-3" v-on="on" v-bind="attrs"
                 @click="showTrisDialog = true"
                 v-if="!fasePreparazione && !turno.tris">
            <v-icon>mdi-cards</v-icon>
          </v-btn>
        </template>
        <span>Gioca tris di carte territorio</span>
      </v-tooltip>

    </v-row>

    <v-dialog v-model="showTrisDialog" max-width="700px">
      <tris-dialog @close="showTrisDialog = false"/>
    </v-dialog>
  </div>
</template>

<script>
import utils from "@/store/utils";
import TrisDialog from "@/components/Gioco/TrisDialog";
import {mapActions, mapGetters} from "vuex";

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
    ...mapGetters(["fasePreparazione", "giocatoreAttivo", "mappaGioco", "armateDisponibili", "turno"]),
    totaleRinforzi() {
      let ris = 0
      this.rinforzi.forEach(r => { ris = ris + r.quantity })
      return ris
    },
    rinforziConsentiti() {
      if (this.fasePreparazione)
        return Math.min(3, this.armateDisponibili)
      return this.$store.getters.armateDisponibili
    }
  },
  methods: {
    ...mapActions(["inviaRinforzi"]),
    onNodeSelected({ id }) {
      let stato = utils.trovaStatoId(this.mappaGioco, id)
      if (stato.proprietario === this.giocatoreAttivo && this.totaleRinforzi < this.rinforziConsentiti) {
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
    async confermaInviaRinforzi() {
      await this.inviaRinforzi(this.rinforzi)
      this.rinforzi = []
    }
  }
}
</script>

<style scoped>
  .rinforzi-list {
    overflow-y: auto;
    max-height: 15rem;
  }
</style>