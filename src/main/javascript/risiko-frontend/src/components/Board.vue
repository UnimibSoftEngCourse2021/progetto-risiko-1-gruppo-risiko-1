<template>
  <v-container>
    <div id="network">Hello</div>
  </v-container>
</template>

<script>

let network = null;
let nodes = null
import * as visNet from "vis-network";
import * as visData from "vis-data";

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
      }
    }
  },

  watch: {
    networkData: {
      handler: function (value) {
        value.nodes.forEach(node => {
          let {id, label} = node
          nodes.update({id, label})
        })
      },
      deep: true
    }
  },

  mounted() {
    // create a network
    let container = document.getElementById('network');
    nodes = new visData.DataSet(this.networkData.nodes)
    let edges = new visData.DataSet(this.networkData.edges)
    network = new visNet.Network(container, {nodes, edges}, this.options)
    network.on("select", (data) => {
      if (data.nodes.length > 0)
        this.$emit("nodeSelected", {id: data.nodes[0]})
    })

    network.on("hoverNode", ({ node }) => {
      let continenteId = this.trovaContinente(node)
      this.evidenziaStatiContinente(continenteId)
    })
    network.on("blurNode", () => {
      network.unselectAll()
    })

  },

  computed: {
    networkData() {
      return this.$store.getters.mapNetwork
    },
    mappa() {
      return this.$store.getters.getMappaGioco
    }
  },

  methods: {
    trovaStatiInContinente(continenteId) {
      return this.mappa.stati.filter(stato => stato.continente === continenteId).map(stato => stato.id)
    },
    trovaContinente(statoId) {
      return this.mappa.stati.find(stato => stato.id === statoId).continente
    },
    evidenziaStatiContinente(continenteId) {
      let nodeIds = this.trovaStatiInContinente(continenteId)
      network.selectNodes(nodeIds, false)
    }
  }
}
</script>

<style scoped lang="scss">
@import url("~vis/dist/vis.min.css");

#network {
  height: 800px;
}
</style>