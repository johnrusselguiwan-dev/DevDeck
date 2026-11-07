(function () {
    const themeKey = "devdeck.theme";
    const validTheme = value => ["SYSTEM", "LIGHT", "DARK"].includes(value);
    let savedTheme = "SYSTEM";
    try {
        const stored = window.localStorage.getItem(themeKey);
        if (validTheme(stored)) savedTheme = stored;
    } catch (_) { /* Storage can be disabled by browser privacy settings. */ }
    document.documentElement.setAttribute("data-theme", savedTheme);
    window.devdeckLoadTheme = () => savedTheme;
    window.devdeckSaveTheme = value => {
        if (!validTheme(value)) return;
        savedTheme = value;
        document.documentElement.setAttribute("data-theme", value);
        try { window.localStorage.setItem(themeKey, value); } catch (_) { /* Keep the current session usable. */ }
    };
    window.devdeckShare = async (url, onResult) => {
        if (typeof navigator.share === "function") {
            try {
                await navigator.share({ title: "DevDeck portfolio", url });
                onResult("SHARED");
                return;
            } catch (error) {
                if (error.name === "AbortError") {
                    onResult("CANCELLED");
                    return;
                }
            }
        }
        try {
            await navigator.clipboard.writeText(url);
            onResult("COPIED");
        } catch (_) {
            onResult("FAILED");
        }
    };
})();
