import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";
import vm from "node:vm";

const source = readFileSync(new URL("../shared/src/wasmJsMain/resources/platform.js", import.meta.url), "utf8");
function load(navigator = {}, stored = null, blocked = false) {
    const attributes = {};
    const window = {
        localStorage: {
            getItem() { if (blocked) throw Error("Blocked"); return stored; },
            setItem(key, value) { if (blocked) throw Error("Blocked"); stored = value; }
        }
    };
    const document = { documentElement: { setAttribute: (key, value) => { attributes[key] = value; } } };
    vm.runInNewContext(source, { window, navigator, document });
    return { window, attributes, stored: () => stored };
}

const url = "https://johnrusselguiwan.web.app";
function share(page) {
    return new Promise(resolve => page.window.devdeckShare(url, resolve));
}

test("theme initializes before Wasm and persists only recognized choices", () => {
    const page = load({}, "DARK");
    assert.equal(page.attributes["data-theme"], "DARK");
    page.window.devdeckSaveTheme("LIGHT");
    assert.equal(page.stored(), "LIGHT");
    assert.equal(page.attributes["data-theme"], "LIGHT");
    page.window.devdeckSaveTheme("invalid");
    assert.equal(page.stored(), "LIGHT");
    assert.equal(load({}, "invalid").attributes["data-theme"], "SYSTEM");
});

test("blocked storage does not prevent loading or changing the current theme", () => {
    const page = load({}, null, true);
    assert.equal(page.attributes["data-theme"], "SYSTEM");
    page.window.devdeckSaveTheme("DARK");
    assert.equal(page.attributes["data-theme"], "DARK");
});

test("native browser sharing receives the portfolio URL", async () => {
    let received;
    const page = load({ share: async value => { received = value; } });
    assert.equal(await share(page), "SHARED");
    assert.equal(received.url, url);
});

test("cancelling sharing does not copy anything", async () => {
    let copies = 0;
    const page = load({
        share: async () => { throw Object.assign(Error("Cancelled"), { name: "AbortError" }); },
        clipboard: { writeText: async () => { copies++; } }
    });
    assert.equal(await share(page), "CANCELLED");
    assert.equal(copies, 0);
});

test("unavailable or failed sharing falls back to copying the URL", async () => {
    for (const nativeShare of [undefined, async () => { throw Error("Unavailable"); }]) {
        let copied;
        const page = load({ share: nativeShare, clipboard: { writeText: async text => { copied = text; } } });
        assert.equal(await share(page), "COPIED");
        assert.equal(copied, url);
    }
});

test("clipboard denial and unsupported browsers report failure", async () => {
    assert.equal(await share(load()), "FAILED");
    assert.equal(await share(load({ clipboard: { writeText: async () => { throw Error("Denied"); } } })), "FAILED");
});
