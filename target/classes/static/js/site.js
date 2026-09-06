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

    function decodeJwtPayload(token) {
        if (!token) {
            return null;
        }

        try {
            const payload = token.split('.')[1];
            if (!payload) {
                return null;
            }

            const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
            const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
            const binary = atob(padded);
            return JSON.parse(decodeURIComponent(
                Array.from(binary).map((ch) => `%${(`00${ch.charCodeAt(0).toString(16)}`).slice(-2)}`).join('')
            ));
        } catch (error) {
            console.error('Unable to decode token payload', error);
            return null;
        }
    }

    function getUserRole() {
        const payload = decodeJwtPayload(currentToken());
        if (!payload) {
            return null;
        }

        if (payload.role) {
            return payload.role;
        }

        if (Array.isArray(payload.roles)) {
            return payload.roles[0] || null;
        }

        if (Array.isArray(payload.authorities)) {
            return payload.authorities[0] || null;
        }

        return null;
    }

    function isEmployeeUser() {
        const role = getUserRole();
        return role === 'ROLE_EMPLOYEE' || role === 'ROLE_EMPLOYEE ' || (typeof role === 'string' && role.includes('ROLE_EMPLOYEE'));
    }

    function showToast(message, variant = 'info') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast toast--${variant}`;
        toast.textContent = message;
        container.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, 3200);
    }

    async function parseApiError(response) {
        const defaultMessage = 'Request failed.';

        try {
            const responseText = await response.text();
            if (!responseText) {
                return defaultMessage;
            }

            const parsed = JSON.parse(responseText);
            if (parsed && parsed.message) {
                return parsed.message;
            }
            if (parsed && parsed.error) {
                return parsed.error;
            }

            return responseText;
        } catch (error) {
            return defaultMessage;
        }
    }

    function resolveRaceId(rawValue) {
        if (rawValue === undefined || rawValue === null || rawValue === '') {
            return null;
        }

        const parsed = Number(rawValue);
        if (!Number.isFinite(parsed) || parsed <= 0 || !Number.isInteger(parsed)) {
            return null;
        }

        return parsed;
    }

    function ensureCrudModal() {
        if (document.getElementById('entity-modal')) {
            return;
        }

        const modal = document.createElement('div');
        modal.id = 'entity-modal';
        modal.className = 'modal';
        modal.innerHTML = `
            <div class="modal__backdrop" data-close-modal="true"></div>
            <div class="modal__panel" role="dialog" aria-modal="true" aria-labelledby="crud-modal-title">
                <div class="modal__header">
                    <h2 id="crud-modal-title">Create</h2>
                    <button type="button" class="modal__close" data-close-modal="true" aria-label="Close">&times;</button>
                </div>
                <form id="crud-form">
                    <div id="crud-form-fields" class="form-grid"></div>
                    <div class="modal__footer">
                        <button type="button" class="page-btn" data-close-modal="true">Cancel</button>
                        <button type="submit" class="btn-primary" id="crud-submit-btn">Save</button>
                    </div>
                </form>
            </div>
        `;

        document.body.appendChild(modal);

        const form = modal.querySelector('#crud-form');
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            const entity = form.dataset.entity;
            const mode = form.dataset.mode || 'create';
            const submitButton = document.getElementById('crud-submit-btn');

            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            const payload = Object.fromEntries(new FormData(form).entries());
            const normalizedPayload = normalizeCrudPayload(entity, payload);

            if (!entity) {
                return;
            }

            let actionUrl;
            if (entity === 'results' || entity === 'qualifying' || entity === 'sprintResults') {
                const raceId = resolveRaceId(form.dataset.raceId || document.body.dataset.raceId);
                if (!raceId) {
                    showToast('Invalid race context. Please open this page from a real race record.', 'error');
                    return;
                }
                const routeName = entity === 'results'
                    ? 'results'
                    : entity === 'qualifying' ? 'qualifying' : 'sprint-results';
                actionUrl = mode === 'edit'
                    ? `/api/races/${raceId}/${routeName}/${form.dataset.itemId}`
                    : `/api/races/${raceId}/${routeName}`;
            } else {
                const endpointConfig = {
                    drivers: {
                        create: '/api/drivers',
                        update: (id) => `/api/drivers/${id}`
                    },
                    constructors: {
                        create: '/api/constructors',
                        update: (id) => `/api/constructors/${id}`
                    },
                    circuits: {
                        create: '/api/circuits',
                        update: (id) => `/api/circuits/${id}`
                    },
                    races: {
                        create: '/api/races',
                        update: (id) => `/api/races/${id}`
                    }
                }[entity];

                actionUrl = mode === 'edit'
                    ? endpointConfig.update(form.dataset.itemId)
                    : endpointConfig.create;
            }

            submitButton.disabled = true;
            submitButton.textContent = 'Saving...';

            try {
                const response = await fetch(actionUrl, {
                    method: mode === 'edit' ? 'PUT' : 'POST',
                    headers: {
                        ...authHeaders(),
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(normalizedPayload)
                });

                if (isUnauthorized(response)) {
                    handleAuthAction();
                    throw new Error('Unauthorized');
                }

                if (!response.ok) {
                    const errorMessage = await parseApiError(response);
                    throw new Error(errorMessage || 'API request failed.');
                }

                showToast(mode === 'edit' ? 'Updated successfully.' : 'Created successfully.', 'success');
                document.dispatchEvent(new CustomEvent('f1:registry-refresh'));
                modal.classList.remove('is-open');
                form.reset();
            } catch (error) {
                console.error(error);
                showToast(error.message || 'Request failed.', 'error');
            } finally {
                submitButton.disabled = false;
                submitButton.textContent = 'Save';
            }
        });

        modal.addEventListener('click', (event) => {
            if (event.target instanceof HTMLElement && event.target.dataset.closeModal === 'true') {
                modal.classList.remove('is-open');
                const formElement = modal.querySelector('#crud-form');
                if (formElement) {
                    formElement.reset();
                }
            }
        });
    }

    function normalizeCrudPayload(entity, payload) {
        const normalized = { ...payload };

        Object.keys(normalized).forEach((key) => {
            const value = normalized[key];
            if (value === null || value === undefined) {
                delete normalized[key];
            } else if (typeof value === 'string') {
                normalized[key] = value.trim();
            }
        });

        if (entity === 'drivers') {
            return {
                name: normalized.name,
                nationality: normalized.nationality,
                url: normalized.url
            };
        }

        if (entity === 'constructors') {
            return {
                name: normalized.name
            };
        }

        if (entity === 'circuits') {
            return {
                name: normalized.name,
                country: normalized.country,
                url: normalized.url
            };
        }

        if (entity === 'races') {
            return {
                name: normalized.name,
                date: normalized.date,
                circuitName: normalized.circuitName,
                sprintAppearance: payload.sprintAppearance === 'on'
            };
        }

        if (entity === 'results') {
            return {
                driverUrl: normalized.driverUrl,
                constructorName: normalized.constructorName,
                grid: normalized.grid !== undefined && normalized.grid !== '' ? Number(normalized.grid) : null,
                position: normalized.position !== undefined && normalized.position !== '' ? Number(normalized.position) : null,
                points: normalized.points !== undefined && normalized.points !== '' ? Number(normalized.points) : null
            };
        }

        if (entity === 'sprintResults') {
            return {
                driverUrl: normalized.driverUrl,
                constructorName: normalized.constructorName,
                grid: normalized.grid !== undefined && normalized.grid !== '' ? Number(normalized.grid) : null,
                position: normalized.position !== undefined && normalized.position !== '' ? Number(normalized.position) : null,
                points: normalized.points !== undefined && normalized.points !== '' ? Number(normalized.points) : null
            };
        }

        if (entity === 'qualifying') {
            return {
                driverUrl: normalized.driverUrl,
                constructorName: normalized.constructorName,
                position: normalized.position !== undefined && normalized.position !== '' ? Number(normalized.position) : null
            };
        }

        return normalized;
    }

    function renderCrudField(field) {
        const wrapper = document.createElement('div');
        wrapper.className = 'form-field';

        const label = document.createElement('label');
        label.setAttribute('for', field.name);
        label.textContent = field.label;

        const input = document.createElement(field.type === 'textarea' ? 'textarea' : 'input');
        input.id = field.name;
        input.name = field.name;
        input.placeholder = field.placeholder || '';

        if (field.type === 'date') {
            input.type = 'date';
        } else if (field.type === 'url') {
            input.type = 'url';
        } else if (field.type === 'number') {
            input.type = 'number';
        } else if (field.type === 'checkbox') {
            input.type = 'checkbox';
        } else if (field.type && field.type !== 'textarea') {
            input.type = field.type;
        }

        if (field.type === 'checkbox') {
            input.checked = field.value === true || field.value === 'true';
            input.required = field.required === true;
            wrapper.style.display = 'flex';
            wrapper.style.flexDirection = 'row';
            wrapper.style.alignItems = 'center';
            wrapper.style.justifyContent = 'flex-start';
            input.style.flex = '1';
            label.style.flex = '2';
            input.style.margin = '0';
            label.style.margin = '0';
        } else {
            input.value = field.value ?? '';
            input.required = field.required !== false;
        }

        if (field.type === 'textarea') {
            input.rows = 3;
        }

        wrapper.appendChild(label);
        wrapper.appendChild(input);
        return wrapper;
    }

    function openCrudModal(entity, mode, item) {
        ensureCrudModal();

        const modal = document.getElementById('entity-modal');
        const form = document.getElementById('crud-form');
        const fieldsContainer = document.getElementById('crud-form-fields');
        const title = document.getElementById('crud-modal-title');

        const schema = {
            drivers: [
                { name: 'name', label: 'Driver name', placeholder: 'e.g. Max Verstappen', required: true },
                { name: 'nationality', label: 'Nationality', placeholder: 'e.g. Dutch', required: true },
                { name: 'url', label: 'Wikipedia URL', placeholder: 'https://...', required: true }
            ],
            constructors: [
                { name: 'name', label: 'Constructor name', placeholder: 'e.g. Red Bull', required: true }
            ],
            circuits: [
                { name: 'name', label: 'Circuit name', placeholder: 'e.g. Silverstone', required: true },
                { name: 'country', label: 'Country', placeholder: 'e.g. United Kingdom', required: true },
                { name: 'url', label: 'Website URL', placeholder: 'https://...', required: false }
            ],
            races: [
                { name: 'name', label: 'Race name', placeholder: 'e.g. British Grand Prix', required: true },
                { name: 'date', label: 'Race date', type: 'date', required: true },
                { name: 'circuitName', label: 'Circuit name', placeholder: 'e.g. Monaco', required: true },
                { name: 'sprintAppearance', label: 'Sprint appearance', type: 'checkbox' }
            ],
            results: [
                { name: 'driverUrl', label: 'Driver URL', placeholder: 'e.g. http://driver-url', required: true },
                { name: 'constructorName', label: 'Constructor name', placeholder: 'e.g. ferrari', required: true },
                { name: 'grid', label: 'Grid', type: 'number', required: true },
                { name: 'position', label: 'Position', type: 'number', required: true },
                { name: 'points', label: 'Points', type: 'number', required: true }
            ],
            sprintResults: [
                { name: 'driverUrl', label: 'Driver URL', placeholder: 'e.g. http://driver-url', required: true },
                { name: 'constructorName', label: 'Constructor name', placeholder: 'e.g. ferrari', required: true },
                { name: 'grid', label: 'Grid', type: 'number', required: true },
                { name: 'position', label: 'Position', type: 'number', required: true },
                { name: 'points', label: 'Points', type: 'number', required: true }
            ],
            qualifying: [
                { name: 'driverUrl', label: 'Driver URL', placeholder: 'e.g. http://driver-url', required: true },
                { name: 'constructorName', label: 'Constructor name', placeholder: 'e.g. ferrari', required: true },
                { name: 'position', label: 'Position', type: 'number', required: true }
            ]
        }[entity] || [];

        fieldsContainer.innerHTML = '';
        schema.forEach((field) => {
            const value = mode === 'edit' && item
                ? item[field.name] ?? (field.name === 'driverUrl' && item.driver ? item.driver.url : null) ?? (field.name === 'constructorName' && item.constructor ? item.constructor.name : null)
                : undefined;
            if (value !== undefined && value !== null) {
                field.value = value;
            }
            fieldsContainer.appendChild(renderCrudField(field));
        });

        form.dataset.entity = entity;
        form.dataset.mode = mode;
        form.dataset.itemId = item && item.id ? item.id : item && item.driverId ? item.driverId : item && item.constructorId ? item.constructorId : item && item.circuitId ? item.circuitId : item && item.raceId ? item.raceId : item && item.resultId ? item.resultId : item && item.qualifyingId ? item.qualifyingId : '';
        form.dataset.raceId = resolveRaceId(item && item.raceId ? item.raceId : document.body.dataset.raceId) ?? '';
        title.textContent = mode === 'edit' ? `Edit ${entity.slice(0, -1)}` : `Create ${entity.slice(0, -1)}`;
        modal.classList.add('is-open');
    }

    function syncEmployeeControls() {
        document.querySelectorAll('[data-employee-action]').forEach((button) => {
            button.hidden = !isEmployeeUser();
        });
    }

    function bindCrudRowActions() {
        const handleActionClick = async (event) => {
            const target = event.target instanceof HTMLElement ? event.target.closest('[data-action]') : null;
            if (!target) {
                return;
            }

            const { action, entity, id } = target.dataset;

            if (action === 'edit-row') {
                openCrudModal(entity, 'edit', { id, ...JSON.parse(target.dataset.item || '{}') });
                return;
            }

            if (action === 'delete-row') {
                const routeMap = {
                    drivers: `/api/drivers/${id}`,
                    constructors: `/api/constructors/${id}`,
                    circuits: `/api/circuits/${id}`,
                    races: `/api/races/${id}`,
                    results: `/api/races/${target.dataset.raceId}/results/${id}`,
                    qualifying: `/api/races/${target.dataset.raceId}/qualifying/${id}`,
                    sprintResults: `/api/races/${target.dataset.raceId}/sprint-results/${id}`
                };

                const confirmMessage = `Delete this ${entity.slice(0, -1)}? This action cannot be undone.`;
                if (!window.confirm(confirmMessage)) {
                    return;
                }

                try {
                    const response = await fetch(routeMap[entity], {
                        method: 'DELETE',
                        headers: authHeaders()
                    });

                    if (isUnauthorized(response)) {
                        handleAuthAction();
                        throw new Error('Unauthorized');
                    }

                    if (!response.ok) {
                        throw new Error('Unable to delete item.');
                    }

                    showToast('Deleted successfully.', 'success');
                    document.dispatchEvent(new CustomEvent('f1:registry-refresh'));
                } catch (error) {
                    console.error(error);
                    showToast(error.message || 'Delete failed.', 'error');
                }
            }

            if (action === 'create-row') {
                openCrudModal(entity, 'create', { raceId: target.dataset.raceId || document.body.dataset.raceId || '' });
            }
        };

        document.removeEventListener('click', handleActionClick);
        document.addEventListener('click', handleActionClick);

        document.querySelectorAll('[data-action]').forEach((button) => {
            button.onclick = null;
            button.addEventListener('click', handleActionClick, { once: false });
        });
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
            syncEmployeeControls();

            const params = new URLSearchParams({
                page: String(page),
                size: String(pageSize)
            });

            if (state.currentSearchQuery && config.searchParamName) {
                params.set(config.searchParamName, state.currentSearchQuery);
            }

            if (config.getExtraParams) {
                const extraParams = config.getExtraParams();
                Object.keys(extraParams).forEach(key => {
                    if (extraParams[key]) {
                        params.set(key, extraParams[key]);
                    }
                });
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

        if (config.filterElements) {
            config.filterElements.forEach(id => {
                const el = document.getElementById(id);
                if (el) {
                    el.addEventListener('change', () => {
                        executeSearch();
                    });
                }
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
            entity: 'drivers',
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
            lookupId: (driver) => driver.driverId,
            renderRow: (driver, index, baseRank) => `
                <tr>
                    <td><strong>${baseRank + index + 1}</strong></td>
                    <td><a href="/drivers/${driver.driverId}" class="driver-link">${escapeHtml(driver.name)}</a></td>
                    <td class="text-right">${driver.numOfWins}</td>
                    <td class="text-right">${driver.numOfRaces}</td>
                    <td class="text-right">
                        <div class="row-actions">
                            <a class="page-btn page-btn--inline" href="/drivers/${driver.driverId}">Profile</a>
                            ${isEmployeeUser() ? `
                                <button type="button" class="page-btn page-btn--inline" data-action="edit-row" data-entity="drivers" data-id="${driver.driverId}" data-item='${JSON.stringify(driver).replace(/'/g, '&apos;')}'>Edit</button>
                                <button type="button" class="page-btn page-btn--inline page-btn--danger" data-action="delete-row" data-entity="drivers" data-id="${driver.driverId}">Delete</button>
                            ` : ''}
                        </div>
                    </td>
                </tr>
            `
        });
    }

    function initConstructorsPage() {
        initRegistryPage({
            entity: 'constructors',
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
            lookupId: (constructor) => constructor.constructorId,
            renderRow: (constructor, index, baseRank) => `
                <tr>
                    <td><strong>${baseRank + index + 1}</strong></td>
                    <td><a href="/constructors/${constructor.constructorId}" class="constructor-link">${escapeHtml(constructor.name)}</a></td>
                    <td class="text-right">${constructor.numOfWins}</td>
                    <td class="text-right">${constructor.numOfRaces}</td>
                    <td class="text-right">
                        <div class="row-actions">
                            <a class="page-btn page-btn--inline" href="/constructors/${constructor.constructorId}">Profile</a>
                            ${isEmployeeUser() ? `
                                <button type="button" class="page-btn page-btn--inline" data-action="edit-row" data-entity="constructors" data-id="${constructor.constructorId}" data-item='${JSON.stringify(constructor).replace(/'/g, '&apos;')}'>Edit</button>
                                <button type="button" class="page-btn page-btn--inline page-btn--danger" data-action="delete-row" data-entity="constructors" data-id="${constructor.constructorId}">Delete</button>
                            ` : ''}
                        </div>
                    </td>
                </tr>
            `
        });
    }

    function initCircuitsPage() {
        initRegistryPage({
            entity: 'circuits',
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
            colspan: 5,
            emptyMessage: (query) => `No circuits found matching "${query}".`,
            lookupId: (circuit) => circuit.circuitId,
            renderRow: (circuit, index, baseRank) => `
                <tr>
                    <td><strong>${baseRank + index + 1}</strong></td>
                    <td><a href="/circuits/${circuit.circuitId}" class="circuit-link">${escapeHtml(circuit.name)}</a></td>
                    <td>${escapeHtml(circuit.country)}</td>
                    <td class="text-right">${circuit.numOfRaces}</td>
                    <td class="text-right">
                        <div class="row-actions">
                            ${isEmployeeUser() ? `
                                <button type="button" class="page-btn page-btn--inline" data-action="edit-row" data-entity="circuits" data-id="${circuit.circuitId}" data-item='${JSON.stringify(circuit).replace(/'/g, '&apos;')}'>Edit</button>
                                <button type="button" class="page-btn page-btn--inline page-btn--danger" data-action="delete-row" data-entity="circuits" data-id="${circuit.circuitId}">Delete</button>
                            ` : ''}
                        </div>
                    </td>
                </tr>
            `
        });
    }

    function initRacesPage() {
        initRegistryPage({
            entity: 'races',
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
            filterElements: ['sprint-filter'],
            getExtraParams: () => {
                const sprintVal = document.getElementById('sprint-filter')?.value;
                if (sprintVal === 'yes') return { sprint: 'true' };
                if (sprintVal === 'no') return { sprint: 'false' };
                return {};
            },
            lookupId: (race) => race.raceId,
            renderRow: (race, index, baseRank) => `
                <tr>
                    <td style="white-space: nowrap;">${escapeHtml(race.date || '-')}</td>
                    <td><a href="/races/${race.raceId}" class="race-link">${escapeHtml(race.raceName)}</a></td>
                    <td>${escapeHtml(race.circuitName || '-')}</td>
                    <td>${escapeHtml(race.country || '-')}</td>
                    <td>${escapeHtml(race.winnerName || '-')}</td>
                    <td>${escapeHtml(race.constructorName || '-')}</td>
                    <td>${escapeHtml(race.sprintAppearance ? 'yes' : 'no')}</td>
                    <td class="text-right">
                        <div class="row-actions">
                            <a class="page-btn page-btn--inline" href="/races/${race.raceId}">Details</a>
                            ${isEmployeeUser() ? `
                                <button type="button" class="page-btn page-btn--inline" data-action="edit-row" data-entity="races" data-id="${race.raceId}" data-item='${JSON.stringify(race).replace(/'/g, '&apos;')}'>Edit</button>
                                <button type="button" class="page-btn page-btn--inline page-btn--danger" data-action="delete-row" data-entity="races" data-id="${race.raceId}">Delete</button>
                            ` : ''}
                        </div>
                    </td>
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
            setText('race-country', raceData.circuit.country || '');
            setText('race-circuit', raceData.circuit.name || '');
            setHref('race-circuit', `/circuits/${raceData.circuit.circuitId}`);

            hide('status-message');
            show('data-container');

            if (raceData.sprintAppearance) {
                const actionButtons = document.querySelector('.action-buttons');

                const sprintBtn = document.createElement('a');
                sprintBtn.href = `/races/${raceId}/sprint-results`;
                sprintBtn.id = 'btn-sprint-results';
                sprintBtn.className = 'action-btn';
                sprintBtn.textContent = 'View Sprint Results';

                actionButtons.appendChild(sprintBtn);
            }
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

        const state = {
            currentPage: 0
        };

        const prevButton = document.getElementById('prev-btn');
        const nextButton = document.getElementById('next-btn');

        async function loadPage(page) {
            try {
                const data = await fetchJson(`/api/drivers/${driverId}/races?page=${page}&size=50`);
                const content = Array.isArray(data.content) ? data.content : [];
                state.currentPage = typeof data.number === 'number' ? data.number : page;

                if (content.length > 0) {
                    setText('page-title', `Race History: ${content[0].name}`);
                }

                setHtml('races-body', content.map((race) => {
                    const positionDisplay = renderPosition(race.position);
                    const circuitLabel = race.country || '-';

                    return `
                        <tr>
                            <td>${escapeHtml(race.date || '-')}</td>
                            <td><a href="/races/${race.raceId}" class="race-link">${escapeHtml(race.raceName || '-')}</a></td>
                            <td>${escapeHtml(circuitLabel)}</td>
                            <td><a href="/constructors/${race.constructorId}" class="constructor-link">${escapeHtml(race.team || '-')}</a></td>
                            <td class="text-center">${escapeHtml(race.grid)}</td>
                            <td class="text-center"><strong>${positionDisplay}</strong></td>
                            <td class="text-center">+${race.points}</td>
                        </tr>
                    `;
                }).join(''));

                setText('page-info', `Page ${state.currentPage + 1} of ${data.totalPages}`);
                if (prevButton) {
                    prevButton.disabled = !!data.first || state.currentPage <= 0;
                }
                if (nextButton) {
                    nextButton.disabled = !!data.last;
                }
            } catch (error) {
                console.error(error);
                setHtml('races-body', '<tr><td colspan="7" class="empty-state">Error loading data or unauthorized context.</td></tr>');
            }
        }

        if (prevButton) {
            prevButton.addEventListener('click', () => {
                if (state.currentPage > 0) {
                    loadPage(state.currentPage - 1);
                }
            });
        }

        if (nextButton) {
            nextButton.addEventListener('click', () => {
                loadPage(state.currentPage + 1);
            });
        }

        loadPage(0);
    }

    async function initConstructorRacesPage() {
        updateAuthButton();

        const constructorId = document.body.dataset.constructorId;
        const backLink = document.getElementById('profile-back-link');
        if (backLink) {
            backLink.href = `/constructors/${constructorId}`;
        }

        const state = {
            currentPage: 0
        };

        const prevButton = document.getElementById('prev-btn');
        const nextButton = document.getElementById('next-btn');

        async function loadPage(page) {
            try {
                const data = await fetchJson(`/api/constructors/${constructorId}/races?page=${page}&size=50`);
                const content = Array.isArray(data.content) ? data.content : [];
                state.currentPage = typeof data.number === 'number' ? data.number : page;

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

                setText('page-info', `Page ${state.currentPage + 1} of ${data.totalPages}`);
                if (prevButton) {
                    prevButton.disabled = !!data.first || state.currentPage <= 0;
                }
                if (nextButton) {
                    nextButton.disabled = !!data.last;
                }
            } catch (error) {
                console.error(error);
                setHtml('races-body', '<tr><td colspan="6" class="empty-state">Error loading data or unauthorized context.</td></tr>');
            }
        }

        if (prevButton) {
            prevButton.addEventListener('click', () => {
                if (state.currentPage > 0) {
                    loadPage(state.currentPage - 1);
                }
            });
        }

        if (nextButton) {
            nextButton.addEventListener('click', () => {
                loadPage(state.currentPage + 1);
            });
        }

        loadPage(0);
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
                    ${isEmployeeUser() ? `
                        <td class="text-right">
                            <div class="row-actions">
                                <button type="button" class="page-btn page-btn--inline" data-action="edit-row" data-entity="results" data-id="${result.resultId}" data-race-id="${raceId}" data-item='${JSON.stringify({
                                    resultId: result.resultId,
                                    raceId,
                                    driverUrl: result.driver.url,
                                    constructorName: result.constructor.name,
                                    grid: result.grid,
                                    position: result.position,
                                    points: result.points
                                }).replace(/'/g, '&apos;')}'>Edit</button>
                                <button type="button" class="page-btn page-btn--inline page-btn--danger" data-action="delete-row" data-entity="results" data-id="${result.resultId}" data-race-id="${raceId}">Delete</button>
                            </div>
                        </td>
                    ` : ''}
                </tr>
            `).join('') || '<tr><td colspan="6" class="text-center empty-state">No results data available.</td></tr>');

            if (isEmployeeUser()) {
                const actionsRow = document.createElement('div');
                actionsRow.className = 'header-section';
                actionsRow.innerHTML = `<button type="button" class="btn-primary" data-open-create="results" data-race-id="${raceId}" data-employee-action="true" hidden>Create Result</button>`;
                const container = document.querySelector('#data-container .card');
                if (container && !document.querySelector('[data-open-create="results"]')) {
                    container.insertAdjacentElement('afterend', actionsRow);
                }
            }

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
                    ${isEmployeeUser() ? `
                        <td class="text-right">
                            <div class="row-actions">
                                <button type="button" class="page-btn page-btn--inline" data-action="edit-row" data-entity="qualifying" data-id="${qualifying.qualifyingId}" data-race-id="${raceId}" data-item='${JSON.stringify({
                                    qualifyingId: qualifying.qualifyingId,
                                    raceId,
                                    driverUrl: qualifying.driver.url,
                                    constructorName: qualifying.constructor.name,
                                    position: qualifying.position
                                }).replace(/'/g, '&apos;')}'>Edit</button>
                                <button type="button" class="page-btn page-btn--inline page-btn--danger" data-action="delete-row" data-entity="qualifying" data-id="${qualifying.qualifyingId}" data-race-id="${raceId}">Delete</button>
                            </div>
                        </td>
                    ` : ''}
                </tr>
            `).join('') || '<tr><td colspan="4" class="text-center empty-state">No qualifying data available.</td></tr>');

            if (isEmployeeUser()) {
                const actionsRow = document.createElement('div');
                actionsRow.className = 'header-section';
                actionsRow.innerHTML = `<button type="button" class="btn-primary" data-open-create="qualifying" data-race-id="${raceId}" data-employee-action="true" hidden>Create Qualifying</button>`;
                const container = document.querySelector('#data-container .card');
                if (container && !document.querySelector('[data-open-create="qualifying"]')) {
                    container.insertAdjacentElement('afterend', actionsRow);
                }
            }

            hide('status-message');
            show('data-container');
        } catch (error) {
            console.error(error);
            setText('status-message', 'Error loading data.');
        }
    }

    async function initRaceSprintResultsPage() {
        updateAuthButton();

        const raceId = document.body.dataset.raceId;
        setHref('back-link', `/races/${raceId}`);

        try {
            const [raceData, sprintResultsData] = await Promise.all([
                fetchJson(`/api/races/${raceId}`),
                fetchJson(`/api/races/${raceId}/sprint-results`)
            ]);

            setText('race-name', raceData.name);
            setHtml('sprint-results-body', (sprintResultsData || []).map((result) => `
                <tr>
                    <td class="text-center"><strong>${escapeHtml(result.position || '-')}</strong></td>
                    <td class="text-center">${escapeHtml(result.grid || '-')}</td>
                    <td><a href="/drivers/${result.driver.driverId}" class="driver-link">${escapeHtml(result.driver.name)}</a></td>
                    <td>${escapeHtml(result.driver.nationality)}</td>
                    <td><a href="/constructors/${result.constructor.constructorId}" class="constructor-link">${escapeHtml(result.constructor.name)}</a></td>
                    <td class="text-right">+${result.points || '0'}</td>
                    ${isEmployeeUser() ? `
                        <td class="text-right">
                            <div class="row-actions">
                                <button type="button" class="page-btn page-btn--inline" data-action="edit-row" data-entity="sprintResults" data-id="${result.resultId}" data-race-id="${raceId}" data-item='${JSON.stringify({
                                    resultId: result.resultId,
                                    raceId,
                                    driverUrl: result.driver.url,
                                    constructorName: result.constructor.name,
                                    grid: result.grid,
                                    position: result.position,
                                    points: result.points
                                }).replace(/'/g, '&apos;')}'>Edit</button>
                                <button type="button" class="page-btn page-btn--inline page-btn--danger" data-action="delete-row" data-entity="sprintResults" data-id="${result.resultId}" data-race-id="${raceId}">Delete</button>
                            </div>
                        </td>
                    ` : ''}
                </tr>
            `).join('') || '<tr><td colspan="7" class="text-center empty-state">No sprint results data available.</td></tr>');

            hide('status-message');
            show('data-container');
        } catch (error) {
            console.error(error);
            setText('status-message', 'Error loading sprint results.');
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
        ensureCrudModal();
        updateAuthButton();
        syncEmployeeControls();
        bindCrudRowActions();

        const authButton = document.getElementById('auth-btn');
        if (authButton) {
            authButton.addEventListener('click', handleAuthAction);
        }

        document.addEventListener('click', (event) => {
            const createButton = event.target instanceof HTMLElement ? event.target.closest('[data-open-create]') : null;
            if (!createButton) {
                return;
            }

            openCrudModal(createButton.dataset.openCreate, 'create', {
                raceId: createButton.dataset.raceId || document.body.dataset.raceId || ''
            });
        });

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
            case 'race-sprint-results':
                initRaceSprintResultsPage();
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
