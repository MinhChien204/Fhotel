document.addEventListener("DOMContentLoaded", () => {
  fetchRooms();
});

const roomFormModal = document.getElementById("roomFormModal");
const roomForm = document.getElementById("roomForm");

// Fetch rooms from the backend
function fetchRooms() {
  fetch("/api/rooms") // Endpoint for getting room data
    .then((response) => response.json())
    .then((data) => {
      if (data.status === 200) {
        displayRooms(data.data); // Populate the table with fetched data
      } else {
        console.error("Error fetching rooms:", data.message);
      }
    })
    .catch((error) => console.error("Error fetching data:", error));
}

// Display the rooms in the table
// Display the rooms in the table
function displayRooms(rooms) {
  const tableBody = document.getElementById("roomTableBody");
  tableBody.innerHTML = ""; // Clear the table before populating

  rooms.forEach((room, index) => {
    const row = document.createElement("tr");

    // Create star icons based on rating
    const stars = createStars(room.rating);

    row.innerHTML = `
      <td>${index + 1}</td>
      <td><img src="${room.image}" alt="${room.name}" width="50" height="50" /></td>
      <td>${room.name}</td>
      <td>$${room.price}</td>
      <td>${stars}</td>
      <td>${room.description}</td>
      <td>${room.capacity}</td>
    <td class="status-cell">
  <select
    class="status-dropdown"
    data-room-id="${room._id}"
    onchange="updateRoomStatus('${room._id}', this.value)"
  >
    <option value="unavailable" ${
      room.status === "unavailable" ? "selected" : ""
    }>Unavailable</option>
    <option value="available" ${
      room.status === "available" ? "selected" : ""
    }>Available</option>
  </select>
</td>

      <td class="actions-cell">
        <button class="btn-edit" onclick="editRoom('${room._id}')">Edit</button>
        <button class="btn-delete" onclick="deleteRoom('${
          room._id
        }')">Delete</button>
      </td>
    `;

    tableBody.appendChild(row);
  });
}

// Helper function to generate star icons for ratings
function createStars(rating) {
  const fullStar = '<i class="bx bxs-star" style="color: gold;"></i>';
  const halfStar = '<i class="bx bxs-star-half" style="color: gold;"></i>';
  const emptyStar = '<i class="bx bx-star" style="color: gold;"></i>';

  let starsHTML = "";

  // Add full stars
  for (let i = 1; i <= Math.floor(rating); i++) {
    starsHTML += fullStar;
  }

  // Add half star if the rating has a decimal
  if (rating % 1 !== 0) {
    starsHTML += halfStar;
  }

  // Add empty stars to fill up to 5 stars
  const emptyStarsCount = 5 - Math.ceil(rating);
  for (let i = 1; i <= emptyStarsCount; i++) {
    starsHTML += emptyStar;
  }

  return starsHTML;
}

// Open the modal for adding/editing rooms
function openModal(room = null) {
  roomFormModal.style.display = "flex";

  if (room) {
    document.getElementById("roomId").value = room._id;
    document.getElementById("roomImage").value = room.image;
    document.getElementById("roomName").value = room.name;
    document.getElementById("roomPrice").value = room.price;
    document.getElementById("roomRating").value = room.rating;
    document.getElementById("roomDescription").value = room.description;
    document.getElementById("roomCapacity").value = room.capacity;
    document.getElementById("roomStatus").value = room.status;
  } else {
    document.getElementById("roomId").value = "";
    roomForm.reset();
  }
}

// Close the modal
function closeModal() {
  roomFormModal.style.display = "none";
}

// Handle form submission for adding/updating a room
roomForm.addEventListener("submit", (event) => {
  event.preventDefault();

  const roomId = document.getElementById("roomId").value;
  const roomData = {
    image: document.getElementById("roomImage").value,
    name: document.getElementById("roomName").value,
    price: document.getElementById("roomPrice").value,
    rating: document.getElementById("roomRating").value,
    description: document.getElementById("roomDescription").value,
    capacity: document.getElementById("roomCapacity").value,
    status: document.getElementById("roomStatus").value,
  };

  if (roomId) {
    // Update room
    fetch(`/api/update_room/${roomId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(roomData),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.status === 200) {
          alert("Room updated successfully!");
          closeModal();
          fetchRooms();
        } else {
          alert("Failed to update room.");
        }
      })
      .catch((error) => console.error("Error updating room:", error));
  } else {
    // Create new room
    fetch(`/api/add_room`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(roomData),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.status === 200) {
          alert("Room created successfully!");
          closeModal();
          fetchRooms();
        } else {
          alert("Failed to create room.");
        }
      })
      .catch((error) => console.error("Error creating room:", error));
  }
});

// Edit room functionality
function editRoom(roomId) {
  fetch(`/api/room/${roomId}`)
    .then((response) => response.json())
    .then((data) => {
      if (data.status === 200) {
        openModal(data.room);
      } else {
        alert("Failed to fetch room data.");
      }
    })
    .catch((error) => console.error("Error fetching room data:", error));
}

// Delete room functionality
function deleteRoom(roomId) {
  if (confirm("Are you sure you want to delete this room?")) {
    fetch(`/api/delete_room/${roomId}`, { method: "DELETE" })
      .then((response) => response.json())
      .then((data) => {
        if (data.status === 200) {
          alert("Room deleted successfully!");
          fetchRooms();
        } else {
          alert("Failed to delete room.");
        }
      })
      .catch((error) => console.error("Error deleting room:", error));
  }
}

// Update room status
function updateRoomStatus(roomId, newStatus) {
  const dropdown = document.querySelector(`[data-room-id="${roomId}"]`);
  dropdown.disabled = true; // Temporarily disable dropdown during update

  fetch(`/api/update-status-room/${roomId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ status: newStatus }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.message === "room update successfully") {
        alert("Room status updated successfully!");
        fetchRooms();
      } else {
        alert(`Error: ${data.message}`);
      }
    })
    .catch((error) => {
      console.error("Error updating room status:", error);
      alert("An error occurred while updating room status.");
    })
    .finally(() => {
      dropdown.disabled = false; // Re-enable dropdown after update
    });
}

document.getElementById("addRoomBtn").addEventListener("click", () => {
  openModal();
});
