// Fetch all users and populate the table
async function fetchUsers() {
    const response = await fetch("/api/user");
    const users = await response.json();
    populateTable(users);
  }
  
  // Populate the user table
  function populateTable(users) {
    const tableBody = document.getElementById("userTableBody");
    tableBody.innerHTML = ""; // Xóa bảng hiện tại
  
    users.forEach((user, index) => {
      const gender = user.gender?.toLowerCase() === "nam" ? "Nam" : "Nữ"; // Kiểm tra giới tính
  
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${index + 1}</td>
        <td><img src="${user.avatar}" alt="User Image" class="product-image" width="50"></td>
        <td>${user.name}</td>
        <td>${formatDate(user.birthday)}</td> <!-- Chuyển định dạng ngày -->
        <td>${gender}</td>
        <td>${user.phonenumber || "N/A"}</td>
        <td>${user.address || "N/A"}</td>
        <td>${user.role === 0 ? "Admin" : "User"}</td>
        <td>
          <img onclick="editUser('${user._id}')" src="./img/edit.png" style="width:20px;height:20px" alt="Edit">
        </td>
      `;
      tableBody.appendChild(row);
    });
  }
  
  // Add or Edit User
  document.getElementById("userForm").addEventListener("submit", async (e) => {
    e.preventDefault();
  
    const id = e.target.dataset.id; // Kiểm tra xem đang thêm mới hay chỉnh sửa
    const user = {
      username: document.getElementById("username").value,
      password: document.getElementById("password").value,
      name: document.getElementById("name").value,
      email: document.getElementById("email").value,
      phonenumber: document.getElementById("phonenumber").value,
      gender: document.getElementById("gender").value.toLowerCase(), // Chuyển thành chữ thường
      birthday: document.getElementById("birthday").value,
      address: document.getElementById("address").value,
      avatar: document.getElementById("avatar").value,
    };
  
    const url = id ? `/api/update_user/${id}` : "/api/add_user";
    const method = id ? "PUT" : "POST";
  
    const response = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(user),
    });
  
    if (response.ok) {
      fetchUsers();
      closeModal();
    }
  });
  
  
  // Open Add User Modal
  document.getElementById("addUserBtn").addEventListener("click", () => {
    openModal();
  });
  
  // Open Edit User Modal
  async function editUser(id) {
    const response = await fetch(`/api/getuserbyid/${id}`);
    const { data: user } = await response.json();
  
    // Điền dữ liệu người dùng vào form
    document.getElementById("username").value = user.username;
    document.getElementById("password").value = user.password;
    document.getElementById("name").value = user.name;
    document.getElementById("email").value = user.email;
    document.getElementById("phonenumber").value = user.phonenumber;
    document.getElementById("gender").value = user.gender.toLowerCase() === "nam" ? "Nam" : "Nữ";
    document.getElementById("birthday").value = user.birthday.slice(0, 10);
    document.getElementById("address").value = user.address;
    document.getElementById("avatar").value = user.avatar;
  
    // Gắn ID vào form để biết đây là chỉnh sửa
    document.getElementById("userForm").dataset.id = id;
  
    openModal(); // Mở modal
  }
  
  // Delete User
  async function deleteUser(id) {
    if (confirm("Are you sure you want to delete this user?")) {
      const response = await fetch(`/api/delete_user/${id}`, { method: "DELETE" });
  
      if (response.ok) {
        alert("User deleted successfully!");
        fetchUsers(); // Refresh table
      }
    }
  }
  
  // Modal Handling
  function openModal() {
    const modal = document.getElementById("userFormModal");
    modal.style.display = "flex"; // Hiển thị modal
  }
  
  function closeModal() {
    const modal = document.getElementById("userFormModal");
    modal.style.display = "none"; // Đảm bảo modal ẩn đi
    document.getElementById("userForm").reset(); // Reset form nếu cần
    delete document.getElementById("userForm").dataset.id; // Xóa ID (nếu có)
  }
  
  
  document.addEventListener("DOMContentLoaded", () => {
    // Đảm bảo modal không được hiển thị khi trang tải lại
    closeModal();
  });
  
  // Load users on page load
  document.addEventListener("DOMContentLoaded", fetchUsers);
  

  //tìm kiếm theo tên người dùng
document.querySelector('form').addEventListener('submit', async (e) => {
  e.preventDefault(); // Ngừng sự kiện submit mặc định
  
  const searchQuery = document.querySelector('input[type="search"]').value; // Lấy giá trị tìm kiếm

  if (searchQuery) {
    const response = await fetch(`/api/searchUsersByName?name=${searchQuery}`);
    const users = await response.json();
    populateTable(users);
  } else {
    // Nếu không có gì để tìm, tải lại tất cả người dùng
    fetchSearchUsers();
  }
});

// Fetch all users and populate the table (cập nhật lại sau khi tìm kiếm)
async function fetchSearchUsers() {
  const response = await fetch("/api/searchUsersByName");
  const users = await response.json();
  populateTable(users);
}

// Populate the user table
function populateTable(users) {
  const tableBody = document.getElementById("userTableBody");
  tableBody.innerHTML = ""; // Xóa các hàng hiện tại

  users.forEach((user, index) => {
    const gender = user.gender.toLowerCase() === "nam" ? "Nam" : "Nữ"; // Chuẩn hóa chữ thường/hoa

    const row = document.createElement("tr");
    row.innerHTML = `
      <td style="margin-top:20px;margin-left:10px">${index + 1}</td>
      <td><img src="${user.avatar}" alt="User Image" class="product-image" width="50"></td>
      <td>${user.name}</td>
      <td>${formatDate(user.birthday)}</td>
      <td style="text-align:center">${gender}</td>
      <td>${user.phonenumber}</td>
      <td>${user.address}</td>
      <td>${user.role === 0 ? "Admin" : "User"}</td>
      <td>
        <img onclick="editUser('${user._id}')" src="./img/edit.png" style="width:20px;height:20px;text-align:center" alt="Edit">
      </td>
    `;
    tableBody.appendChild(row);
  });
}

function formatDate(dateString) {
  if (!dateString) return "N/A"; // Trả về N/A nếu không có ngày
  const date = new Date(dateString);
  const day = String(date.getDate()).padStart(2, "0"); // Thêm 0 nếu nhỏ hơn 10
  const month = String(date.getMonth() + 1).padStart(2, "0"); // Tháng bắt đầu từ 0
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
}