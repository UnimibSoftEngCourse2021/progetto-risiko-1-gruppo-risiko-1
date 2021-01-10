<template>
  <v-card>
    <v-card-title>Esito combattimento</v-card-title>

    <v-card-text>
      <span class="d-block">Tiri attaccante: {{combattimento.dadoAtt}}</span>
      <span class="d-block">Tiri difensore: {{combattimento.dadoDif}}</span>

      <span class="d-block">L'attaccante perde {{combattimento.vittimeAtt}} mentre il difensore perde
        {{combattimento.vittimeDif}} armate</span>
    </v-card-text>

    <div v-if="combattimento.vittoriaAtt">

    </div>
    <v-card-actions v-else>
      <v-btn color="red" text>OK</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
export default {
  name: "CombattimentoDialog",
  computed: {
    combattimento() {
      return this.$store.getters.getCombattimento
    }
  },
  methods: {
    chiudi() {
      this.$store.commit("clearCombattimento")
      this.$emit("close")
    }
  }
}
</script>

<style scoped>

</style>