<template>
  <v-card>
    <v-app-bar color="primary" dark class="d-flex align-center">
      <v-icon large class="mx-3">mdi-cards</v-icon>
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
            <v-list-item-title>{{carta.figura}}</v-list-item-title>
            <v-list-item-subtitle>{{carta.statoRappresentato}}</v-list-item-subtitle>
          </v-list-item-content>


        </v-list-item>
      </v-list>

      <span class="d-block text-body-2" v-else>
              Non hai carte territorio al momento
      </span>
    </v-card-text>

    <v-card-actions>
      <v-spacer/>
      <v-btn color="primary" text @click="$emit('close')">Chiudi</v-btn>
      <v-btn color="primary" text :disabled="!trisValido" @click="confermaGiocaTris">Gioca</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import { mapActions, mapGetters } from "vuex";

export default {
    name: "TrisDialog",
    data() {
        return {
            checked: []
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

            const carte = [];

            this.checked.forEach((c, index) => {
                if (c) {
                    carte.push(this.carteTerritorio[index]);
                }
            });

            return (this.countJolly(carte) === 0 && (this.figureUguali(carte) || this.figureDiverse(carte))) ||
          (this.countJolly(carte) === 1 && this.coppia(carte));
        }
    },
    methods: {
        ...mapActions(["giocaTris"]),
        figureUguali(carte) {
            return carte[0].figura === carte[1].figura && carte[0].figura === carte[2].figura;
        },
        figureDiverse(carte) {
            return carte[0].figura !== carte[1].figura && carte[1].figura !== carte[2].figura && carte[0].figura !== carte[2].figura;
        },
        countJolly(carte) {
            return carte.filter(c => c.figura === "JOLLY").length;
        },
        coppia(carte) {
            return carte[0].figura === carte[1].figura || carte[1].figura === carte[2].figura || carte[0].figura === carte[2].figura;
        },
        async confermaGiocaTris() {
            const carteId = [];

            this.checked.forEach((c, index) => {
                if (c) {
                    carteId.push(this.carteTerritorio[index].id);
                }
            });
            this.$emit("close");
            await this.giocaTris(carteId);
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
