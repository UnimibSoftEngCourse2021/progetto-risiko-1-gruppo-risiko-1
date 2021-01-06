<template>
  <v-card>
    <v-app-bar color="red" dark>
      <v-app-bar-title>Obiettivi</v-app-bar-title>
    </v-app-bar>

    <v-card-text class="pa-5">
      Attenzione! Cliccando su un giocatore, verrà mostrato il suo obiettivo segreto. Fai in modo che gli altri giocatori
      siano girati mentre lo leggi
    </v-card-text>

    <v-expansion-panels>
      <v-expansion-panel v-for="ob in obiettivi" :key="ob.nome">
        <v-expansion-panel-header>Obiettivo di {{ob.nome}}</v-expansion-panel-header>
        <v-expansion-panel-content>{{ob.obiettivo}}</v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-card>
</template>

<script>
export default {
  name: "ObiettiviDialog",
  computed: {
    obiettivi() {
      return this.$store.getters.getGiocatori.map(gioc => { return { nome: gioc.nome, obiettivo: gioc.obiettivo } })
    }
  }
}
</script>

<style scoped>

</style>