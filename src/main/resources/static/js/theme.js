/**
 * Dark mode toggle. Runs immediately (not on DOMContentLoaded) so the
 * saved preference is applied before the page paints - otherwise you'd
 * see a flash of the light theme first on every reload. Falls back to
 * the OS/browser preference (prefers-color-scheme, handled in style.css)
 * when nothing has been chosen manually yet.
 */
(function () {
  function getStored() {
    try {
      return localStorage.getItem("optlink-theme");
    } catch (e) {
      return null;
    }
  }

  function setStored(value) {
    try {
      localStorage.setItem("optlink-theme", value);
    } catch (e) {
      // Private browsing / blocked storage - the toggle still works for
      // this page view, it just won't be remembered next time.
    }
  }

  function systemPrefersDark() {
    return window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
  }

  function isDark() {
    var explicit = document.documentElement.getAttribute("data-theme");
    if (explicit === "dark") return true;
    if (explicit === "light") return false;
    return systemPrefersDark();
  }

  function apply(theme) {
    if (theme === "dark" || theme === "light") {
      document.documentElement.setAttribute("data-theme", theme);
    } else {
      document.documentElement.removeAttribute("data-theme");
    }
  }

  function updateButton() {
    var btn = document.getElementById("theme-toggle");
    if (!btn) return;
    var dark = isDark();
    btn.textContent = dark ? "☀️" : "🌙";
    btn.setAttribute("aria-label", dark ? "Switch to light mode" : "Switch to dark mode");
  }

  // Apply the stored choice as early as possible.
  apply(getStored());

  document.addEventListener("DOMContentLoaded", function () {
    updateButton();
    var btn = document.getElementById("theme-toggle");
    if (!btn) return;
    btn.addEventListener("click", function () {
      var next = isDark() ? "light" : "dark";
      apply(next);
      setStored(next);
      updateButton();
    });
  });
})();
