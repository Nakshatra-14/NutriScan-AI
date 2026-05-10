(function () {
    'use strict';

    const THEME_KEY = 'foodsense-theme';
    const USER_KEY = 'foodsense-user';

    const html = document.documentElement;

    function getStoredTheme() {
        return localStorage.getItem(THEME_KEY);
    }

    function setTheme(theme) {
        if (theme === 'dark') {
            html.setAttribute('data-theme', 'dark');
        } else {
            html.removeAttribute('data-theme');
        }
        localStorage.setItem(THEME_KEY, theme === 'dark' ? 'dark' : 'light');
        updateThemeToggleLabel();
    }

    function updateThemeToggleLabel() {
        const btn = document.getElementById('theme-toggle');
        if (!btn) return;
        const isDark = html.getAttribute('data-theme') === 'dark';
        btn.setAttribute('aria-label', isDark ? 'Switch to light mode' : 'Switch to dark mode');
        btn.setAttribute('title', isDark ? 'Light mode' : 'Dark mode');
        const icon = btn.querySelector('i');
        if (icon) {
            icon.className = isDark ? 'fas fa-sun' : 'fas fa-moon';
        }
    }

    function initTheme() {
        const stored = getStoredTheme();
        if (stored === 'dark') {
            setTheme('dark');
        } else if (stored === 'light') {
            setTheme('light');
        } else if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
            setTheme('dark');
        } else {
            setTheme('light');
        }
    }

    function openAuthModal(mode) {
        const overlay = document.getElementById('auth-modal-overlay');
        if (!overlay) return;
        overlay.hidden = false;
        overlay.classList.add('is-open');
        document.body.style.overflow = 'hidden';
        switchAuthTab(mode === 'signup' ? 'signup' : 'signin');
        const firstInput = overlay.querySelector(
            mode === 'signup' ? '#signup-name' : '#signin-email'
        );
        if (firstInput) setTimeout(() => firstInput.focus(), 100);
    }

    function closeAuthModal() {
        const overlay = document.getElementById('auth-modal-overlay');
        if (!overlay) return;
        overlay.classList.remove('is-open');
        overlay.hidden = true;
        document.body.style.overflow = '';
        clearAuthMessages();
        document.getElementById('form-signin')?.reset();
        document.getElementById('form-signup')?.reset();
    }

    function switchAuthTab(tab) {
        const signinPanel = document.getElementById('auth-panel-signin');
        const signupPanel = document.getElementById('auth-panel-signup');
        const tabs = document.querySelectorAll('.auth-tab');
        tabs.forEach((t) => {
            const isSignin = t.dataset.tab === 'signin';
            const active = (tab === 'signin' && isSignin) || (tab === 'signup' && !isSignin);
            t.classList.toggle('is-active', active);
            t.setAttribute('aria-selected', active ? 'true' : 'false');
        });
        if (signinPanel) {
            signinPanel.hidden = tab !== 'signin';
        }
        if (signupPanel) {
            signupPanel.hidden = tab !== 'signup';
        }
        clearAuthMessages();
    }

    function clearAuthMessages() {
        document.querySelectorAll('.auth-message').forEach((el) => {
            el.textContent = '';
            el.classList.remove('is-error', 'is-success');
        });
    }

    function showAuthMessage(formId, text, type) {
        const form = document.getElementById(formId);
        if (!form) return;
        const msg = form.querySelector('.auth-message');
        if (!msg) return;
        msg.textContent = text;
        msg.classList.remove('is-error', 'is-success');
        msg.classList.add(type === 'error' ? 'is-error' : 'is-success');
    }

    function setUserSession(user) {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        updateUserUi();
    }

    function clearUserSession() {
        localStorage.removeItem(USER_KEY);
        updateUserUi();
    }

    function getUser() {
        try {
            const raw = localStorage.getItem(USER_KEY);
            return raw ? JSON.parse(raw) : null;
        } catch {
            return null;
        }
    }

    function updateUserUi() {
        const user = getUser();
        const guest = document.getElementById('nav-auth-guest');
        const logged = document.getElementById('nav-auth-user');
        const nameEl = document.getElementById('nav-user-name');
        if (guest && logged) {
            guest.hidden = !!user;
            logged.hidden = !user;
        }
        if (nameEl && user && user.name) {
            nameEl.textContent = user.name.split(' ')[0] || user.email;
        }
    }

    function handleSignIn(e) {
        e.preventDefault();
        const email = document.getElementById('signin-email').value.trim();
        const password = document.getElementById('signin-password').value;
        if (!email || !password) {
            showAuthMessage('form-signin', 'Please enter email and password.', 'error');
            return;
        }
        const usersRaw = localStorage.getItem('foodsense-users');
        let users = [];
        try {
            users = usersRaw ? JSON.parse(usersRaw) : [];
        } catch {
            users = [];
        }
        const match = users.find((u) => u.email === email && u.password === password);
        if (match) {
            setUserSession({ name: match.name, email: match.email });
            showAuthMessage('form-signin', 'Signed in successfully.', 'success');
            setTimeout(() => closeAuthModal(), 600);
        } else {
            showAuthMessage(
                'form-signin',
                'Invalid credentials or account not found. Try signing up first.',
                'error'
            );
        }
    }

    function handleSignUp(e) {
        e.preventDefault();
        const name = document.getElementById('signup-name').value.trim();
        const email = document.getElementById('signup-email').value.trim();
        const password = document.getElementById('signup-password').value;
        const confirm = document.getElementById('signup-confirm').value;
        if (!name || !email || !password) {
            showAuthMessage('form-signup', 'Please fill in all fields.', 'error');
            return;
        }
        if (password.length < 6) {
            showAuthMessage('form-signup', 'Password must be at least 6 characters.', 'error');
            return;
        }
        if (password !== confirm) {
            showAuthMessage('form-signup', 'Passwords do not match.', 'error');
            return;
        }
        let users = [];
        try {
            const raw = localStorage.getItem('foodsense-users');
            users = raw ? JSON.parse(raw) : [];
        } catch {
            users = [];
        }
        if (!Array.isArray(users)) users = [];
        if (users.some((u) => u.email === email)) {
            showAuthMessage('form-signup', 'An account with this email already exists.', 'error');
            return;
        }
        users.push({ name, email, password });
        localStorage.setItem('foodsense-users', JSON.stringify(users));
        setUserSession({ name, email });
        showAuthMessage('form-signup', 'Account created. You are signed in.', 'success');
        setTimeout(() => closeAuthModal(), 600);
    }

    function scrollToTarget(selector) {
        const el = document.querySelector(selector);
        if (el) {
            el.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }

    function bindNavScroll() {
        document.querySelectorAll('[data-scroll-target]').forEach((link) => {
            link.addEventListener('click', (e) => {
                const target = link.getAttribute('data-scroll-target');
                if (target && target.startsWith('#')) {
                    e.preventDefault();
                    scrollToTarget(target);
                }
            });
        });
    }

    function bindButtons() {
        document.querySelectorAll('[data-open-auth]').forEach((btn) => {
            btn.addEventListener('click', () => {
                const mode = btn.getAttribute('data-open-auth') || 'signin';
                openAuthModal(mode);
            });
        });

        const themeToggle = document.getElementById('theme-toggle');
        if (themeToggle) {
            themeToggle.addEventListener('click', () => {
                const isDark = html.getAttribute('data-theme') === 'dark';
                setTheme(isDark ? 'light' : 'dark');
            });
        }

        const signOutBtn = document.getElementById('nav-sign-out');
        if (signOutBtn) {
            signOutBtn.addEventListener('click', () => clearUserSession());
        }

        const overlay = document.getElementById('auth-modal-overlay');
        if (overlay) {
            overlay.addEventListener('click', (e) => {
                if (e.target === overlay) closeAuthModal();
            });
        }

        const closeBtn = document.getElementById('auth-modal-close');
        if (closeBtn) closeBtn.addEventListener('click', closeAuthModal);

        document.querySelectorAll('.auth-tab').forEach((tab) => {
            tab.addEventListener('click', () => {
                switchAuthTab(tab.dataset.tab === 'signup' ? 'signup' : 'signin');
            });
        });

        const formSignin = document.getElementById('form-signin');
        if (formSignin) formSignin.addEventListener('submit', handleSignIn);

        const formSignup = document.getElementById('form-signup');
        if (formSignup) formSignup.addEventListener('submit', handleSignUp);

        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                const o = document.getElementById('auth-modal-overlay');
                if (o && o.classList.contains('is-open')) closeAuthModal();
            }
        });

        document.querySelectorAll('[data-footer-link]').forEach((a) => {
            a.addEventListener('click', (e) => {
                e.preventDefault();
                const action = a.getAttribute('data-footer-link');
                if (action === 'features') scrollToTarget('#features');
                else if (action === 'how') scrollToTarget('#how-it-works');
                else if (action === 'benefits') scrollToTarget('#benefits');
                else if (action === 'cta') scrollToTarget('#cta-section');
            });
        });
    }

    initTheme();
    bindNavScroll();
    bindButtons();
    updateUserUi();
})();
