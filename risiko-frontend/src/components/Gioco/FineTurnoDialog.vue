<template>
  <v-dialog v-model="showDialog" max-width="700px">
    <v-card>
      <v-app-bar class="d-flex align-center" color="primary" dark>
        <v-app-bar-title>
          FINE TURNO
        </v-app-bar-title>
      </v-app-bar>

      <v-card-text class="black--text text-body-1 mt-5">
        Hai terminato il tuo turno, ora spetta al giocatore {{ giocatoreAttivo }} a giocare.
      </v-card-text>

      <v-card-text v-if="turno.giocatorePrecedentePescato" class="black--text text-body-1">
        Siccome hai conquistato uno stato in questo turno, peschi una carta territorio che potrai vedere all'inizio
        del tuo prossimo turno nella sezione "Giocatori".
      </v-card-text>

      <v-card-actions>
        <v-spacer/>
        <v-btn color="primary" text @click="showDialog = false">OK</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import {mapGetters} from "vuex";

export default {
  name: "FineTurnoDialog",
  data() {
    return {
      showDialog: false
    }
  },
  computed: {
    ...mapGetters(["giocatoreAttivo", "turno"])
  },
  methods: {
    show() {
      this.showDialog = true
    }
  }
};
</script>
