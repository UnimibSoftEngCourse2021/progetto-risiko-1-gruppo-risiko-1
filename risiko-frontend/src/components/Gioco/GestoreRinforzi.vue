<template>
  <div>
    <h4 class="text-h6 ma-4">RINFORZI</h4>

    <!-- Contatore delle armate posizionate -->
    <v-row v-if="rinforziConsentiti" class="align-center mb-2 ml-3">
      <v-icon class="mr-3" large>mdi-tank</v-icon>
      <span class="text-subtitle-1">{{ totaleRinforzi }}/{{ rinforziConsentiti }}</span>
    </v-row>

    <div v-if="fasePreparazione || rinforziConsentiti" class="ml-5">
      <!-- Spiegazione delle armate da posizionare -->
      <div v-if="!fasePreparazione">
        <span class="text-subtitle-2 d-block mb-2">Devi piazzare {{ rinforziConsentiti }} armate: </span>
        <span class="text-subtitle-2 d-block">{{ turno.armateStati }} per gli stati che possiedi</span>
        <span class="text-subtitle-2 d-block">{{ turno.armateContinenti }} per i continenti conquistati</span>
        <span class="text-subtitle-2 d-block">{{ turno.armateTris }} per il tris eventualmente giocato</span>
      </div>
      <span v-else class="text-subtitle-2 d-block mb-2">Devi piazzare {{ rinforziConsentiti }} armate per la fase
        di preparazione ({{ armateDisponibili }} rimanenti in tutto)</span>

      <!-- Stati selezionati per il rinforzo con relativo n di armate-->
      <v-list class="rinforzi-list">
        <v-list-item v-for="(rinf, index) in rinforzi" :key="rinf.id">
          <v-row>
            <v-badge :content="rinf.quantity" color="primary" inline>
              <v-list-item-title>{{ rinf.nome }}</v-list-item-title>
            </v-badge>

            <v-spacer/>
            <v-list-item-action>
              <v-row class="mr-5">
                <v-btn :disabled="totaleRinforzi === rinforziConsentiti" icon @click="rinf.quantity++">
                  <v-icon>mdi-plus</v-icon>
                </v-btn>
                <v-btn icon @click="diminuisciRinforzo(rinf, index)">
                  <v-icon>mdi-minus</v-icon>
                </v-btn>
              </v-row>
            </v-list-item-action>
          </v-row>
        </v-list-item>
      </v-list>
    </div>

    <!-- Messaggio che annuncia che non si può più effettuare rinforzi -->
    <span v-if="!fasePreparazione && turno.tris && !rinforziConsentiti" class="d-block text-subtitle-2 mb-2">
      Hai già effettuato rinforzi in questo turno
    </span>

    <!-- Azioni disponibili -->
    <v-row>
      <v-spacer/>
      <!-- Conferma posizionamento truppe -->
      <v-tooltip top>
        <template v-slot:activator="{ on, attrs }">
          <v-btn v-bind="attrs" v-on="on"
                 :disabled="rinforziConsentiti !== totaleRinforzi || !rinforziConsentiti"
                 class="mx-3" color="primary" icon
                 large
                 @click="confermaInviaRinforzi">
            <v-icon>mdi-check</v-icon>
          </v-btn>
        </template>
        <span>Posiziona armate</span>
      </v-tooltip>

      <!-- Gioca tris di carte territorio -->
      <v-tooltip top>
        <template v-slot:activator="{ on, attrs }">
          <v-btn v-if="!fasePreparazione && !turno.tris" v-bind="attrs" v-on="on" class="mx-3" color="primary" icon
                 large
                 @click="$refs.trisDialog.show()">
            <v-icon>mdi-cards</v-icon>
          </v-btn>
        </template>
        <span>Gioca tris di carte territorio</span>
      </v-tooltip>
    </v-row>

    <tris-dialog ref="trisDialog"/>
  </div>
</template>

<script>
import TrisDialog from "@/components/Gioco/TrisDialog";
import {mapActions, mapGetters} from "vuex";

export default {
  name: "GestoreRinforzi",
  components: {TrisDialog},
  data() {
    return {
      rinforzi: []
    };
  },
  computed: {
    ...mapGetters(["fasePreparazione", "giocatoreAttivo", "mappaGioco", "armateDisponibili", "turno"]),
    totaleRinforzi() {
      return this.rinforzi.reduce((ris, rinf) => ris + rinf.quantity, 0);
    },
    rinforziConsentiti() {
      return this.fasePreparazione ?
          Math.min(3, this.armateDisponibili) :
          this.armateDisponibili;
    }
  },
  methods: {
    ...mapActions(["inviaRinforzi"]),
    onStatoSelezionato({id}) {
      const stato = this.mappaGioco.trovaStatoId(id);
      if (stato.proprietario === this.giocatoreAttivo && this.totaleRinforzi < this.rinforziConsentiti) {
        let rinforzoIndex = this.rinforzi.findIndex(rinf => rinf.id === id);

        if (rinforzoIndex === -1) {
          rinforzoIndex = this.rinforzi.push({id, nome: stato.nome, quantity: 0}) - 1;
        }

        this.rinforzi[rinforzoIndex].quantity++;
      }
    },
    diminuisciRinforzo(rinf, rinfIndex) {
      rinf.quantity--;
      if (rinf.quantity <= 0) {
        this.rinforzi.splice(rinfIndex, 1);
      }
    },
    async confermaInviaRinforzi() {
      await this.inviaRinforzi(this.rinforzi);
      this.rinforzi = [];
    }
  }
};
</script>

<style scoped>
.rinforzi-list {
  overflow-y: auto;
  max-height: 15rem;
}
</style>
