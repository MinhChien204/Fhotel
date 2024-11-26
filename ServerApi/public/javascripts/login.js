const loginForm = document.getElementById('loginForm');
const username = document.getElementById('username');
const password = document.getElementById('password');

const loginUser = async () => {
    try {
        const data = {
            username: username.value.trim(),
            password: password.value.trim(),
        };

        const response = await fetch('http://localhost:3000/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        const result = await response.json();

        if (response.ok) {
            alert('Login thành công');
            window.location.href = '/dashbroad';
        } else {
            alert(result.message || 'Login không thành công');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Đã xảy ra lỗi khi gửi yêu cầu');
    } 
};

loginForm.addEventListener('submit', event => {
    event.preventDefault();
    loginUser();
});
