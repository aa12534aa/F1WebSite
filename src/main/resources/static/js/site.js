(function () {
    function currentToken() {
        return localStorage.getItem('token');
    }

    function authHeaders() {
        const headers = {};
        const token = currentToken();
        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }
        return headers;
    }

    function updateAuthButton() {
        const authButton = document.getElementById('auth-btn');
        if (!authButton) {
            return;
        }
        authButton.innerText = currentToken() ? 'Logout' : 'Login';
    }

    function handleAuthAction() {
        localStorage.removeItem('token');
        window.location.href = '/login';
    }

    function setText(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.innerText = value;
        }
    }

    function setHtml(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.innerHTML = value;
        }
    }

    function setHref(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.href = value;
        }
    }

    function show(id) {
        const element = document.getElementById(id);
        if (element) {
            element.hidden = false;
        }
    }

    function hide(id) {
        const element = document.getElementById(id);
        if (element) {
            element.hidden = true;
        }
    }

    function setMessage(id, value, variant) {
        const element = document.getElementById(id);
        if (!element) {
            return;
        }
        element.innerText = value;
        element.className = 'message';
        if (variant) {
            element.classList.add(`message--${variant}`);
        }
    }

    function escapeHtml(value) {
        return String(value ?? '')
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function renderPosition(position) {
        const numericPosition = Number(position);

        if (!Number.isFinite(numericPosition) || numericPosition <= 0) {
            return '-';
        }

        if (numericPosition === 1) {
            return '<span class="position-badge position-badge--gold">P1</span>';
        }

        if (numericPosition === 2) {
            return '<span class="position-badge position-badge--silver">P2</span>';
        }

        if (numericPosition === 3) {
            return '<span class="position-badge position-badge--bronze">P3</span>';
        }

        return `P${numericPosition}`;
    }

    function isUnauthorized(response) {
        return response.status === 401 || response.status === 403;
    }

    async function fetchJson(url) {
        const response = await fetch(url, { headers: authHeaders() });
        if (isUnauthorized(response)) {
            handleAuthAction();
            throw new Error('Unauthorized');
        }
        if (!response.ok) {
            throw new Error(`Request failed for ${url}`);
        }
        return response.json();
    }

    function initRegistryPage(config) {
        const searchInput = document.getElementById(config.searchInputId);
        const searchButton = document.getElementById(config.searchButtonId);
        const prevButton = document.getElementById(config.prevButtonId);
        const nextButton = document.getElementById(config.nextButtonId);
        const pageInfo = document.getElementById(config.pageInfoId);
        const state = {
            currentPage: 0,
            currentSearchQuery: ''
        };
        const pageSize = config.pageSize ?? 50;

        async function loadPage(page) {
            updateAuthButton();

            const params = new URLSearchParams({
                page: String(page),
                size: String(pageSize)
            });

            if (state.currentSearchQuery && config.searchParamName) {
                params.set(config.searchParamName, state.currentSearchQuery);
            }

            try {
                const data = await fetchJson(`${config.apiBase}?${params.toString()}`);
                const content = Array.isArray(data.content) ? data.content : [];
                state.currentPage = typeof data.number === 'number' ? data.number : page;

                if (content.length === 0) {
                    setHtml(config.bodyId, `<tr><td colspan="${config.colspan}" class="empty-state">${escapeHtml(config.emptyMessage(state.currentSearchQuery))}</td></tr>`);
                    if (pageInfo) {
                        pageInfo.innerText = 'Page 0 of 0';
                    }
                    if (prevButton) {
                        prevButton.disabled = true;
                    }
                    if (nextButton) {
                        nextButton.disabled = true;
                    }
                } else {
                    const baseRank = state.currentPage * pageSize;
                    setHtml(config.bodyId, content.map((item, index) => config.renderRow(item, index, baseRank)).join(''));
                    if (pageInfo) {
                        pageInfo.innerText = `Page ${state.currentPage + 1} of ${data.totalPages}`;
                    }
                    if (prevButton) {
                        prevButton.disabled = !!data.first;
                    }
                    if (nextButton) {
                        nextButton.disabled = !!data.last;
                    }
                }

                hide(config.statusMessageId);
                show(config.dataContainerId);
            } catch (error) {
                console.error(error);
                setText(config.statusMessageId, 'Error loading data.');
                show(config.statusMessageId);
                hide(config.dataContainerId);
            }
        }

        function executeSearch() {
            if (searchInput) {
                state.currentSearchQuery = searchInput.value.trim();
            }
            setText(config.statusMessageId, 'Searching...');
            show(config.statusMessageId);
            hide(config.dataContainerId);
            loadPage(0);
        }

        if (searchInput) {
            searchInput.addEventListener('keydown', (event) => {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    executeSearch();
                }
            });
        }

        if (searchButton) {
            searchButton.addEventListener('click', executeSearch);
        }

        if (prevButton) {
            prevButton.addEventListener('click', () => {
                loadPage(Math.max(0, state.currentPage - 1));
            });
        }

        if (nextButton) {
            nextButton.addEventListener('click', () => {
                loadPage(state.currentPage + 1);
            });
        }

        loadPage(0);
    }

    async function initHomePage() {
        updateAuthButton();

        try {
            const data = await fetchJson('/api/home');

            const driversHtml = (data.bestDrivers || []).map((driver, index) => `
                <tr>
                    <td><strong>${index + 1}</strong></td>
                    <td><a href="/drivers/${driver.driverId}" class="driver-link">${escapeHtml(driver.name)}</a></td>
                    <td class="text-right">${driver.numOfWins}</td>
                    <td class="text-right">${driver.numOfRaces}</td>
                </tr>
            `).join('');

            const constructorsHtml = (data.bestConstructors || []).map((constructor, index) => `
                <tr>
                    <td><strong>${index + 1}</strong></td>
                    <td><a href="/constructors/${constructor.constructorId}" class="driver-link">${escapeHtml(constructor.name)}</a></td>
                    <td class="text-right">${constructor.numOfWins}</td>
                    <td class="text-right">${constructor.numOfRaces}</td>
                </tr>
            `).join('');

            setHtml('top-drivers-body', driversHtml);
            setHtml('top-constructors-body', constructorsHtml);
            hide('status-message');
            show('dashboard-content');
        } catch (error) {
            console.error(error);
            setText('status-message', 'Error loading dashboard data.');
        }
    }

    function initDriversPage() {
        initRegistryPage({
            apiBase: '/api/drivers',
            bodyId: 'all-drivers-body',
            statusMessageId: 'status-message',
            dataContainerId: 'data-container',
            searchInputId: 'search-input',
            searchButtonId: 'search-btn',
            prevButtonId: 'prev-btn',
            nextButtonId: 'next-btn',
            pageInfoId: 'page-info',
            searchParamName: 'searchName',
            colspan: 5,
            emptyMessage: (query) => `No drivers found matching "${query}".`,
            renderRow: (driver, index, baseRank) => `
                <tr>
                    <td><strong>${baseRank + index + 1}</strong></td>
                    <td><a href="/drivers/${driver.driverId}" class="driver-link">${escapeHtml(driver.name)}</a></td>
                    <td class="text-right">${driver.numOfWins}</td>
                    <td class="text-right">${driver.numOfRaces}</td>
                    <td class="text-right"><a class="page-btn page-btn--inline" href="/drivers/${driver.driverId}">Profile</a></td>
                </tr>
            `
        });
    }

    function initConstructorsPage() {
        initRegistryPage({
            apiBase: '/api/constructors',
            bodyId: 'all-constructors-body',
            statusMessageId: 'status-message',
            dataContainerId: 'data-container',
            searchInputId: 'search-input',
            searchButtonId: 'search-btn',
            prevButtonId: 'prev-btn',
            nextButtonId: 'next-btn',
            pageInfoId: 'page-info',
            searchParamName: 'searchName',
            colspan: 5,
            emptyMessage: (query) => `No constructors found matching "${query}".`,
            renderRow: (constructor, index, baseRank) => `
                <tr>
                    <td><strong>${baseRank + index + 1}</strong></td>
                    <td><a href="/constructors/${constructor.constructorId}" class="constructor-link">${escapeHtml(constructor.name)}</a></td>
                    <td class="text-right">${constructor.numOfWins}</td>
                    <td class="text-right">${constructor.numOfRaces}</td>
                    <td class="text-right"><a class="page-btn page-btn--inline" href="/constructors/${constructor.constructorId}">Profile</a></td>
                </tr>
            `
        });
    }

    function initCircuitsPage() {
        initRegistryPage({
            apiBase: '/api/circuits',
            bodyId: 'circuits-body',
            statusMessageId: 'status-message',
            dataContainerId: 'data-container',
            searchInputId: 'search-input',
            searchButtonId: 'search-btn',
            prevButtonId: 'prev-btn',
            nextButtonId: 'next-btn',
            pageInfoId: 'page-info',
            searchParamName: 'searchName',
            colspan: 4,
            emptyMessage: (query) => `No circuits found matching "${query}".`,
            renderRow: (circuit, index, baseRank) => `
                <tr>
                    <td><strong>${baseRank + index + 1}</strong></td>
                    <td><a href="/circuits/${circuit.circuitId}" class="circuit-link">${escapeHtml(circuit.name)}</a></td>
                    <td>${escapeHtml(circuit.country)}</td>
                    <td class="text-right">${circuit.numOfRaces}</td>
                </tr>
            `
        });
    }

    function initRacesPage() {
        initRegistryPage({
            apiBase: '/api/races',
            bodyId: 'all-races-body',
            statusMessageId: 'status-message',
            dataContainerId: 'data-container',
            searchInputId: 'search-input',
            searchButtonId: 'search-btn',
            prevButtonId: 'prev-btn',
            nextButtonId: 'next-btn',
            pageInfoId: 'page-info',
            searchParamName: 'searchName',
            colspan: 7,
            emptyMessage: (query) => `No races found matching "${query}".`,
            renderRow: (race, index, baseRank) => `
                <tr>
                    <td style="white-space: nowrap;">${escapeHtml(race.date || '-')}</td>
                    <td><a href="/races/${race.raceId}" class="race-link">${escapeHtml(race.raceName)}</a></td>
                    <td>${escapeHtml(race.circuitName || '-')}</td>
                    <td>${escapeHtml(race.country || '-')}</td>
                    <td>${escapeHtml(race.winnerName || '-')}</td>
                    <td>${escapeHtml(race.constructorName || '-')}</td>
                    <td class="text-right"><a class="page-btn page-btn--inline" href="/races/${race.raceId}">Details</a></td>
                </tr>
            `
        });
    }

    async function initDriverDetailsPage() {
        updateAuthButton();

        const driverId = document.body.dataset.driverId;
        setHref('races-link', `/drivers/${driverId}/races`);

        try {
            const driver = await fetchJson(`/api/drivers/${driverId}`);

            setText('driver-name', driver.name);
            setText('driver-nat', driver.nationality ? `Nationality: ${driver.nationality}` : '');
            setText('stat-races', driver.numOfRaces);
            setText('stat-points', driver.gainedPoints);
            setText('stat-poles', driver.numOfPolePosition);
            setText('stat-wins', driver.firstPlaces);
            setText('p1-count', driver.firstPlaces);
            setText('p2-count', driver.secondPlaces);
            setText('p3-count', driver.thirdPlaces);

            if (driver.url) {
                setHref('wiki-url', driver.url);
            } else {
                hide('wiki-url');
            }

            if (Array.isArray(driver.bestCircuits) && driver.bestCircuits.length > 0) {
                setHtml('best-circuits-body', driver.bestCircuits.map((circuit) => `
                    <tr>
                        <td>
                            <a href="/circuits/${circuit.circuitId}" class="circuit-link">${escapeHtml(circuit.circuitName)}</a>
                        </td>
                        <td>${escapeHtml(circuit.country)}</td>
                        <td>${circuit.numOfWins}</td>
                    </tr>
                `).join(''));
            }
        } catch (error) {
            console.error(error);
            setText('driver-name', 'Error loading driver profile.');
        }
    }

    async function initConstructorDetailsPage() {
        updateAuthButton();

        const constructorId = document.body.dataset.constructorId;
        setHref('races-link', `/constructors/${constructorId}/races`);

        try {
            const constructor = await fetchJson(`/api/constructors/${constructorId}`);

            setText('constructor-name', constructor.name);
            if (constructor.nationality) {
                setText('constructor-nat', `Nationality: ${constructor.nationality}`);
            }
            setText('stat-races', constructor.numOfRaces);
            setText('stat-points', constructor.gainedPoints);
            setText('stat-wins', constructor.firstPlaces);
            setText('p1-count', constructor.firstPlaces);
            setText('p2-count', constructor.secondPlaces);
            setText('p3-count', constructor.thirdPlaces);

            if (constructor.url) {
                setHref('wiki-url', constructor.url);
            } else {
                hide('wiki-url');
            }
        } catch (error) {
            console.error(error);
            setText('constructor-name', 'Error loading constructor profile.');
        }
    }

    async function initCircuitDetailsPage() {
        updateAuthButton();

        const circuitId = document.body.dataset.circuitId;

        try {
            const circuit = await fetchJson(`/api/circuits/${circuitId}`);

            setText('circuit-name', circuit.circuitName);
            setText('circuit-country', circuit.country);
            setText('circuit-races', circuit.numOfRaces);

            if (circuit.url) {
                setHref('circuit-url', circuit.url);
            } else {
                hide('circuit-url');
            }

            if (Array.isArray(circuit.bestDrivers) && circuit.bestDrivers.length > 0) {
                setHtml('drivers-body', circuit.bestDrivers.map((driver, index) => `
                    <tr>
                        <td class="text-center"><strong>${index + 1}</strong></td>
                        <td><a href="/drivers/${driver.driverId}" class="driver-link">${escapeHtml(driver.driverName)}</a></td>
                        <td>${escapeHtml(driver.nationality)}</td>
                        <td class="text-right"><strong>${driver.numOfWins}</strong></td>
                    </tr>
                `).join(''));
            } else {
                setHtml('drivers-body', '<tr><td colspan="4" class="text-center empty-state">No driver data available for this circuit.</td></tr>');
            }

            hide('status-message');
            show('data-container');
        } catch (error) {
            console.error(error);
            setText('status-message', 'Error loading data.');
        }
    }

    async function initRaceDetailsPage() {
        updateAuthButton();

        const raceId = document.body.dataset.raceId;
        setHref('btn-results', `/races/${raceId}/results`);
        setHref('btn-qualifying', `/races/${raceId}/qualifying`);

        try {
            const raceData = await fetchJson(`/api/races/${raceId}`);

            setText('race-name', raceData.name);
            setText('race-date', raceData.date || 'Unknown date');
            if (raceData.circuit) {
                setText('race-country', raceData.circuit.country || '');
                setText('race-circuit', raceData.circuit.name || '');
                setHref('race-circuit', `/circuits/${raceData.circuit.circuitId}`);
            }

            hide('status-message');
            show('data-container');
        } catch (error) {
            console.error(error);
            setText('status-message', 'Error loading data.');
        }
    }

    async function initDriverRacesPage() {
        updateAuthButton();

        const driverId = document.body.dataset.driverId;
        const backLink = document.getElementById('profile-back-link');
        if (backLink) {
            backLink.href = `/drivers/${driverId}`;
        }

        try {
            const data = await fetchJson(`/api/drivers/${driverId}/races?page=0&size=50`);
            const content = Array.isArray(data.content) ? data.content : [];
            if (content.length > 0) {
                setText('page-title', `Race History: ${content[0].name}`);
            }

            setHtml('races-body', content.map((race) => {
                const positionDisplay = renderPosition(race.position);
                const circuitLabel = race.country || '-';
                return `
                    <tr>
                        <td>${escapeHtml(race.date)}</td>
                        <td><a href="/races/${race.raceId}" class="race-link">${escapeHtml(race.raceName)}</a></td>
                        <td>${escapeHtml(circuitLabel)}</td>
                        <td><a href="/constructors/${race.constructorId}" class="constructor-link">${escapeHtml(race.team)}</a></td>
                        <td class="text-center">${escapeHtml(race.grid)}</td>
                        <td class="text-center"><strong>${positionDisplay}</strong></td>
                        <td class="text-center">+${race.points}</td>
                    </tr>
                `;
            }).join(''));

            setText('page-info', `Page ${((data.number ?? 0) + 1)} of ${data.totalPages}`);
            const prevButton = document.getElementById('prev-btn');
            const nextButton = document.getElementById('next-btn');
            if (prevButton) {
                prevButton.disabled = !!data.first;
            }
            if (nextButton) {
                nextButton.disabled = !!data.last;
            }
        } catch (error) {
            console.error(error);
            setHtml('races-body', '<tr><td colspan="7" class="empty-state">Error loading data or unauthorized context.</td></tr>');
        }
    }

    async function initConstructorRacesPage() {
        updateAuthButton();

        const constructorId = document.body.dataset.constructorId;
        const backLink = document.getElementById('profile-back-link');
        if (backLink) {
            backLink.href = `/constructors/${constructorId}`;
        }

        try {
            const data = await fetchJson(`/api/constructors/${constructorId}/races?page=0&size=50`);
            const content = Array.isArray(data.content) ? data.content : [];
            if (content.length > 0 && content[0].teamName) {
                setText('page-title', `Race History: ${content[0].teamName}`);
            }

            setHtml('races-body', content.map((race) => {
                const driverName = race.driverName || race.driver || 'Unknown Driver';
                const circuitLabel = race.country || '-';
                const position = renderPosition(race.position);

                return `
                    <tr>
                        <td>${escapeHtml(race.date || '-')}</td>
                        <td><a href="/races/${race.raceId}" class="race-link">${escapeHtml(race.raceName || '-')}</a></td>
                        <td>${escapeHtml(circuitLabel)}</td>
                        <td><a href="/drivers/${race.driverId}" class="driver-link">${escapeHtml(driverName)}</a></td>
                        <td class="text-center"><strong>${position}</strong></td>
                        <td class="text-center">+${race.points !== undefined ? race.points : '0'}</td>
                    </tr>
                `;
            }).join(''));

            setText('page-info', `Page ${((data.number ?? 0) + 1)} of ${data.totalPages}`);
            const prevButton = document.getElementById('prev-btn');
            const nextButton = document.getElementById('next-btn');
            if (prevButton) {
                prevButton.disabled = !!data.first;
            }
            if (nextButton) {
                nextButton.disabled = !!data.last;
            }
        } catch (error) {
            console.error(error);
            setHtml('races-body', '<tr><td colspan="6" class="empty-state">Error loading data or unauthorized context.</td></tr>');
        }
    }

    async function initRaceResultsPage() {
        updateAuthButton();

        const raceId = document.body.dataset.raceId;
        setHref('back-link', `/races/${raceId}`);

        try {
            const [raceData, resultsData] = await Promise.all([
                fetchJson(`/api/races/${raceId}`),
                fetchJson(`/api/races/${raceId}/results`)
            ]);

            setText('race-name', raceData.name);
            setHtml('results-body', (resultsData || []).map((result) => `
                <tr>
                    <td class="text-center"><strong>${escapeHtml(result.position || '-')}</strong></td>
                    <td class="text-center">${escapeHtml(result.grid || '-')}</td>
                    <td><a href="/drivers/${result.driver.driverId}" class="driver-link">${escapeHtml(result.driver.name)}</a></td>
                    <td>${escapeHtml(result.driver.nationality)}</td>
                    <td><a href="/constructors/${result.constructor.constructorId}" class="constructor-link">${escapeHtml(result.constructor.name)}</a></td>
                    <td class="text-right">+${result.points || '0'}</td>
                </tr>
            `).join('') || '<tr><td colspan="6" class="text-center empty-state">No results data available.</td></tr>');

            hide('status-message');
            show('data-container');
        } catch (error) {
            console.error(error);
            setText('status-message', 'Error loading data.');
        }
    }

    async function initRaceQualifyingPage() {
        updateAuthButton();

        const raceId = document.body.dataset.raceId;
        setHref('back-link', `/races/${raceId}`);

        try {
            const [raceData, qualifyingData] = await Promise.all([
                fetchJson(`/api/races/${raceId}`),
                fetchJson(`/api/races/${raceId}/qualifying`)
            ]);

            setText('race-name', raceData.name);
            setHtml('qualifying-body', (qualifyingData || []).map((qualifying) => `
                <tr>
                    <td class="text-center"><strong>${escapeHtml(qualifying.position || '-')}</strong></td>
                    <td><a href="/drivers/${qualifying.driver.driverId}" class="driver-link">${escapeHtml(qualifying.driver.name)}</a></td>
                    <td>${escapeHtml(qualifying.driver.nationality)}</td>
                    <td><a href="/constructors/${qualifying.constructor.constructorId}" class="constructor-link">${escapeHtml(qualifying.constructor.name)}</a></td>
                </tr>
            `).join('') || '<tr><td colspan="4" class="text-center empty-state">No qualifying data available.</td></tr>');

            hide('status-message');
            show('data-container');
        } catch (error) {
            console.error(error);
            setText('status-message', 'Error loading data.');
        }
    }

    function initLoginPage() {
        const form = document.getElementById('loginForm');
        if (!form) {
            return;
        }

        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            const usernameInput = document.getElementById('username').value;
            const passwordInput = document.getElementById('password').value;

            setMessage('message', 'Logging in...', 'info');

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username: usernameInput, password: passwordInput })
                });

                if (response.ok) {
                    const data = await response.json();
                    localStorage.setItem('token', data.accessToken);
                    window.location.href = '/home';
                } else {
                    setMessage('message', 'Invalid email or password.', 'error');
                }
            } catch (error) {
                console.error(error);
                setMessage('message', 'Server connection error!', 'error');
            }
        });
    }

    function initRegisterPage() {
        const form = document.getElementById('registerForm');
        if (!form) {
            return;
        }

        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            const registerData = {
                firstname: document.getElementById('firstname').value,
                lastname: document.getElementById('lastname').value,
                username: document.getElementById('username').value,
                password: document.getElementById('password').value
            };

            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(registerData)
                });

                if (response.ok) {
                    setMessage('message', 'Account created successfully!', 'success');
                    form.reset();
                } else {
                    setMessage('message', 'Registration failed. Email might be taken.', 'error');
                }
            } catch (error) {
                console.error(error);
                setMessage('message', 'Cannot connect to server!', 'error');
            }
        });
    }

    function bootstrap() {
        updateAuthButton();

        const authButton = document.getElementById('auth-btn');
        if (authButton) {
            authButton.addEventListener('click', handleAuthAction);
        }

        switch (document.body.dataset.page) {
            case 'home':
                initHomePage();
                break;
            case 'drivers':
                initDriversPage();
                break;
            case 'constructors':
                initConstructorsPage();
                break;
            case 'circuits':
                initCircuitsPage();
                break;
            case 'races':
                initRacesPage();
                break;
            case 'driver-details':
                initDriverDetailsPage();
                break;
            case 'constructor-details':
                initConstructorDetailsPage();
                break;
            case 'circuit-details':
                initCircuitDetailsPage();
                break;
            case 'race-details':
                initRaceDetailsPage();
                break;
            case 'driver-races':
                initDriverRacesPage();
                break;
            case 'constructor-races':
                initConstructorRacesPage();
                break;
            case 'race-results':
                initRaceResultsPage();
                break;
            case 'race-qualifying':
                initRaceQualifyingPage();
                break;
            case 'login':
                initLoginPage();
                break;
            case 'register':
                initRegisterPage();
                break;
            default:
                break;
        }
    }

    window.handleAuthAction = handleAuthAction;
    window.updateAuthButton = updateAuthButton;
    window.logout = handleAuthAction;

    document.addEventListener('DOMContentLoaded', bootstrap);
})();
