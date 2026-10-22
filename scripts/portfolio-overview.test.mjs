import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";
import { renderOverview } from "./generate-portfolio-overview.mjs";

const portfolio = JSON.parse(readFileSync(new URL("../shared/src/commonMain/composeResources/files/portfolio.json", import.meta.url), "utf8"));

test("the text portfolio preserves current shared content and working local resource links", () => {
    const html = renderOverview(portfolio);
    for (const project of portfolio.projects) {
        assert.ok(html.includes(project.title));
        for (const contribution of project.contributions || []) assert.ok(html.includes(contribution));
    }
    assert.ok(html.indexOf('id="experience"') < html.indexOf('id="education"'));
    assert.ok(html.includes('href="/resume/John_Russel_Guiwan_Resume.pdf"'));
    assert.ok(html.includes('href="index.html"'));
    assert.ok(html.includes('href="mailto:russelguiwan@gmail.com"'));
    assert.ok(!html.includes("<script"));
});

test("shared content is escaped as text and unsafe URL protocols fail generation", () => {
    const content = structuredClone(portfolio);
    content.profile.name = '<script>alert("test")</script>';
    content.projects[0].contributions = ['<img src=x onerror="alert(1)">'];
    const html = renderOverview(content);
    assert.ok(!html.includes("<script"));
    assert.ok(!html.includes("<img"));
    assert.ok(html.includes("&lt;script&gt;"));
    assert.ok(html.includes("&lt;img"));
    content.projects[0].githubUrl = "javascript:alert(1)";
    assert.throws(() => renderOverview(content), /Unsupported portfolio link protocol/);
});

test("optional profile resource URLs can be omitted", () => {
    const content = structuredClone(portfolio);
    delete content.profile.websiteUrl;
    content.profile.resumeUrl = null;
    content.profile.androidDownloadUrl = null;
    const html = renderOverview(content);
    assert.ok(html.includes("MLBB Game Guide"));
    assert.ok(!html.includes('rel="canonical"'));
    assert.ok(!html.includes("Resume (PDF)"));
    assert.ok(!html.includes("Download the Android portfolio"));
});
