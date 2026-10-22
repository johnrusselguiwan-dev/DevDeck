import { mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const escapeHtml = value => String(value).replace(/[&<>"']/g, character => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
})[character]);

export function renderOverview(portfolio) {
    const { profile, skills, experiences, projects, socialLinks } = portfolio;
    const skillCategories = {
        MOBILE_DEV: "Android & Mobile Dev",
        CROSS_PLATFORM: "Multiplatform & Web",
        ARCHITECTURE: "Architecture & Design",
        DEVOPS: "DevOps & Play Console"
    };
    const website = profile.websiteUrl ? new URL(profile.websiteUrl) : null;
    const link = (url, label) => {
        const destination = new URL(url);
        if (!["https:", "http:", "mailto:", "tel:"].includes(destination.protocol)) {
            throw new Error(`Unsupported portfolio link protocol: ${destination.protocol}`);
        }
        const href = destination.origin === website?.origin
            ? destination.pathname + destination.search + destination.hash : url;
        return `<a href="${escapeHtml(href)}">${escapeHtml(label)}</a>`;
    };
    const list = entries => `<ul>${entries.map(entry => `<li>${escapeHtml(entry)}</li>`).join("")}</ul>`;
    const experience = entry => `<article>
        <h3>${escapeHtml(entry.role)}</h3>
        <p class="meta">${escapeHtml(entry.company)} · ${escapeHtml(entry.period)}</p>
        <p>${escapeHtml(entry.summary)}</p>
        ${list(entry.highlights)}
    </article>`;
    const project = entry => `<article>
        <h3>${escapeHtml(entry.title)}</h3>
        <p class="meta">${escapeHtml(entry.subtitle)}</p>
        <p>${escapeHtml(entry.description)}</p>
        ${entry.contributions?.length ? `<h4>My contribution</h4>${list(entry.contributions)}` : ""}
        <p class="meta">${escapeHtml(entry.techBadges.join(" · "))}</p>
        <p>${[
            entry.playStoreUrl && link(entry.playStoreUrl, `${entry.title} on Google Play`),
            entry.githubUrl && link(entry.githubUrl, `${entry.title} source on GitHub`)
        ].filter(Boolean).join(" · ")}</p>
    </article>`;
    const work = experiences.filter(entry => !entry.isEducation);
    const education = experiences.filter(entry => entry.isEducation);
    return `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${escapeHtml(profile.name)} - Android Developer Portfolio</title>
    <meta name="description" content="${escapeHtml(profile.bio)}">
    ${website ? `<link rel="canonical" href="${escapeHtml(new URL("overview.html", website).href)}">` : ""}
    <style>
        :root { color-scheme: light dark; --page: #fafafa; --text: #27272a; --muted: #52525b; --line: #d4d4d8; }
        * { box-sizing: border-box; }
        body { margin: 0; background: var(--page); color: var(--text); font: 17px/1.65 system-ui, sans-serif; }
        main, header, footer { width: min(100% - 40px, 900px); margin-inline: auto; }
        header { padding-block: 24px 32px; border-bottom: 1px solid var(--line); }
        nav { display: flex; flex-wrap: wrap; gap: 8px 24px; }
        nav a { display: inline-block; padding-block: 8px; }
        a { color: inherit; text-underline-offset: 4px; }
        a:hover { text-decoration-thickness: 2px; }
        a:focus-visible { outline: 2px solid currentColor; outline-offset: 4px; }
        h1 { font-size: clamp(2rem, 5vw, 3rem); line-height: 1.15; margin-bottom: 12px; }
        h2 { font-size: 1.5rem; border-bottom: 1px solid var(--line); padding-bottom: 12px; }
        h3 { font-size: 1.2rem; margin-bottom: 4px; }
        h4 { font-size: 1rem; margin-bottom: 0; }
        p { margin-block: 8px 16px; }
        li { margin-block: 8px; }
        .skill-categories { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 24px; }
        dl > div { margin-bottom: 20px; }
        dd { margin: 6px 0 0; }
        @media (max-width: 760px) { .skill-categories { grid-template-columns: 1fr; } }
        .meta { color: var(--muted); }
        section { margin-block: 40px; scroll-margin-top: 20px; }
        article { margin-block: 28px; }
        footer { padding-block: 24px; border-top: 1px solid var(--line); }
        @media (prefers-color-scheme: dark) {
            :root { --page: #09090b; --text: #f4f4f5; --muted: #a1a1aa; --line: #3f3f46; }
        }
        @media print { body { font-size: 11pt; } nav { display: none; } }
    </style>
</head>
<body>
    <header>
        <nav aria-label="Portfolio resources">
            <a href="index.html">Interactive portfolio</a>
            ${profile.resumeUrl ? link(profile.resumeUrl, "Resume (PDF)") : ""}
            ${socialLinks.filter(entry => entry.platform === "Email").map(entry => link(entry.url, "Email me")).join("")}
            <a href="#projects">Projects</a>
        </nav>
        <h1>${escapeHtml(profile.name)}</h1>
        <p><strong>${escapeHtml(profile.title)} · Kotlin &amp; Jetpack Compose</strong></p>
        <p class="meta">${escapeHtml(profile.location)} · ${escapeHtml(profile.yearsExperience)}</p>
        <p>${escapeHtml(profile.bio)}</p>
    </header>
    <main>
        <section aria-labelledby="experience"><h2 id="experience">Experience</h2>${work.map(experience).join("")}</section>
        <section aria-labelledby="projects"><h2 id="projects">Projects</h2>${projects.map(project).join("")}</section>
        <section aria-labelledby="skills">
            <h2 id="skills">Skills</h2>
            <div class="skill-categories">${Object.entries(skillCategories).map(([category, name]) => `<div><h3>${escapeHtml(name)}</h3><dl>${skills.filter(skill => skill.category === category).map(skill => `<div><dt><strong>${escapeHtml(skill.name)}</strong> <span class="meta">· ${escapeHtml(skill.specialty)}</span></dt><dd>${escapeHtml(skill.description)}</dd></div>`).join("")}</dl></div>`).join("")}</div>
        </section>
        <section aria-labelledby="education"><h2 id="education">Education</h2>${education.map(experience).join("")}</section>
        <section aria-labelledby="contact">
            <h2 id="contact">Contact</h2>
            <ul>${socialLinks.map(entry => `<li>${link(entry.url, `${entry.platform}: ${entry.url.replace(/^(https?:\/\/|mailto:|tel:)/, "")}`)}</li>`).join("")}</ul>
            ${profile.resumeUrl ? `<p>${link(profile.resumeUrl, "View resume (PDF)")}</p>` : ""}
            ${profile.androidDownloadUrl ? `<p>${link(profile.androidDownloadUrl, "Download the Android portfolio")}</p>` : ""}
        </section>
    </main>
    <footer><a href="index.html">Open the interactive Kotlin/Compose portfolio</a></footer>
</body>
</html>
`;
}

if (process.argv[1] && resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
    if (process.argv.length !== 3) throw new Error("Usage: node scripts/generate-portfolio-overview.mjs <output.html>");
    const source = new URL("../shared/src/commonMain/composeResources/files/portfolio.json", import.meta.url);
    const output = resolve(process.argv[2]);
    const html = renderOverview(JSON.parse(readFileSync(source, "utf8")));
    mkdirSync(dirname(output), { recursive: true });
    writeFileSync(output, html);
}
