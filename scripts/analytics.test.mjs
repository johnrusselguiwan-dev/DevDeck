import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";
import vm from "node:vm";

const source = readFileSync(new URL("../shared/src/wasmJsMain/resources/analytics.js", import.meta.url), "utf8");
function load(hostname, preferences = {}) {
    const scripts = [];
    const listeners = new Map();
    const window = {
        location: { hostname, origin: `https://${hostname}`, pathname: "/", search: "?private=value" },
        addEventListener: (name, callback) => listeners.set(name, callback),
        dispatchEvent: event => listeners.get(event.type)?.(event)
    };
    const document = { createElement: () => ({}), head: { appendChild: element => scripts.push(element) } };
    vm.runInNewContext(source, { window, document, navigator: preferences });
    return { window, scripts, commands: () => Array.from(window.dataLayer || [], command => Array.from(command)) };
}

test("preview hosts and privacy opt-outs do not load analytics or record events", () => {
    for (const [hostname, preferences] of [
        ["localhost", {}], ["127.0.0.1", {}], ["example.com", {}],
        ["johnrusselguiwan.web.app", { globalPrivacyControl: true }],
        ["johnrusselguiwan.web.app", { doNotTrack: "1" }]
    ]) {
        const page = load(hostname, preferences);
        assert.equal(page.scripts.length, 0);
        assert.equal(page.commands().length, 0);
        page.window.dispatchEvent({ type: "devdeck:android_app" });
        page.window.dispatchEvent({ type: "devdeck:resume" });
        assert.equal(page.commands().length, 0);
    }
});

test("production initializes the existing private stream without query data or advertising features", () => {
    const page = load("johnrusselguiwan.web.app");
    const config = page.commands().find(command => command[0] === "config");
    assert.equal(config[1], "G-X3ECWQBXT6");
    assert.equal(config[2].page_location, "https://johnrusselguiwan.web.app/");
    assert.equal(config[2].page_referrer, "");
    assert.equal(config[2].allow_google_signals, false);
    assert.equal(config[2].allow_ad_personalization_signals, false);
    assert.equal(page.scripts.length, 1);
    assert.equal(page.scripts[0].async, true);
    assert.equal(page.scripts[0].src, "https://www.googletagmanager.com/gtag/js?id=G-X3ECWQBXT6");
});

test("resource actions report only the resource category, without link URLs or contact details", () => {
    const page = load("johnrusselguiwan.web.app");
    page.window.dispatchEvent({ type: "devdeck:android_app" });
    page.window.dispatchEvent({ type: "devdeck:resume" });
    page.window.dispatchEvent({ type: "devdeck:email" });
    page.window.dispatchEvent({ type: "devdeck:toString" });
    const events = page.commands().filter(command => command[0] === "event");
    assert.deepEqual(events.map(command => command[1]), ["apk_download_click", "resume_open"]);
    for (const event of events) {
        assert.equal(event[2].send_to, "G-X3ECWQBXT6");
        assert.equal(event[2].transport_type, "beacon");
        assert.deepEqual(Object.keys(event[2]).sort(), ["send_to", "transport_type"]);
    }
});
