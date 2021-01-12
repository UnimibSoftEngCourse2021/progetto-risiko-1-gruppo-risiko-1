<template>
  <v-card>
    <v-card-title>Gioca un tris di carte territorio!</v-card-title>
    <v-card-text class="text-h6">Legenda</v-card-text>
    <v-card-text>
      3 cannoni: 4 armate<br/>
      3 fanti: 6 armate<br/>
      3 cavalieri: 8 armate<br/>
      un fante, un cannone e un cavaliere: 10 armate<br/>
      un “Jolly” più due carte uguali: 12 armate<br/>
    </v-card-text>

    <v-card-text class="text-h6">Scegli 3 carte:</v-card-text>
    <v-list v-if="carteTerritorio.length > 0">
      <v-list-item v-for="(carta, index) in carteTerritorio" :key="index">
        <v-list-item-action>
          <v-checkbox v-model="checked[index]" :disabled="numberChecked === 3 && !checked[index]"></v-checkbox>
        </v-list-item-action>

        <v-list-item-title>{{carta.figura}}</v-list-item-title>
        <v-list-item-subtitle>{{carta.statoRappresentato}}</v-list-item-subtitle>
      </v-list-item>
    </v-list>
    <v-card-text class="text-caption" v-else>
      Non hai carte territorio al momento
    </v-card-text>

    <v-card-actions>
      <v-btn color="red" text @click="$emit('close')">Chiudi</v-btn>
      <v-btn color="red" text :disabled="!trisValido" @click="confermaGiocaTris">Gioca</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import {mapActions, mapGetters} from "vuex";

export default {
  name: "TrisDialog",
  data() {
    return {
      checked: []
    }
  },
  watch: {
    carteTerritorio(value) {
      this.checked = new Array(value.length).fill(false)
    }
  },
  computed: {
    ...mapGetters(["carteTerritorio"]),
    numberChecked() {
      return this.checked.filter(c => c).length
    },
    trisValido() {
      if (this.numberChecked !== 3)
        return false

      let carte = []
      this.checked.forEach((c, index) => {
        if (c)
          carte.push(this.carteTerritorio[index])
      })

      return (this.countJolly(carte) === 0 && (this.figureUguali(carte) || this.figureDiverse(carte))) ||
          (this.countJolly(carte) === 1 && this.coppia(carte))
    }
  },
  methods: {
    ...mapActions(["giocaTris"]),
    figureUguali(carte) {
      return carte[0].figura === carte[1].figura && carte[0].figura === carte[2].figura
    },
    figureDiverse(carte) {
      return carte[0].figura !== carte[1].figura && carte[1].figura !== carte[2].figura && carte[0].figura !== carte[2].figura
    },
    countJolly(carte) {
      return carte.filter(c => c.figura === "JOLLY").length
    },
    coppia(carte) {
      return carte[0].figura === carte[1].figura || carte[1].figura === carte[2].figura || carte[0].figura === carte[2].figura
    },
    async confermaGiocaTris() {
      let carteId = []
      this.checked.forEach((c, index) => {
        if (c)
          carteId.push(this.carteTerritorio[index].id)
      })
      this.$emit("close")
      await this.giocaTris(carteId)
    }
  },
  mounted() {
    this.checked = new Array(this.carteTerritorio.length).fill(false)
  }
}
</script>

<style scoped>

</style>