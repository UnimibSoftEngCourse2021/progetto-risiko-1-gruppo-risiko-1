/** Configuration file for Vue.js framework */

module.exports = {
  // set build directory inside maven target directory
  "outputDir": "../../../../target/frontend/public",

  "transpileDependencies": [
    "vuetify"
  ],

  // proxy rest api to local BE while developing (to exploit hot reload FE and BE must run as different processes)
  devServer: {
    proxy: {
      '^/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}