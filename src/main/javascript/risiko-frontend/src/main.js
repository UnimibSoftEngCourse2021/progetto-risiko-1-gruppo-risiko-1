import Vue from 'vue'
import './plugins/axios'
import App from './App.vue'
import store from './store'
import vuetify from './plugins/vuetify';

Vue.config.productionTip = false

let vueApp = new Vue({
  store,
  vuetify,
  render: h => h(App)
})

vueApp.$mount('#app')

export { vueApp };