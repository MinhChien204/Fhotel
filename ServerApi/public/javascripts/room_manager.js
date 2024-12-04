document.addEventListener("DOMContentLoaded", () => {
  fetchRooms();
});

const roomFormModal = document.getElementById("roomFormModal");
const roomForm = document.getElementById("roomForm");
const imageIcon = document.querySelector('.bi-image');
const imagePreviewContainer = document.querySelector('.image-preview-container'); // Get the existing preview container


let imageFiles = [];
imageIcon.addEventListener('click', () => {
  const imageInput = document.createElement('input');
  imageInput.type = 'file';
  imageInput.multiple = true;
  imageInput.accept = 'image/*'; // Limit to image types
  imageInput.style.display = 'none'; // Hide the input

  document.body.appendChild(imageInput);
  imageInput.click();

  imageInput.onchange = () => {
    const files = imageInput.files;
    imageFiles = Array.from(files); // Convert FileList to Array

    // Clear previous previews
    imagePreviewContainer.innerHTML = '';

    // Display selected images in the HTML
    imageFiles.forEach((file) => {
      const img = document.createElement('img');
      img.src = URL.createObjectURL(file);
      img.style.width = '100px'; // Set a fixed width for previews
      img.style.marginRight = '10px'; // Spacing between images
      img.style.borderRadius = '5px'; // Rounded corners
      imagePreviewContainer.appendChild(img);
    });

    // Clean up
    document.body.removeChild(imageInput);
  };
});

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
function displayRooms(rooms) {
  const tableBody = document.getElementById("roomTableBody");
  tableBody.innerHTML = ""; // Clear the table before populating

  rooms.forEach((room, index) => {
    const row = document.createElement("tr");

    // Create star icons based on rating
    const stars = createStars(room.rating);

    row.innerHTML = `
      <td>${index + 1}</td>
      <td><img src="${room.image}" alt="${room.name}" width="100" height="100" /></td>
      <td>${room.name}</td>
      <td>$${room.price}</td>
      <td>${stars}</td>
      
      <td>${room.room_code}</td>
      <td>${room.description}</td>
      <td>${room.capacity}</td>
    <td class="status-cell">
  <select
    class="status-dropdown"
    data-room-id="${room._id}"
    onchange="updateRoomStatus('${room._id}', this.value)"
  >
    <option value="unavailable" ${room.status === "unavailable" ? "selected" : ""
      }>Unavailable</option>
    <option value="available" ${room.status === "available" ? "selected" : ""
      }>Available</option>
  </select>
</td>

      <td class="actions-cell">
        <button class="btn-edit" onclick="editRoom('${room._id}')">Edit</button>
        <button class="btn-delete" onclick="deleteRoom('${room._id}')">Delete</button>
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

function openModal(room = null) {
  roomFormModal.style.display = "flex";

  // Gọi API để lấy danh sách dịch vụ
  fetch("/api/services")
    .then((response) => response.json())
    .then((data) => {
      if (data.status === 200) {
        populateServiceOptions(data.data);  // Truyền mảng dịch vụ vào hàm

        // Nếu có phòng đã được chọn, hãy gán các dịch vụ đã chọn cho checkbox
        if (room && room.services) {
          const selectedServices = room.services.map(service => service._id);
          setSelectedServices(selectedServices);
        }
      } else {
        console.error("Failed to fetch services:", data.message);
      }
    })
    .catch((error) => console.error("Error fetching services:", error));

  if (room) {
    document.getElementById("roomId").value = room._id;
    document.getElementById("roomName").value = room.name;
    document.getElementById("roomPrice").value = room.price;
    document.getElementById("roomRating").value = room.rating;
    document.getElementById("roomCode").value = room.room_code;
    document.getElementById("roomDescription").value = room.description;
    document.getElementById("roomCapacity").value = room.capacity;
    document.getElementById("roomStatus").value = room.status;
  } else {
    document.getElementById("roomId").value = "";
    roomForm.reset();
  }
}



function populateServiceOptions(responseData) {
  const serviceContainer = document.getElementById("roomServices");
  serviceContainer.innerHTML = ''; // Xóa tất cả checkbox trước khi thêm mới

  if (responseData && Array.isArray(responseData)) {
    responseData.forEach(service => {
      const label = document.createElement("label");
      label.textContent = service.name;  // Tên dịch vụ

      const checkbox = document.createElement("input");
      checkbox.type = "checkbox";
      checkbox.name = "service"; // Tên của checkbox (giống tên trong form)
      checkbox.value = service._id;  // ID của dịch vụ là giá trị của checkbox

      // Kiểm tra nếu dịch vụ này đã được chọn trong phòng (chỉ cần tham số `room.services`)
      checkbox.checked = false;

      // Thêm checkbox vào label
      label.appendChild(checkbox);

      const div = document.createElement("div");
      div.appendChild(label);

      serviceContainer.appendChild(div);
    });
  } else {
    console.error("Invalid services data:", responseData);
  }
}

function setSelectedServices(selectedServices) {
  const serviceSelect = document.getElementById("roomServices");

  // Kiểm tra tất cả các checkbox và đánh dấu những cái được chọn
  Array.from(serviceSelect.querySelectorAll("input[name='service']")).forEach(checkbox => {
    // Nếu giá trị checkbox có trong selectedServices, đánh dấu checkbox đó
    if (selectedServices && selectedServices.includes(checkbox.value)) {
      checkbox.checked = true;
    } else {
      checkbox.checked = false;
    }
  });
}

// Close the modal
function closeModal() {
  roomFormModal.style.display = "none";
  roomForm.reset();
}

// Handle form submission for adding/updating a room
roomForm.addEventListener("submit", (event) => {
  event.preventDefault();

  const roomId = document.getElementById("roomId").value;

  const name = document.getElementById("roomName").value,
    price = document.getElementById("roomPrice").value,
    rating = document.getElementById("roomRating").value,
    description = document.getElementById("roomDescription").value,
    capacity = document.getElementById("roomCapacity").value,
    roomcode = document.getElementById("roomCode").value,
    status = document.getElementById("roomStatus").value;

  const roomData = new FormData(roomForm);
  roomData.append('name', name);
  roomData.append('price', price);
  roomData.append('rating', rating);
  roomData.append('room_code', roomcode);
  roomData.append('description', description);
  roomData.append('capacity', capacity);
  roomData.append('status', status);

  const selectedServices = [];
  document.querySelectorAll('input[name="service"]:checked').forEach(checkbox => {
    selectedServices.push(checkbox.value);
  });
  const servicesString = selectedServices.join(',');

  roomData.append('services', servicesString);

  imageFiles.forEach((image) => {
    roomData.append('image', image);
  });

  const endpoint = roomId ? `/api/update_room/${roomId}` : '/api/add_room';
  const method = roomId ? 'PUT' : 'POST';

  // Gửi yêu cầu đến server để tạo hoặc cập nhật dịch vụ
  fetch(endpoint, {
    method: method,
    body: roomData,
  })
    .then((response) => {
      if (!response.ok) {
        return response.json().then((errorData) => {
          throw new Error(errorData.message || 'Unknown error');
        });
      }
      return response.json();
    })
    .then((data) => {
      if (data.status === 200) {
        alert(`${roomId ? "Room updated" : "Room created"} successfully!`);
        closeModal();
        roomForm.reset();
        fetchRooms();
      } else {
        alert(`${roomId ? "Failed to update" : "Failed to create"} Room.`);
      }
    })
    .catch((error) => {
      console.error("Error creating/updating Room:", error);
      alert(`An error occurred while processing your request: ${error.message}`);
    });
});


// Edit room functionality 
function editRoom(roomId) {
  fetch(`/api/room/${roomId}`)
    .then((response) => response.json())
    .then((data) => {
      console.log(data)
      if (data.status === 200) {
        openModal(data.data);
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
