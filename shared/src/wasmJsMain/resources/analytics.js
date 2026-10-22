(() => {
    const hosts = new Set([
        "johnrusselguiwan.web.app",
        "johnrusselguiwan.firebaseapp.com",
        "devdeck-607ea.web.app",
        "devdeck-607ea.firebaseapp.com"
    ]);
    if (!hosts.has(window.location.hostname) ||
        navigator.globalPrivacyControl === true || navigator.doNotTrack === "1") {
        return;
    }

    const measurementId = "G-X3ECWQBXT6";
    window.dataLayer = window.dataLayer || [];
    window.gtag = function () { window.dataLayer.push(arguments); };
    window.gtag("js", new Date());
    window.gtag("config", measurementId, {
        page_location: window.location.origin + window.location.pathname,
        page_referrer: "",
        allow_google_signals: false,
        allow_ad_personalization_signals: false
    });
    for (const [resource, event] of [
        ["android_app", "apk_download_click"],
        ["resume", "resume_open"]
    ]) {
        window.addEventListener("devdeck:" + resource, () => {
            window.gtag("event", event, { send_to: measurementId, transport_type: "beacon" });
        });
    }
    const script = document.createElement("script");
    script.async = true;
    script.src = "https://www.googletagmanager.com/gtag/js?id=" + measurementId;
    document.head.appendChild(script);
})();
