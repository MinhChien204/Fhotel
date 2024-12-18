document.addEventListener('DOMContentLoaded', () => {
  // Lấy danh sách hóa đơn từ API
  fetch('/api/bills')
    .then(response => response.json())
    .then(data => {
      if (data.status === 200) {
        displayBills(data.data); // Gọi hàm để hiển thị hóa đơn
      } else {
        console.error('Không lấy được dữ liệu hóa đơn');
      }
    })
    .catch(error => {
      console.error('Lỗi khi lấy dữ liệu hóa đơn:', error);
    });

    // function formatDate(dateString) {
    //   const dateObj = new Date(dateString);
    //   <td>${formatDate(bill.createdAt)}</td>
    //   return dateObj.toLocaleString('vi-VN', {
    //     day: '2-digit',
    //     month: '2-digit',
    //     year: 'numeric',
    //     hour: '2-digit',
    //     minute: '2-digit',
    //     second: '2-digit',
    //     timeZone: 'UTC' // Đảm bảo múi giờ UTC
    //   });
    // }

  // Hàm hiển thị hóa đơn lên bảng
  function displayBills(bills) {
    const tbody = document.querySelector('#bills-table');
    tbody.innerHTML = ''; // Xóa nội dung cũ trong tbody

    bills.forEach(bill => {
      const row = document.createElement('tr');
      
      // Tạo các ô trong bảng cho mỗi hóa đơn
      row.innerHTML = `
        <td>${bill._id}</td>
        <td>${bill.userId?.name}</td>
        <td>${bill.roomId?.name}</td>
        <td>${bill.startDate}</td>
        <td>${bill.endDate}</td>
        <td>${bill.totalPrice.toLocaleString()}</td>
        <td>${bill.status === 'confirmed' ? 'Đã thanh toán' : bill.status}</td>

      `;
      
      // Thêm hàng vào bảng
      tbody.appendChild(row);
    });
  }
});
