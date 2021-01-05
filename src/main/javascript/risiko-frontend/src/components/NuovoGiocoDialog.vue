<template>
    <v-card>
        <v-app-bar color="red" dark>
          <v-app-bar-title>Nuovo gioco</v-app-bar-title>
        </v-app-bar>

      <v-card-text>
        <v-form class="pt-5" v-model="formValid">
          <v-select
              v-model="selected"
              :items="mappe"
              item-value="id"
              item-text="nome"
              return-object
              label="Scegli la mappa con cui giocare"
              :rule="[v => !!v]"
          />

          <span v-if="selected">Da {{selected.numMinGiocatori}} a {{selected.numMaxGiocatori}} giocatori</span>
          <v-combobox multiple small-chips
              :disabled="!selected"
              :items="giocatoriDefault"
              v-model="elencoGiocatori"
              :rules="[validaGiocatori]"
              label="Inserisci i nomi dei giocatori"
          />
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer/>
        <v-btn text color="red" @click="$emit('close')">
          Annulla
        </v-btn>
        <v-btn text color="red" :disabled="!formValid" @click="nuovoGioco">
          Gioca
        </v-btn>
      </v-card-actions>
    </v-card>
</template>

<script>

export default {
name: "NuovoGiocoDialog",
  data() {
    return {
      show: true,
      selected: null,
      giocatoriDefault: [ "Rosso", "Verde", "Giallo", "Blu", "Viola", "Nero" ],
      elencoGiocatori: [],
      formValid: false
    }
  },
  computed: {
    mappe() {
      return this.$store.getters.getMappe
    }
  },
  methods: {
    validaGiocatori(elenco) {
      if (!this.selected)
        return false;

      return !(elenco.length < this.selected.numMinGiocatori || elenco.length > this.selected.numMaxGiocatori);
    },
    async nuovoGioco() {
      let config = {
        giocatori: this.elencoGiocatori,
        mappaId: this.selected.id,
        mod: "normal"
      }
      await this.$store.dispatch("startGame", config)
      this.$emit("close")
    }
  }
}
</script>

<style scoped>

</style>