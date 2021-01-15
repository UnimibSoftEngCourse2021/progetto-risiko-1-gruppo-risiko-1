<template>
  <v-container class="black">
    <div class="text-box mb-2">
      <span class="white--text d-block text-center text-h6">
        {{ hoverNodeInfo }}
      </span>
    </div>
    <div id="anteprima"/>
  </v-container>
</template>

<script>

import {mapGetters} from "vuex";

let network = null
import * as visNet from "vis-network";
import * as visData from "vis-data";

export default {
  name: "AnteprimaMappa",
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
            node: function (values) {
              values.size = 35;
              values.color = "black"
            }
          }
        },
        edges: {
          width: 2,
        },
        interaction: {
          dragView: true,
          zoomView: true,
          dragNodes: true,
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
      hoverNodeInfo: ""
    }
  },

  watch: {
    anteprimaNetwork: {
      handler: function (value) {
        let nodes = new visData.DataSet(value.nodes)
        let edges = new visData.DataSet(value.edges)
        network.setData({ nodes, edges })
      },
      deep: true
    }
  },

  mounted() {
    let container = document.getElementById('anteprima');
    let nodes = new visData.DataSet(this.anteprimaNetwork.nodes)
    let edges = new visData.DataSet(this.anteprimaNetwork.edges)
    network = new visNet.Network(container, {nodes, edges}, this.options)

    network.on("hoverNode", ({node}) => {
      this.hoverNodeInfo = this.anteprimaNetwork.nodes.find(n => n.id === node).title
    })

    network.on("blurNode", () => {
      this.hoverNodeInfo = ""
    })
  },

  computed: {
    ...mapGetters(["anteprimaNetwork"]),
  }
}
</script>

<style scoped>
#anteprima {
  height: 800px;
}


.text-box {
  height: 1.5rem;
}
</style>