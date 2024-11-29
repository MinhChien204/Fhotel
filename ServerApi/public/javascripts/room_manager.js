document.addEventListener("DOMContentLoaded", () => {
  fetchRooms();
});

// Fetch rooms from the backend
function fetchRooms() {
  fetch('/api/rooms') // Endpoint where the backend serves the room data
    .then(response => response.json())
    .then(data => {
      if (data.status === 200) {
        displayRooms(data.data); // Call function to display rooms
      } else {
        console.error("Error fetching rooms:", data.message);
      }
    })
    .catch(error => console.error("Error fetching data:", error));
}

// Display the rooms in the table
function displayRooms(rooms) {
  const tableBody = document.getElementById('roomTableBody');
  tableBody.innerHTML = ""; // Clear any existing data

  rooms.forEach((room, index) => {
    const row = document.createElement('tr');

    row.innerHTML = `
      <td>${index + 1}</td>
      <td><img src="${room.image}" alt="${room.name}" width="50" height="50" /></td>
      <td>${room.name}</td>
      <td>${room.price}</td>
      <td>${room.rating}</td>
      <td>${room.description}</td>
      <td>${room.capacity}</td>
      <td>
        <select
          class="status-dropdown"
          data-room-id="${room._id}"
          onchange="updateRoomStatus('${room._id}', this.value)"
        >
          <option value="unavailable" ${room.status === "unavailable" ? "selected" : ""}>Unavailable</option>
          <option value="available" ${room.status === "available" ? "selected" : ""}>Available</option>
        </select>
      </td>
      <td>
        <button class="btn-edit" onclick="editRoom('${room._id}')">Edit</button>
        <button class="btn-delete" onclick="deleteRoom('${room._id}')">Delete</button>
      </td>
    `;

    tableBody.appendChild(row);
  });
}

// Update room status
function updateRoomStatus(roomId, newStatus) {
  const dropdown = document.querySelector(`[data-room-id="${roomId}"]`);
  dropdown.disabled = true; // Vô hiệu hóa dropdown tạm thời
  fetch(`/api/update-status-room/${roomId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ status: newStatus }),
  })
    .then(response => response.json())
    .then(data => {
      if (data.message === "room update successfully") {
        alert('Trạng thái phòng đã được cập nhật!');
        fetchRooms();
      } else {
        alert(`Lỗi: ${data.message}`);
      }
    })
    .catch(error => {
      console.error('Lỗi khi cập nhật trạng thái phòng:', error);
      alert('Đã xảy ra lỗi khi cập nhật trạng thái phòng.');
    })
    .finally(() => {
      dropdown.disabled = false; // Bật lại dropdown sau khi cập nhật xong
    });
}


// Function to edit room (not implemented yet)

// Function to delete room
function deleteRoom(roomId) {
  if (confirm('Are you sure you want to delete this room?')) {
    fetch(`/api/delete_room/${roomId}`, {
      method: 'DELETE',
    })
      .then(response => response.json())
      .then(data => {
        if (data.status === 200) {
          alert('Room deleted successfully!');
          fetchRooms(); // Refresh the room list
        } else {
          alert('Failed to delete the room.');
        }
      })
      .catch(error => {
        console.error('Error deleting room:', error);
        alert('An error occurred while deleting the room.');
      });
  }
}
