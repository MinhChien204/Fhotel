let users = [];

// Fetch users from the server
async function fetchUsers() {
    try {
        const response = await fetch('/api/user');
        users = await response.json();
        renderUserTable(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

// Render users in the table
function renderUserTable(users) {
    const userTableBody = document.getElementById('userTableBody');
    userTableBody.innerHTML = '';
    users.forEach(user => {
        const userRow = document.createElement('tr');
        userRow.innerHTML = `
            <td>${user.name || 'N/A'}</td>
            <td>${user.email || 'N/A'}</td>
            <td>${user.phonenumber || 'N/A'}</td>
            <td>${user.role === 0 ? 'Admin' : 'User'}</td>
            <td>
                <img src="${user.avatar || 'default-avatar.png'}" alt="Avatar" class="avatar" width="50" />
            </td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="editUser('${user._id}')">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteUser('${user._id}')">Delete</button>
            </td>
        `;
        userTableBody.appendChild(userRow);
    });
}

// Edit user
function editUser(userId) {
    const user = users.find(user => user._id === userId);
    document.getElementById('userId').value = user._id;
    document.getElementById('username').value = user.username;
    document.getElementById('email').value = user.email;
    document.getElementById('name').value = user.name;
    document.getElementById('phonenumber').value = user.phonenumber;
    document.getElementById('address').value = user.address;
    $('#userModal').modal('show');
}

// Delete user
async function deleteUser(userId) {
    if (confirm('Are you sure you want to delete this user?')) {
        try {
            await fetch(`/api/user/${userId}`, { method: 'DELETE' });
            fetchUsers();
        } catch (error) {
            console.error('Error deleting user:', error);
        }
    }
}

// Initialize
document.addEventListener('DOMContentLoaded', fetchUsers);

// Mở modal và điền dữ liệu người dùng vào form
function editUser(userId) {
    const user = users.find(u => u._id === userId);
    if (!user) {
        alert('User not found');
        return;
    }

    // Điền thông tin vào form
    document.getElementById('editUserId').value = user._id;
    document.getElementById('editUsername').value = user.username;
    document.getElementById('editEmail').value = user.email;
    document.getElementById('editName').value = user.name || '';
    document.getElementById('editPhone').value = user.phonenumber || '';
    document.getElementById('editAddress').value = user.address || '';
    document.getElementById('editAvatar').value = user.avatar || '';
    document.getElementById('editRole').value = user.role.toString();

    // Hiển thị modal
    const modal = document.getElementById('editUserModal');
    modal.style.display = 'block';
}

async function saveUserChanges() {
    const userId = document.getElementById('editUserId').value;
    const userData = {
        username: document.getElementById('editUsername').value.trim(),
        email: document.getElementById('editEmail').value.trim(),
        name: document.getElementById('editName').value.trim(),
        phonenumber: document.getElementById('editPhone').value.trim(),
        address: document.getElementById('editAddress').value.trim(),
        avatar: document.getElementById('editAvatar').value.trim(),
        role: parseInt(document.getElementById('editRole').value, 10),
    };

    try {
        const response = await fetch(`/api/user/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });

        if (response.ok) {
            alert('User updated successfully');
            closeEditModal(); // Đóng modal
            fetchUsers(); // Tải lại danh sách người dùng
        } else {
            const result = await response.json();
            alert(`Failed to update user: ${result.message}`);
        }
    } catch (error) {
        console.error('Error updating user:', error);
        alert('Failed to update user');
    }
}
function closeEditModal() {
    const modal = document.getElementById('editUserModal');
    modal.style.display = 'none';
}
function renderUserTable(users) {
    const userTableBody = document.getElementById('userTableBody');
    userTableBody.innerHTML = '';
    users.forEach(user => {
        const userRow = document.createElement('tr');
        userRow.innerHTML = `
            <td>${user.name || 'N/A'}</td>
            <td>${user.email || 'N/A'}</td>
            <td>${user.phonenumber || 'N/A'}</td>
            <td>${user.role === 0 ? 'Admin' : 'User'}</td>
            <td>
                <img src="${user.avatar || 'default-avatar.png'}" alt="Avatar" class="avatar" width="50" />
            </td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="editUser('${user._id}')">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteUser('${user._id}')">Delete</button>
            </td>
        `;
        userTableBody.appendChild(userRow);
    });
}

