<template>
  <v-container class="pa-0">
    <div class="text-box mb-2">
      <span class="white--text d-block text-center text-h6">
        {{hoverNodeInfo}}
      </span>
    </div>
    <div class="d-flex">
      <div class="d-inline-block my-1 mx-3 d-flex flex-column align-center" v-for="c in colors" :key="c.giocatore">
          <v-btn class="mx-3" :color="c.color" x-small fab>
          </v-btn>
          <span class="white--text text-body-1 d-block text--center">{{c.giocatore}}</span>
      </div>
    </div>
    <div id="network">
    </div>
  </v-container>
</template>

<script>

import {mapGetters} from "vuex";
import * as visNet from "vis-network";
import * as visData from "vis-data";
import networkUtils from "@/utils/networkUtils";

let network = null;
let nodes = null;

export default {
    name: "Board",
    data() {
        return {
            options: networkUtils.gameNetworkOptions,
            hoverNodeInfo: ""
        };
    },
  props: ["onNodeSelected"],

    watch: {
        // update network's nodes when their data change
        mapNetwork: {
            handler(value) {
              value.nodes.forEach(node => nodes.update(node))
            },
            deep: true
        }
    },

    mounted() {
        const container = document.getElementById("network");

        nodes = new visData.DataSet(this.mapNetwork.nodes);
        const edges = new visData.DataSet(this.mapNetwork.edges);

        this.giocatori.forEach(giocatore =>
            this.options.groups[giocatore.nome] = { color: { ...this.gameColors[giocatore.nome] } }
        );

        network = new visNet.Network(container, { nodes, edges }, this.options);
        network.on("select", this.selectNode);
        network.on("hoverNode", this.hoverNode);
        network.on("blurNode", this.blurNode);
    },

    computed: {
        ...mapGetters(["mapNetwork", "mappaGioco", "giocatori", "giocatoreAttivo", "gameColors"]),
        colors() {
          return this.giocatori.map(g => { return {
            giocatore: g.nome,
            color: this.gameColors[g.nome].background
          }})
        }
    },

    methods: {
      evidenziaStatiContinente(continenteId) {
        const idStati = this.mappaGioco.stati.filter(stato => stato.continente === continenteId).map(stato => stato.id);
        network.selectNodes(idStati, false);
      },
      selectNode(selectedItems) {
          if (selectedItems.nodes.length > 0)
            this.$props.onNodeSelected({ id: selectedItems.nodes[0] })
      },
      hoverNode({ node }) {
        const stato = this.mappaGioco.trovaStatoId(node)
        const continente = this.mappaGioco.continenti.find(c => c.id === stato.continente);
        this.evidenziaStatiContinente(continente.id);
        this.hoverNodeInfo = `${continente.nome.toUpperCase()} - ${stato.nome}`;
      },
      blurNode() {
        network.unselectAll();
        this.hoverNodeInfo = "";
      }
    }
};
</script>

<style scoped lang="scss">
#network {
  height: 800px;
}

.text-box {
  height: 1.5rem;
}
</style>
