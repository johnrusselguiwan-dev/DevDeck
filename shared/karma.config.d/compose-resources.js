const path = require('path');
const resourceRoot = path.resolve(
    config.basePath,
    '../../../../shared/build/processedResources/wasmJs/main'
);

// Karma does not serve the main Compose assets alongside the test executable.
config.files.push({
    pattern: path.join(resourceRoot, 'composeResources/**/*'),
    included: false,
    served: true,
    watched: false
});
config.proxies['/composeResources/'] = '/absolute' + resourceRoot + '/composeResources/';
