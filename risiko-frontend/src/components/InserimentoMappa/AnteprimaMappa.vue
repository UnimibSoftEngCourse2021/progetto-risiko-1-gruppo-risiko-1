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
import * as visNet from "vis-network";
import * as visData from "vis-data";
import networkUtils from "@/utils/networkUtils";

let network = null;

export default {
  name: "AnteprimaMappa",
  data() {
    return {
      hoverNodeInfo: ""
    };
  },

  watch: {
    anteprimaNetwork: {
      handler(value) {
        const nodes = new visData.DataSet(value.nodes);
        const edges = new visData.DataSet(value.edges);
        network.setData({nodes, edges});
      },
      deep: true
    }
  },

  mounted() {
    const container = document.getElementById("anteprima");
    const nodes = new visData.DataSet(this.anteprimaNetwork.nodes);
    const edges = new visData.DataSet(this.anteprimaNetwork.edges);
    network = new visNet.Network(container, {nodes, edges}, networkUtils.anteprimaMappaNetworkOptions);
    network.on("hoverNode", this.onHoverNode);
    network.on("blurNode", this.onBlurNode);
  },

  computed: {
    ...mapGetters(["anteprimaNetwork"])
  },

  methods: {
    onHoverNode({node}) {
      this.hoverNodeInfo = this.anteprimaNetwork.nodes.find(n => n.id === node).title;
    },
    onBlurNode() {
      this.hoverNodeInfo = ""
    }
  }
};
</script>

<style scoped>
#anteprima {
  height: 800px;
}

.text-box {
  height: 1.5rem;
}
</style>
