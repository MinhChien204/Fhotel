document.addEventListener("DOMContentLoaded", () => {
    fetchService();
});

const FormModal = document.getElementById("serviceFormModal");
const Form = document.getElementById("serviceForm");
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
            img.onload = () => {
                URL.revokeObjectURL(objectURL);
            };
        });

        // Clean up
        document.body.removeChild(imageInput);
    };
});

function fetchService() {
    fetch("/api/services")
        .then((response) => response.json())
        .then((data) => {
            if (data.status === 200) {
                displayService(data.data); // Populate the table with fetched data
            } else {
                console.error("Error fetching services:", data.message);
            }
        })
        .catch((error) => console.error("Error fetching data:", error));
}

// Display the rooms in the table
function displayService(services) {
    const tableBody = document.getElementById("serviceTableBody");
    tableBody.innerHTML = ""; // Clear the table before populating

    services.forEach((service, index) => {
        const row = document.createElement("tr");

        row.innerHTML = `
        <td>${index + 1}</td>
        <td><img src="${service.image}" alt="${service.name}" width="50" height="50" /></td>
        <td>${service.name}</td>

        <td class="actions-cell">
          <img class="btn-edit" src="./img/edit.png" style="width:20px;height:20px;margin:20px" onclick="editService('${service._id}')"></img>
        </td>
      `;

        tableBody.appendChild(row);
    });
}

// Open the modal for adding/editing services
function openModal(service = null) {
    FormModal.style.display = "flex";

    if (service) {
        // Khi chỉnh sửa, điền thông tin vào form
        document.getElementById("serviceId").value = service._id;  // Điền serviceId vào input ẩn
        document.getElementById("name").value = service.name;
        document.getElementById("price").value = service.price;
        document.getElementById("description").value = service.description;
    } else {
        // Khi tạo mới, reset form
        document.getElementById("serviceId").value = "";
        Form.reset();
    }
}


// Close the modal
function closeModal() {
    FormModal.style.display = "none";
    Form.reset();
}

// Handle form submission for adding/updating a room
Form.addEventListener("submit", (event) => {
    event.preventDefault();

    const name = document.getElementById("name").value;
    const serviceId = document.getElementById("serviceId").value;  // Lấy serviceId
    if (!name || !price) {
        alert("Name and Price are required!");
        return; // Ngừng việc gửi form nếu thiếu trường bắt buộc
    }

    const serviceData = new FormData(Form);
    serviceData.append('name', name);

    // Thêm các hình ảnh vào FormData (nếu có)
    imageFiles.forEach((image) => {
        serviceData.append('image', image);
    });

    // Log dữ liệu FormData để kiểm tra
    for (let pair of serviceData.entries()) {
        console.log(pair[0] + ': ' + pair[1]);
    }

    // Nếu có serviceId, sử dụng PUT để cập nhật, nếu không sử dụng POST để thêm mới
    const endpoint = serviceId ? `/api/update_service/${serviceId}` : '/api/add-service';
    const method = serviceId ? 'PUT' : 'POST';

    // Gửi yêu cầu đến server để tạo hoặc cập nhật dịch vụ
    fetch(endpoint, {
        method: method,
        body: serviceData,
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
                alert(`${serviceId ? "Service updated" : "Service created"} successfully!`);
                closeModal();
                fetchService(); // Refresh the service list
            } else {
                alert(`${serviceId ? "Failed to update" : "Failed to create"} service.`);
            }
        })
        .catch((error) => {
            console.error("Error creating/updating service:", error);
            alert(`An error occurred while processing your request: ${error.message}`);
        });
});


function editService(serviceId) {
    fetch(`/api/service/${serviceId}`)
        .then((response) => response.json())
        .then((data) => {
            console.log(data);  // Kiểm tra dữ liệu nhận được
            if (data.status === 200) {
                openModal(data.service); // Mở modal với thông tin dịch vụ
            } else {
                alert("Failed to fetch service data.");
            }
        })
        .catch((error) => console.error("Error fetching service data:", error));
}

// Delete service functionality
function deleteService(serviceId) {
    if (confirm("Are you sure you want to delete this service?")) {
        fetch(`/api/delete-service/${serviceId}`, { method: "DELETE" })
            .then((response) => response.json())
            .then((data) => {
                if (data.status === 200) {
                    alert("Service deleted successfully!");
                    fetchService(); // Refresh the service list
                } else {
                    alert("Failed to delete service.");
                }
            })
            .catch((error) => console.error("Error deleting service:", error));
    }
}

document.getElementById("addServiceBtn").addEventListener("click", () => {
    openModal();
});
