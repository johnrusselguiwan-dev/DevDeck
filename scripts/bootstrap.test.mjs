import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";
import vm from "node:vm";

const source = readFileSync(new URL("../shared/src/wasmJsMain/resources/bootstrap.js", import.meta.url), "utf8");
function page() {
    const listeners = new Map();
    const status = { removed: false, remove() { this.removed = true; } };
    const message = { textContent: "Loading" };
    const spinner = { hidden: false };
    const document = { getElementById: id => ({ "bootstrap-status": status.removed ? null : status,
        "bootstrap-message": message, spinner })[id] };
    const window = { addEventListener: (name, listener) => listeners.set(name, listener) };
    vm.runInNewContext(source, { window, document });
    return { status, message, spinner, emit: (name, event = {}) => listeners.get(name)?.(event) };
}

test("script loading and Wasm startup failures show recovery without removing content", () => {
    for (const [event, detail] of [
        ["error", { target: { tagName: "SCRIPT", src: "https://example.com/devdeck.js" } }],
        ["error", { target: { tagName: "SCRIPT", src: "https://example.com/platform.js" } }],
        ["error", { filename: "https://example.com/devdeck.js", target: {} }],
        ["error", { filename: "https://example.com/platform.js", target: {} }],
        ["devdeck:failed", {}], ["unhandledrejection", {}]
    ]) {
        const view = page();
        view.emit(event, detail);
        assert.match(view.message.textContent, /couldn.t load/i);
        assert.equal(view.spinner.hidden, true);
        assert.equal(view.status.removed, false);
    }
});

test("successful composition clears loading and ignores later unrelated errors", () => {
    const view = page();
    view.emit("devdeck:ready");
    assert.equal(view.status.removed, true);
    view.emit("unhandledrejection");
    assert.equal(view.message.textContent, "Loading");
});

test("analytics failures do not replace application startup status", () => {
    const view = page();
    view.emit("error", { target: { tagName: "SCRIPT", src: "https://example.com/analytics.js" } });
    view.emit("error", { filename: "https://example.com/analytics.js", target: {} });
    assert.equal(view.message.textContent, "Loading");
});

test("recovery links exist in HTML even when bootstrap JavaScript cannot load", () => {
    const html = readFileSync(new URL("../shared/src/wasmJsMain/resources/index.html", import.meta.url), "utf8");
    const status = html.slice(html.indexOf('id="bootstrap-status"'), html.indexOf('<noscript>'));
    assert.match(status, /href="overview.html"/);
    assert.match(status, /href="index.html"/);
    assert.ok(html.indexOf('src="bootstrap.js"') < html.indexOf('src="platform.js"'));
});
