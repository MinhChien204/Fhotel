// room_manager.js

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
      <td>${room.status}</td>
      <td>
        <button class="btn-edit" onclick="editRoom(${room._id})">Edit</button>
        <button class="btn-delete" onclick="deleteRoom(${room._id})">Delete</button>
      </td>
    `;

    tableBody.appendChild(row);
  });
}

// Function to edit room (not implemented here)
function editRoom(roomId) {
  console.log('Editing room', roomId);
  // Implement room editing logic here
}

// Function to delete room (not implemented here)
function deleteRoom(roomId) {
  console.log('Deleting room', roomId);
  // Implement room deletion logic here
}
