config.devServer = {
    ...config.devServer,

    historyApiFallback: {
        index: 'index.html'
    },

    proxy: {
        '/api': 'http://localhost:8000'
    },
}
