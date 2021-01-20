<template>
  <v-dialog v-model="showDialog" max-width="700px">
    <v-card>
      <v-app-bar class="d-flex align-center" color="primary" dark>
        <v-icon class="mx-3" large>mdi-cards</v-icon>
        <v-app-bar-title>
          GIOCA UN TRIS DI CARTE TERRITORIO
        </v-app-bar-title>
      </v-app-bar>

      <v-card-text class="text-subtitle-2 black--text mt-6">Scegli 3 carte:</v-card-text>
      <v-card-text class="black--text">
        <v-list v-if="carteTerritorio.length > 0" class="lista-carte">
          <v-list-item v-for="(carta, index) in carteTerritorio" :key="index" two-line>
            <v-list-item-action>
              <v-checkbox v-model="checked[index]" :disabled="numberChecked === 3 && !checked[index]"
                          color="primary"
              />
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>{{ carta.figura }}</v-list-item-title>
              <v-list-item-subtitle>{{ carta.statoRappresentato }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list>

        <span v-else class="d-block text-body-2">
              Non hai carte territorio al momento
      </span>
      </v-card-text>

      <v-card-actions>
        <v-spacer/>
        <v-btn color="primary" text @click="showDialog = false">Chiudi</v-btn>
        <v-btn :disabled="!trisValido" color="primary" text @click="confermaGiocaTris">Gioca</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import {mapActions, mapGetters} from "vuex";
import gameConstants from "@/utils/gameConstants";

export default {
  name: "TrisDialog",
  data() {
    return {
      checked: [],
      showDialog: false
    };
  },
  watch: {
    carteTerritorio(value) {
      this.checked = new Array(value.length).fill(false);
    }
  },
  computed: {
    ...mapGetters(["carteTerritorio"]),
    numberChecked() {
      return this.checked.filter(c => c).length;
    },
    trisValido() {
      if (this.numberChecked !== 3) {
        return false;
      }
      const carte = this.carteTerritorio.filter((value, index) => this.checked[index])
      return gameConstants.trisValido(carte)
    }
  },
  methods: {
    ...mapActions(["giocaTris"]),
    async confermaGiocaTris() {
      const carteId = this.carteTerritorio.filter((value, index) => this.checked[index]).map(carta => carta.id)
      this.showDialog = false;
      await this.giocaTris(carteId);
    },
    show() {
      this.showDialog = true
    }
  },
  mounted() {
    this.checked = new Array(this.carteTerritorio.length).fill(false);
  }
};
</script>

<style scoped>
.lista-carte {
  max-height: 30rem;
  overflow-y: auto;
}
</style>
