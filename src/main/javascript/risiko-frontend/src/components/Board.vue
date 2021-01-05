<template>
  <v-container>
    <div id="network">Hello</div>
  </v-container>
</template>

<script>

let network = null;
import * as visNet from "vis-network";

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
        network.setData(value);
      },
      deep: true
    }
  },

  mounted() {
    // create a network
    let container = document.getElementById('network');
    network = new visNet.Network(container, this.networkData, this.options);

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