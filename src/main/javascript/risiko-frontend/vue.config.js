module.exports = {
  "outputDir": "../../../../target/frontend/public",
  "transpileDependencies": [
    "vuetify"
  ],
  devServer: {
    proxy: {
      '^/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}