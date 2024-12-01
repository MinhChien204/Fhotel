const allSideMenu = document.querySelectorAll('#sidebar .side-menu.top li a');

allSideMenu.forEach(item=> {
	const li = item.parentElement;

	item.addEventListener('click', function () {
		allSideMenu.forEach(i=> {
			i.parentElement.classList.remove('active');
		})
		li.classList.add('active');
	})
});




// TOGGLE SIDEBAR
const menuBar = document.querySelector('#content nav .bx.bx-menu');
const sidebar = document.getElementById('sidebar');

menuBar.addEventListener('click', function () {
	sidebar.classList.toggle('hide');
})







const searchButton = document.querySelector('#content nav form .form-input button');
const searchButtonIcon = document.querySelector('#content nav form .form-input button .bx');
const searchForm = document.querySelector('#content nav form');

searchButton.addEventListener('click', function (e) {
	if(window.innerWidth < 576) {
		e.preventDefault();
		searchForm.classList.toggle('show');
		if(searchForm.classList.contains('show')) {
			searchButtonIcon.classList.replace('bx-search', 'bx-x');
		} else {
			searchButtonIcon.classList.replace('bx-x', 'bx-search');
		}
	}
})





if(window.innerWidth < 768) {
	sidebar.classList.add('hide');
} else if(window.innerWidth > 576) {
	searchButtonIcon.classList.replace('bx-x', 'bx-search');
	searchForm.classList.remove('show');
}


window.addEventListener('resize', function () {
	if(this.innerWidth > 576) {
		searchButtonIcon.classList.replace('bx-x', 'bx-search');
		searchForm.classList.remove('show');
	}
})



const switchMode = document.getElementById('switch-mode');

switchMode.addEventListener('change', function () {
	if(this.checked) {
		document.body.classList.add('dark');
	} else {
		document.body.classList.remove('dark');
	}
})

function logout() {
	// Xóa token khỏi localStorage
	localStorage.removeItem("token");

	// Gửi request logout đến server (nếu bạn cần thu hồi token trên server)
	fetch("/logout", {
	  method: "POST",
	  headers: {
		"Authorization": `Bearer ${localStorage.getItem("token")}`,
	  },
	})
	.then(response => response.json())
	.then(data => {
	  console.log(data.message); // Thông báo đăng xuất thành công từ server

	  // Hiển thị thông báo đăng xuất thành công bằng SweetAlert2
	  Swal.fire({
		title: 'Đăng xuất thành công!',
		text: 'Bạn sẽ được chuyển đến trang đăng nhập.',
		icon: 'success',
		confirmButtonText: 'OK'
	  }).then(() => {
		// Điều hướng đến trang login sau khi đóng thông báo
		window.location.href = "/"; // Điều hướng đến trang login
	  });
	})
	.catch(error => {
	  console.error("Error during logout:", error);
	  // Hiển thị thông báo lỗi khi không thể đăng xuất
	  Swal.fire({
		title: 'Có lỗi xảy ra!',
		text: 'Không thể đăng xuất. Vui lòng thử lại sau.',
		icon: 'error',
		confirmButtonText: 'OK'
	  });
	});
  }

  //booking tables
  // Fetch booking data from the API
async function fetchBookings() {
    try {
      const response = await fetch("/api/bookings"); // Adjust the URL if necessary
      const bookings = await response.json();
  
      const tableBody = document.getElementById("IndexTableBody");
      tableBody.innerHTML = ""; // Clear the table before rendering
  
      bookings.forEach((booking, index) => {
        const row = document.createElement("tr");
        row.innerHTML = `
		  <td><img src="${booking.userId?.avatar}" alt=""></td>

          <td>${booking.userId?.name}</td>
          <td>${booking.startDate}</td>
          <td class="status">${booking.status}</td>
        
        `;
        tableBody.appendChild(row);
      });
    } catch (error) {
      console.error("Failed to fetch bookings:", error);
    }
  }
  
  
  // Call the function to populate the table when the page loads
  fetchBookings();