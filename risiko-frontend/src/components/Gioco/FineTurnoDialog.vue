<template>
  <v-card>
    <v-app-bar color="primary" dark class="d-flex align-center">
      <v-app-bar-title>
        FINE TURNO
      </v-app-bar-title>
    </v-app-bar>

    <v-card-text class="black--text text-body-1 mt-5">
      Hai terminato il tuo turno, ora spetta al giocatore {{ giocatoreAttivo }} a giocare.
    </v-card-text>

    <v-card-text class="black--text text-body-1" v-if="turno.giocatorePrecedentePescato">
      Siccome hai conquistato uno stato in questo turno, peschi una carta territorio che potrai vedere all'inizio
      del tuo prossimo turno nella sezione "Giocatori".
    </v-card-text>

    <v-card-actions>
      <v-spacer/>
      <v-btn color="primary" text @click="$emit('close')">OK</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import { mapGetters } from "vuex";

export default {
    name: "FineTurnoDialog",
    computed: {
        ...mapGetters(["giocatoreAttivo", "turno"])
    }
};
</script>

<style scoped>

</style>
