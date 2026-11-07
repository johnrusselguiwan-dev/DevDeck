(function () {
    const status = () => document.getElementById("bootstrap-status");
    const fail = () => {
        if (!status()) return;
        document.getElementById("bootstrap-message").textContent =
            "Couldn’t load the interactive portfolio. Retry or read the text version below.";
        document.getElementById("spinner").hidden = true;
    };
    window.addEventListener("devdeck:ready", () => status()?.remove());
    window.addEventListener("devdeck:failed", fail);
    window.addEventListener("unhandledrejection", fail);
    window.addEventListener("error", event => {
        const script = event.target;
        const source = script?.tagName === "SCRIPT" ? script.src : event.filename;
        if (/\/(platform|devdeck)\.js(?:[?#]|$)/.test(source || "")) fail();
    }, true);
})();
