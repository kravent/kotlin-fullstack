if (!config.devServer) {
    config.devServer = {};
}
config.devServer.historyApiFallback = {
    index: 'index.html'
};
