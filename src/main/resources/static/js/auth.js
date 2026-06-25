// Token yönetimi
function getToken() {
    return localStorage.getItem('accessToken');
}

function setToken(token) {
    localStorage.setItem('accessToken', token);
}

function removeToken() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userName');
}

function getUserInfo() {
    return {
        email: localStorage.getItem('userEmail'),
        name: localStorage.getItem('userName')
    };
}

function setUserInfo(email, firstName, lastName) {
    localStorage.setItem('userEmail', email);
    localStorage.setItem('userName', firstName + ' ' + lastName);
}

// Login
if (document.getElementById('loginForm')) {
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const errorDiv = document.getElementById('errorMessage');
        errorDiv.classList.remove('show');
        errorDiv.textContent = '';

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (response.ok) {
                setToken(data.accessToken);
                setUserInfo(data.email, data.firstName, data.lastName);
                window.location.href = '/auctions';
            } else {
                errorDiv.textContent = data || 'Giriş başarısız';
                errorDiv.classList.add('show');
            }
        } catch (error) {
            errorDiv.textContent = 'Bir hata oluştu: ' + error.message;
            errorDiv.classList.add('show');
        }
    });
}

// Register
if (document.getElementById('registerForm')) {
    document.getElementById('registerForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const errorDiv = document.getElementById('errorMessage');
        errorDiv.classList.remove('show');
        errorDiv.textContent = '';

        const formData = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            email: document.getElementById('email').value,
            phoneNumber: document.getElementById('phoneNumber').value,
            password: document.getElementById('password').value
        };

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            const data = await response.json();

            if (response.ok) {
                setToken(data.accessToken);
                setUserInfo(data.email, data.firstName, data.lastName);
                window.location.href = '/auctions';
            } else {
                errorDiv.textContent = data || 'Kayıt başarısız';
                errorDiv.classList.add('show');
            }
        } catch (error) {
            errorDiv.textContent = 'Bir hata oluştu: ' + error.message;
            errorDiv.classList.add('show');
        }
    });
}

// Logout
if (document.getElementById('logoutBtn')) {
    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        removeToken();
        window.location.href = '/login';
    });
}

// User info gösterimi
function updateUserInfo() {
    const userInfo = getUserInfo();
    const userInfoSpan = document.getElementById('userInfo');
    const logoutBtn = document.getElementById('logoutBtn');
    const loginLink = document.getElementById('loginLink');

    if (userInfo.email) {
        if (userInfoSpan) {
            userInfoSpan.textContent = 'Hoş geldiniz, ' + (userInfo.name || userInfo.email);
        }
        if (logoutBtn) {
            logoutBtn.style.display = 'inline-block';
        }
        if (loginLink) {
            loginLink.style.display = 'none';
        }
    } else {
        if (logoutBtn) {
            logoutBtn.style.display = 'none';
        }
        if (loginLink) {
            loginLink.style.display = 'inline-block';
        }
    }
}

// Sayfa yüklendiğinde user info'yu güncelle
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', updateUserInfo);
} else {
    updateUserInfo();
}

