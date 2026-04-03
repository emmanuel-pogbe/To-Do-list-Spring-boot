let currentUserId = null;
let currentUsername = null;

const authModal = document.getElementById('auth-modal');
const appContainer = document.getElementById('app-container');
const taskListEl = document.getElementById('task-list');
const emptyStateEl = document.getElementById('empty-state');
const statusMessage = document.getElementById('status-message');
const searchFormEl = document.getElementById('search-form');
const searchInputEl = document.getElementById('search-input');
const clearSearchBtnEl = document.getElementById('clear-search-btn');

// Date filter elements
const filterRadios = document.querySelectorAll('input[name="filter-type"]');
const dateAfterInput = document.getElementById('date-after');
const dateBeforeInput = document.getElementById('date-before');
const dateFromInput = document.getElementById('date-from');
const dateToInput = document.getElementById('date-to');
const applyFilterBtn = document.getElementById('apply-filter-btn');
const resetFilterBtn = document.getElementById('reset-filter-btn');

// Pagination elements
const paginationContainer = document.getElementById('pagination-container');
const paginationInfo = document.getElementById('pagination-info');
const pageNumbersDiv = document.getElementById('page-numbers');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');

// Pagination state
let currentPage = 0;
let totalPages = 0;
let pageSize = 5;
let currentFilter = null; // Track which filter is active

function showMessage(message, type = 'success') {
  statusMessage.textContent = message;
  statusMessage.className = `status-message show ${type}`;
  setTimeout(() => {
    statusMessage.classList.remove('show');
  }, 4000);
}

function showErrorInForm(formId, message) {
  const errorEl = document.getElementById(formId + '-error');
  if (errorEl) {
    errorEl.textContent = message;
    errorEl.classList.add('show');
    setTimeout(() => {
      errorEl.classList.remove('show');
    }, 4000);
  }
}

function showAuthModal() {
  authModal.classList.remove('hidden');
  appContainer.classList.remove('show');
}

function showAppView() {
  authModal.classList.add('hidden');
  appContainer.classList.add('show');
  document.getElementById('user-badge').textContent = 'Logged in as ' + currentUsername;
}

function switchToSignup() {
  document.getElementById('login-view').style.display = 'none';
  document.getElementById('signup-view').style.display = 'block';
  document.getElementById('login-error').classList.remove('show');
  document.getElementById('signup-error').classList.remove('show');
}

function switchToLogin() {
  document.getElementById('login-view').style.display = 'block';
  document.getElementById('signup-view').style.display = 'none';
  document.getElementById('login-error').classList.remove('show');
  document.getElementById('signup-error').classList.remove('show');
}

function handleSessionExpired() {
  currentUserId = null;
  currentUsername = null;
  showAuthModal();
  switchToLogin();
  showMessage('Your session expired. Please sign in again.', 'error');
}

async function postJson(url, body) {
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(body)
  });

  const data = await response.json().catch(() => ({}));
  return { response, data };
}

async function requestJson(url, method, body) {
  const options = { method, headers: {} };

  if (body) {
    options.headers['Content-Type'] = 'application/json';
    options.body = JSON.stringify(body);
  }

  const response = await fetch(url, options);
  const data = await response.json().catch(() => ({}));
  return { response, data };
}

async function restoreSession() {
  const { response, data } = await requestJson('/user/me', 'GET');

  if (response.ok && data.authenticated) {
    currentUserId = data.userId;
    currentUsername = data.userName;
    showAppView();
    await handleLoadTasks();
    return true;
  }

  return false;
}

document.getElementById('signup-form').addEventListener('submit', async (event) => {
  event.preventDefault();

  const username = document.getElementById('signup-username').value.trim();
  const password = document.getElementById('signup-password').value;

  if (!username || !password) {
    showErrorInForm('signup', 'Please fill in all fields.');
    return;
  }

  const { data } = await postJson('/user/create', { username, password });

  if (data.createStatus) {
    showErrorInForm('signup', '');
    document.getElementById('signup-form').reset();
    switchToLogin();
    showMessage('Account created! Please sign in.', 'success');
  } else {
    showErrorInForm('signup', 'Username already exists. Try another.');
  }
});

document.getElementById('login-form').addEventListener('submit', async (event) => {
  event.preventDefault();

  const userName = document.getElementById('login-username').value.trim();
  const password = document.getElementById('login-password').value;

  if (!userName || !password) {
    showErrorInForm('login', 'Please fill in all fields.');
    return;
  }

  const { response, data } = await postJson('/user/login', { userName, password });

  if (response.ok && data.authenticated) {
    currentUserId = data.userId;
    currentUsername = userName;
    document.getElementById('login-form').reset();
    showErrorInForm('login', '');
    showAppView();
    showMessage('Welcome back, ' + userName + '!', 'success');
    handleLoadTasks();
  } else {
    showErrorInForm('login', 'Invalid username or password.');
    currentUserId = null;
    currentUsername = null;
  }
});

document.getElementById('task-form').addEventListener('submit', async (event) => {
  event.preventDefault();

  if (!currentUserId) {
    showMessage('Log in first before creating tasks.', 'error');
    return;
  }

  const task = document.getElementById('task-text').value.trim();

  if (!task) {
    showMessage('Please enter a task.', 'error');
    return;
  }

  const { data } = await postJson('/task/create', { userId: currentUserId, task });

  if (data.taskId) {
    showMessage('Task created!', 'success');
    document.getElementById('task-text').value = '';
    handleLoadTasks();
  } else {
    showMessage('Could not create task.', 'error');
  }
});

searchFormEl.addEventListener('submit', async (event) => {
  event.preventDefault();
  await handleLoadTasks();
});

clearSearchBtnEl.addEventListener('click', async () => {
  searchInputEl.value = '';
  await handleLoadTasks();
});

// Date filter event listeners
filterRadios.forEach(radio => {
  radio.addEventListener('change', (e) => {
    const filterType = e.target.value;
    dateAfterInput.style.display = 'none';
    dateBeforeInput.style.display = 'none';
    dateFromInput.style.display = 'none';
    dateToInput.style.display = 'none';
    applyFilterBtn.style.display = 'none';

    if (filterType === 'after') {
      dateAfterInput.style.display = 'block';
      applyFilterBtn.style.display = 'block';
    } else if (filterType === 'before') {
      dateBeforeInput.style.display = 'block';
      applyFilterBtn.style.display = 'block';
    } else if (filterType === 'between') {
      dateFromInput.style.display = 'block';
      dateToInput.style.display = 'block';
      applyFilterBtn.style.display = 'block';
    }
  });
});

applyFilterBtn.addEventListener('click', async () => {
  const filterType = document.querySelector('input[name="filter-type"]:checked').value;
  
  if (filterType === 'after') {
    const date = dateAfterInput.value;
    if (!date) {
      showMessage('Please select a date', 'error');
      return;
    }
    await handleDateFilter('after', date);
  } else if (filterType === 'before') {
    const date = dateBeforeInput.value;
    if (!date) {
      showMessage('Please select a date', 'error');
      return;
    }
    await handleDateFilter('before', date);
  } else if (filterType === 'between') {
    const startDate = dateFromInput.value;
    const endDate = dateToInput.value;
    if (!startDate || !endDate) {
      showMessage('Please select both dates', 'error');
      return;
    }
    if (startDate > endDate) {
      showMessage('Start date must be before end date', 'error');
      return;
    }
    await handleDateFilter('between', startDate, endDate);
  }
});

resetFilterBtn.addEventListener('click', async () => {
  document.getElementById('filter-all').checked = true;
  dateAfterInput.style.display = 'none';
  dateBeforeInput.style.display = 'none';
  dateFromInput.style.display = 'none';
  dateToInput.style.display = 'none';
  applyFilterBtn.style.display = 'none';
  dateAfterInput.value = '';
  dateBeforeInput.value = '';
  dateFromInput.value = '';
  dateToInput.value = '';
  searchInputEl.value = '';
  currentPage = 0; // Reset pagination
  await handleLoadTasks();
});

async function handleLoadTasks() {
  if (!currentUserId) {
    showMessage('Log in first to view your tasks.', 'error');
    return;
  }

  const searchTerm = searchInputEl.value.trim();
  const endpoint = searchTerm
    ? '/task/user/' + currentUserId + '?search=' + encodeURIComponent(searchTerm) + '&page=' + currentPage + '&size=' + pageSize
    : '/task/user/' + currentUserId + '?page=' + currentPage + '&size=' + pageSize;
  const response = await fetch(endpoint);

  if (response.status === 401) {
    handleSessionExpired();
    return;
  }

  const pageResponse = await response.json().catch(() => ({ content: [] }));

  // Extract tasks from PagedModel response
  const tasks = pageResponse.content || [];
  totalPages = pageResponse.page?.totalPages || 0;

  taskListEl.innerHTML = '';

  if (tasks.length === 0) {
    emptyStateEl.style.display = 'block';
    paginationContainer.style.display = 'none';
  } else {
    emptyStateEl.style.display = 'none';
    renderTasks(tasks);
    renderPagination();
    paginationContainer.style.display = 'flex';
  }
}

function renderTasks(tasks) {
  tasks.forEach((item) => {
    const li = document.createElement('li');
    li.className = 'task-item';
    
    const taskDiv = document.createElement('div');
    taskDiv.className = 'task-text';
    
    const taskP = document.createElement('p');
    taskP.textContent = item.task;
    
    const dateP = document.createElement('p');
    dateP.className = 'task-date';
    dateP.textContent = 'Created: ' + (item.createdAt || 'Today');

    const actionsDiv = document.createElement('div');
    actionsDiv.className = 'task-actions';

    const editInput = document.createElement('input');
    editInput.type = 'text';
    editInput.placeholder = 'Update description';

    const editBtn = document.createElement('button');
    editBtn.className = 'edit-btn';
    editBtn.type = 'button';
    editBtn.textContent = 'Update';
    editBtn.addEventListener('click', async () => {
      await handleUpdateTask(item.id, editInput.value);
    });

    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'delete-btn';
    deleteBtn.type = 'button';
    deleteBtn.textContent = 'Delete';
    deleteBtn.addEventListener('click', async () => {
      await handleDeleteTask(item.id);
    });

    actionsDiv.appendChild(editInput);
    actionsDiv.appendChild(editBtn);
    actionsDiv.appendChild(deleteBtn);
    
    taskDiv.appendChild(taskP);
    taskDiv.appendChild(dateP);
    li.appendChild(taskDiv);
    li.appendChild(actionsDiv);
    taskListEl.appendChild(li);
  });
}

function renderPagination() {
  paginationInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;
  
  prevBtn.disabled = currentPage === 0;
  nextBtn.disabled = currentPage >= totalPages - 1;

  pageNumbersDiv.innerHTML = '';
  
  // Show page numbers (max 5 pages visible)
  let startPage = Math.max(0, currentPage - 2);
  let endPage = Math.min(totalPages, startPage + 5);
  if (endPage - startPage < 5) {
    startPage = Math.max(0, endPage - 5);
  }

  for (let i = startPage; i < endPage; i++) {
    const pageBtn = document.createElement('button');
    pageBtn.className = 'pagination-btn' + (i === currentPage ? ' active' : '');
    pageBtn.textContent = i + 1;
    pageBtn.onclick = () => handlePageClick(i);
    pageNumbersDiv.appendChild(pageBtn);
  }
}

function handlePageClick(pageNum) {
  currentPage = pageNum;
  handleLoadTasks();
}

function handlePreviousPage() {
  if (currentPage > 0) {
    currentPage--;
    handleLoadTasks();
  }
}

function handleNextPage() {
  if (currentPage < totalPages - 1) {
    currentPage++;
    handleLoadTasks();
  }
}

async function handleUpdateTask(taskId, newDescription) {
  const description = newDescription.trim();

  if (!description) {
    showMessage('Enter a new task description first.', 'error');
    return;
  }

  const { response, data } = await requestJson('/task/update/' + taskId, 'PATCH', {
    description
  });

  if (response.status === 401) {
    handleSessionExpired();
    return;
  }

  if (response.ok && (data.isUpdated || data.updated || data.id)) {
    showMessage('Task updated successfully.', 'success');
    handleLoadTasks();
  } else {
    showMessage('Failed to update task.', 'error');
  }
}

async function handleDeleteTask(taskId) {
  const { response, data } = await requestJson('/task/delete/' + taskId, 'DELETE');

  if (response.status === 401) {
    handleSessionExpired();
    return;
  }

  if (response.ok && (data.deleted || data.isDeleted || data.id)) {
    showMessage('Task deleted successfully.', 'success');
    handleLoadTasks();
  } else {
    showMessage('Failed to delete task.', 'error');
  }
}

async function handleDateFilter(filterType, startDate, endDate) {
  if (!currentUserId) {
    showMessage('Log in first to filter tasks.', 'error');
    return;
  }

  currentPage = 0; // Reset to first page when applying new filter
  let endpoint = '';
  if (filterType === 'after') {
    endpoint = '/task/user/' + currentUserId + '/after?date=' + startDate + '&page=' + currentPage + '&size=' + pageSize;
  } else if (filterType === 'before') {
    endpoint = '/task/user/' + currentUserId + '/before?date=' + startDate + '&page=' + currentPage + '&size=' + pageSize;
  } else if (filterType === 'between') {
    endpoint = '/task/user/' + currentUserId + '/between?startDate=' + startDate + '&endDate=' + endDate + '&page=' + currentPage + '&size=' + pageSize;
  }

  const response = await fetch(endpoint);

  if (response.status === 401) {
    handleSessionExpired();
    return;
  }

  const pageResponse = await response.json().catch(() => ({ content: [] }));

  const tasks = pageResponse.content || [];
  totalPages = pageResponse.page?.totalPages || 0;

  taskListEl.innerHTML = '';

  if (tasks.length === 0) {
    emptyStateEl.style.display = 'block';
    paginationContainer.style.display = 'none';
    showMessage('No tasks found for selected date range.', 'success');
  } else {
    emptyStateEl.style.display = 'none';
    renderTasks(tasks);
    renderPagination();
    paginationContainer.style.display = 'flex';
  }
}

async function handleLogout() {
  await requestJson('/logout', 'POST');
  currentUserId = null;
  currentUsername = null;
  showAuthModal();
  switchToLogin();
  document.getElementById('login-form').reset();
  document.getElementById('signup-form').reset();
  showMessage('Logged out successfully.', 'success');
}

// Initialize
async function initializeApp() {
  const isAuthenticated = await restoreSession();

  if (!isAuthenticated) {
    showAuthModal();
    switchToLogin();
  }
}

initializeApp();
