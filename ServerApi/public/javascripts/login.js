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
            // Kiểm tra role từ API
            if (result.role === 0) { // Nếu là admin (role = 0)
                // Hiển thị thông báo đăng nhập thành công
                Swal.fire({
                    title: 'Đăng nhập thành công!',
                    text: 'Chào mừng bạn đến với Dashboard.',
                    icon: 'success',
                    confirmButtonText: 'Đi tới Dashboard',
                }).then(() => {
                    // Chuyển hướng đến Dashboard
                    window.location.href = '/dashbroad';
                });
            } else {
                // Hiển thị thông báo không đủ quyền
                Swal.fire({
                    title: 'Bạn không đủ quyền',
                    text: 'Chỉ quản trị viên mới được phép đăng nhập.',
                    icon: 'warning',
                    confirmButtonText: 'OK',
                });
            }
        } else {
            // Hiển thị thông báo lỗi nếu đăng nhập không thành công
            Swal.fire({
                title: 'Đăng nhập không thành công',
                text: result.message || 'Vui lòng kiểm tra lại thông tin.',
                icon: 'error',
                confirmButtonText: 'Thử lại',
            });
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.fire({
            title: 'Đã xảy ra lỗi!',
            text: 'Vui lòng thử lại sau.',
            icon: 'error',
            confirmButtonText: 'Đóng',
        });
    }
};

loginForm.addEventListener('submit', event => {
    event.preventDefault();
    loginUser();
});
