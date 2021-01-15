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

let network = null;
let nodes = null
import * as visNet from "vis-network";
import * as visData from "vis-data";
import utils from "@/store/utils";

export default {
  name: "Board",
  data() {
    return {
      options: {
        nodes: {
          shape: "dot",

          size: 30,
          font: {
            size: 38,
            color: "#ffffff",
            align: "center"
          },
          borderWidth: 2,

          chosen: {
            node: function (values) { values.size = 35; values.color = "black" }
          }
        },
        edges: {
          width: 2,
        },
        interaction: {
          dragView: false,
          zoomView: false,
          dragNodes: false,
          hover: true,
          tooltipDelay: 0
        },
        physics: {
          solver: "forceAtlas2Based",
          stabilization: {
            iterations: 2000
          },
          forceAtlas2Based: {
            avoidOverlap: 1
          }
        }
      },
      hoverNodeInfo: "",
      colors: []
    }
  },

  watch: {
    mapNetwork: {
      handler: function (value) {
        value.nodes.forEach(node => {
          let {id, label, group} = node
          nodes.update({id, label, group})
        })
        this.setColors()
      },
      deep: true
    }
  },

  mounted() {
    // create a network
    let container = document.getElementById('network');
    nodes = new visData.DataSet(this.mapNetwork.nodes)
    let edges = new visData.DataSet(this.mapNetwork.edges)
    network = new visNet.Network(container, {nodes, edges}, this.options)
    network.on("select", (data) => {
      if (data.nodes.length > 0)
        this.$emit("nodeSelected", {id: data.nodes[0]})
    })

    network.on("hoverNode", ({ node }) => {
      let continente = this.trovaContinente(node)
      this.evidenziaStatiContinente(continente.id)
      let stato = utils.trovaStatoId(this.mappaGioco, node)
      this.hoverNodeInfo = continente.nome.toUpperCase() + " - " + stato.nome
    })

    network.on("blurNode", () => {
      network.unselectAll()
      this.hoverNodeInfo = ""
    })

    this.setColors()
  },

  computed: {
    ...mapGetters(["mapNetwork", "mappaGioco", "giocatori", "giocatoreAttivo"])
  },

  methods: {
    setColors() {
      if (!network)
        this.colors = []

      let ris = []
      this.giocatori.forEach((g, index) => {
        let ids = Object.keys(network.body.nodes)
        let id = ids.find(id => network.body.nodes[id].options.group === index)
        let n = network.body.nodes[id]
        ris.push({ giocatore: g.nome, color: n.options.color.background })
      })

      this.colors=ris
    },
    trovaStatiInContinente(continenteId) {
      return this.mappaGioco.stati.filter(stato => stato.continente === continenteId).map(stato => stato.id)
    },
    trovaContinente(statoId) {
      let continenteId = this.mappaGioco.stati.find(stato => stato.id === statoId).continente
      return this.mappaGioco.continenti.find(c => c.id === continenteId)
    },
    evidenziaStatiContinente(continenteId) {
      let nodeIds = this.trovaStatiInContinente(continenteId)
      network.selectNodes(nodeIds, false)
    }
  }
}
</script>

<style scoped lang="scss">
#network {
  height: 800px;
}

.text-box {
  height: 1.5rem;
}
</style>