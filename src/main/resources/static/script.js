let currentUserId = null;
let currentUsername = null;

const authModal = document.getElementById('auth-modal');
const appContainer = document.getElementById('app-container');
const taskListEl = document.getElementById('task-list');
const emptyStateEl = document.getElementById('empty-state');
const statusMessage = document.getElementById('status-message');

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

async function handleLoadTasks() {
  if (!currentUserId) {
    showMessage('Log in first to view your tasks.', 'error');
    return;
  }

  const response = await fetch('/task/user/' + currentUserId);
  const tasks = await response.json().catch(() => []);

  taskListEl.innerHTML = '';

  if (tasks.length === 0) {
    emptyStateEl.style.display = 'block';
  } else {
    emptyStateEl.style.display = 'none';
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
      
      taskDiv.appendChild(taskP);
      taskDiv.appendChild(dateP);
      li.appendChild(taskDiv);
      taskListEl.appendChild(li);
    });
  }
}

function handleLogout() {
  currentUserId = null;
  currentUsername = null;
  showAuthModal();
  switchToLogin();
  document.getElementById('login-form').reset();
  document.getElementById('signup-form').reset();
  showMessage('Logged out successfully.', 'success');
}

// Initialize
showAuthModal();
