// Fetch booking data from the API
async function fetchBookings() {
    try {
      const response = await fetch("/api/bookings"); // Adjust the URL if necessary
      const bookings = await response.json();
  
      const tableBody = document.getElementById("BookingTableBody");
      tableBody.innerHTML = ""; // Clear the table before rendering
  
      bookings.forEach((booking, index) => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${index + 1}</td>
          <td>${booking.userId?.name || "Unknown "}</td>
          <td>${booking.roomId?.name || "Unknown "}</td>
          <td>${booking.totalPrice.toLocaleString()} VND</td>
          <td>${booking.startDate}</td>
          <td>${booking.endDate}</td>
          <td>${booking.userId?.phonenumber}</td>
          <td>
            <select
              class="status-dropdown"
              data-booking-id="${booking._id}"
              onchange="updateBookingStatus('${booking._id}', this.value)"
            >
              <option value="pending" ${booking.status === "pending" ? "selected" : ""}>Chờ xử ký</option>
              <option value="confirmed" ${booking.status === "confirmed" ? "selected" : ""}>Đã xác nhận</option>
              <option value="cancelled" ${booking.status === "cancelled" ? "selected" : ""}>Huỷ</option>
            </select>
          </td>
          <td>   
          </td>
        `;
        tableBody.appendChild(row);
      });
    } catch (error) {
      console.error("Failed to fetch bookings:", error);
    }
  }
  
  
  fetchBookings();
  
  // Example edit and delete functions (implement backend for these)

  async function updateBookingStatus(bookingId, newStatus) {
    try {
      const response = await fetch(`/api/update-status-booking/${bookingId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ status: newStatus }),
      });
  
      const result = await response.json();
  
      if (response.ok) {
        alert("Cập nhật trạng thái booking thành công!");
        fetchBookings(); // Tải lại danh sách booking
      } else {
        alert(`Lỗi: ${result.message}`);
      }
    } catch (error) {
      console.error("Lỗi khi cập nhật trạng thái booking:", error);
      alert("Không thể cập nhật trạng thái booking!");
    }
  }
  
  async function deleteBooking(bookingId) {
    const confirmDelete = confirm("Bạn có chắc chắn muốn xóa booking này?");
    if (!confirmDelete) return;
  
    try {
      const response = await fetch(`/api/cancel_booking/${bookingId}`, {
        method: "DELETE",
      });
  
      if (response.ok) {
        alert("Xóa booking thành công!");
        fetchBookings(); // Tải lại danh sách booking
      } else {
        alert("Không thể xóa booking!");
      }
    } catch (error) {
      console.error("Lỗi khi xóa booking:", error);
      alert("Không thể xóa booking!");
    }
  }
  
  //firebase notifications
  // Firebase messaging initialization
const messaging = firebase.messaging();

// Request permission to receive notifications
async function requestNotificationPermission() {
  try {
    const permission = await Notification.requestPermission();
    if (permission === "granted") {
      const token = await messaging.getToken({
        vapidKey: process.env.VAPIDKEY, // Thêm VAPID key của bạn
      });
      console.log("FCM Token:", token);

      // Lưu token vào server (để sau này gửi thông báo)
      // Ví dụ gửi token lên server của bạn
      await fetch("/api/save-fcm-token", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ token })
      });
    }
  } catch (error) {
    console.error("Error getting permission or token:", error);
  }
}

// Khi trang web được tải, yêu cầu quyền nhận thông báo
requestNotificationPermission();

// Lắng nghe thông báo khi app đang mở
messaging.onMessage((payload) => {
  console.log("Message received. ", payload);
  // Hiển thị thông báo trên giao diện người dùng (web admin)
  showNotificationOnAdminDashboard(payload.notification);
});

// Hàm hiển thị thông báo trên giao diện
// Hàm hiển thị thông báo và tự động xóa sau một khoảng thời gian
function showNotificationOnAdminDashboard(notification) {
  const notificationArea = document.querySelector(".notification-list");
  const notificationCount = document.querySelector(".notification .num");
  notificationCount.textContent = parseInt(notificationCount.textContent) + 1;

  const notificationItem = document.createElement("div");
  notificationItem.classList.add("notification-item", "new");
  notificationItem.innerHTML = `
    <span class="notification-title">${notification.title}</span>
    <p class="notification-body">${notification.body}</p>
    <span class="close" onclick="closeNotification(this)">x</span>
  `;

  notificationArea.appendChild(notificationItem);

  // Tự động xóa thông báo sau 5 giây
  setTimeout(() => {
    notificationItem.classList.remove("new"); // Bỏ hiệu ứng pulse
    notificationItem.style.opacity = 0; // Làm mờ thông báo
    setTimeout(() => notificationItem.remove(), 1000); // Xóa thông báo sau 1 giây
    notificationCount.textContent = parseInt(notificationCount.textContent) - 1; // Giảm số lượng thông báo
  }, 5000); // 5000ms = 5 giây
}

// Hàm đóng thông báo thủ công
function closeNotification(closeButton) {
  const notificationItem = closeButton.parentElement;
  notificationItem.remove();
  const notificationCount = document.querySelector(".notification .num");
  notificationCount.textContent = parseInt(notificationCount.textContent) - 1; // Giảm số lượng thông báo
}

//tìm kiếm theo ngày
document.getElementById("searchForm").addEventListener("submit", function (e) {
  e.preventDefault(); // Ngăn chặn hành vi mặc định của Form

  const startDate = document.getElementById("startDate").value;
  const endDate = document.getElementById("endDate").value;

  if (!startDate || !endDate) {
    alert("Vui lòng nhập đầy đủ ngày bắt đầu và ngày kết thúc.");
    return;
  }

  console.log("Start Date:", startDate, "End Date:", endDate); // Kiểm tra trên console
  fetchSearchBookings(startDate, endDate); // Gọi hàm tìm kiếm
});



async function fetchSearchBookings(startDate = "", endDate = "") {
  try {
    console.log("Fetching search results:", startDate, endDate); // Kiểm tra xem hàm có được gọi không

    const url = `/api/searchbookings?startDate=${startDate}&endDate=${endDate}`;
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error("Failed to fetch bookings");
    }

    const bookings = await response.json();
    console.log("Fetched Bookings:", bookings); // Kiểm tra phản hồi từ API

    const tableBody = document.getElementById("BookingTableBody");
    tableBody.innerHTML = ""; // Xóa bảng trước khi render mới

    bookings.forEach((booking, index) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${index + 1}</td>
        <td>${booking.userId?.name || "Unknown"}</td>
        <td>${booking.roomId?.name || "Unknown"}</td>
        <td>${booking.totalPrice.toLocaleString()} VND</td>
        <td>${new Date(booking.startDate).toLocaleDateString("vi-VN")}</td>
        <td>${new Date(booking.endDate).toLocaleDateString("vi-VN")}</td>
        <td>${booking.userId?.phonenumber || "Unknown"}</td>
        <td>${booking.status}</td>
      `;
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Lỗi khi tìm kiếm booking:", error);
    alert("Không thể tìm kiếm booking theo ngày.");
  }
}


