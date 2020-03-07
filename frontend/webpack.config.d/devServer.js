config.devServer = {
    ...config.devServer,

    open: false,
    liveReload: false,

    historyApiFallback: {
        index: 'index.html'
    },

    proxy: {
        '/api': 'http://localhost:8000'
    },
}
