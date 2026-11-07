(() => {
    const fs = require("fs");
    const path = require("path");
    let projectRoot = process.cwd();
    while (!fs.existsSync(path.join(projectRoot, "settings.gradle.kts"))) {
        const parent = path.dirname(projectRoot);
        if (parent === projectRoot) throw new Error("Cannot locate DevDeck's source resources");
        projectRoot = parent;
    }
    // Full Compose startup can exceed Mocha's two-second unit-test default.
    config.client = config.client || {};
    config.client.mocha = config.client.mocha || {};
    config.client.mocha.timeout = 30000;
    config.files.unshift({
        pattern: path.join(projectRoot, "shared/src/wasmJsMain/resources/platform.js"),
        watched: false,
        included: true,
        served: true
    });
})();
