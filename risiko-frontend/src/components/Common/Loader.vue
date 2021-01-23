<template>
  <v-overlay v-if="loading">
    <v-progress-circular color="white" indeterminate size="100"/>
  </v-overlay>
</template>

<script>
import {mapGetters} from "vuex";

export default {
    name: "Loader",
  computed: {
      ...mapGetters(["loading"])
  }
};
</script>
