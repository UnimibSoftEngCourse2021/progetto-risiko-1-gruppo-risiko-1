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
            size: 32,
            color: "#ffffff",
            align: "center"
          },
          borderWidth: 2
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
          let { id, label } = node
          nodes.update({ id, label })
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
    network = new visNet.Network(container, { nodes, edges }, this.options)
    network.on("select", (data) => {
      if (data.nodes.length > 0)
        this.$emit("nodeSelected", { id: data.nodes[0] })
    })
  },

  computed: {
    networkData() {
      return this.$store.getters.mapNetwork
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