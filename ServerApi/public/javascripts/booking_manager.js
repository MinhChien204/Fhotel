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
          <td>${booking.totalPrice}</td>
          <td>${booking.startDate}</td>
          <td>${booking.endDate}</td>
          <td>${booking.userId?.phonenumber}</td>
          <td>
            <select
              class="status-dropdown"
              data-booking-id="${booking._id}"
              onchange="updateBookingStatus('${booking._id}', this.value)"
            >
              <option value="pending" ${booking.status === "pending" ? "selected" : ""}>Pending</option>
              <option value="confirmed" ${booking.status === "confirmed" ? "selected" : ""}>Confirmed</option>
              <option value="cancelled" ${booking.status === "cancelled" ? "selected" : ""}>Cancelled</option>
            </select>
          </td>
          <td>
            <button onclick="deleteBooking('${booking._id}')">Delete</button>
          </td>
        `;
        tableBody.appendChild(row);
      });
    } catch (error) {
      console.error("Failed to fetch bookings:", error);
    }
  }
  
  
  // Call the function to populate the table when the page loads
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
  