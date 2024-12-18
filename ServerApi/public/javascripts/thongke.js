// Lấy dữ liệu từ API và render ra bảng
async function fetchStats() {
    try {
        // Tổng doanh thu khách sạn
        const totalRevenueResponse = await fetch('/api/total-revenue');
        const totalRevenueData = await totalRevenueResponse.json();
        const totalRevenueElement = document.getElementById('total-revenue');
        totalRevenueElement.innerHTML = `Tổng doanh thu khách sạn: <strong>${totalRevenueData.totalRevenue.toLocaleString()} VNĐ</strong>`;

        // Thống kê số lượng đặt phòng theo trạng thái
        const statusResponse = await fetch('/api/booking-stats');
        const statusData = await statusResponse.json();
        const statusTable = document.getElementById('status-stats');

        statusData.forEach(stat => {
            const row = `<tr>
                <td>${stat._id}</td>
                <td>${stat.count}</td>
            </tr>`;
            statusTable.innerHTML += row;
        });

        // Thống kê doanh thu theo phòng
        const revenueResponse = await fetch('/api/revenue-by-room');
        const revenueData = await revenueResponse.json();
        const revenueTable = document.getElementById('revenue-stats');

        revenueData.forEach(room => {
            const row = `<tr>
                <td>${room.roomInfo.name}</td>
                <td>${room.totalRevenue.toLocaleString()} VNĐ</td>
            </tr>`;
            revenueTable.innerHTML += row;
        });

        // Phòng phổ biến nhất
        const popularResponse = await fetch('/api/popular-rooms');
        const popularData = await popularResponse.json();
        const popularTable = document.getElementById('popular-rooms');

        popularData.forEach(room => {
            const row = `<tr>
                <td>${room.roomInfo.name}</td>
                <td>${room.bookings}</td>
                <td><img src="${room.roomInfo.image}" alt="${room.roomInfo.name}" style="width:100px;height:auto;"></td>
            </tr>`;
            popularTable.innerHTML += row;
        });

    } catch (error) {
        console.error('Error fetching statistics:', error);
    }
    document.getElementById('filter-btn').addEventListener('click', async () => {
        const startDate = document.getElementById('start-date').value;
        const endDate = document.getElementById('end-date').value;

        // Kiểm tra nếu người dùng đã nhập ngày hợp lệ
        if (!startDate || !endDate) {
            alert('Vui lòng chọn cả ngày bắt đầu và ngày kết thúc!');
            return;
        }

        try {
            // Gọi API để lấy doanh thu theo ngày
            const response = await fetch(`/api/revenue?startDate=${startDate}&endDate=${endDate}`);
            const data = await response.json();

            // Kiểm tra nếu dữ liệu trả về không có doanh thu
            if (data.revenueStats.length === 0) {
                alert('Không có dữ liệu doanh thu trong khoảng thời gian này!');
                return;
            }

            // Hiển thị doanh thu theo ngày
            const revenueStats = data.revenueStats;
            let revenueHTML = '';

            revenueStats.forEach(stat => {
                revenueHTML += `
              <tr>
                <td>${stat._id}</td>
                <td>${stat.totalRevenue.toLocaleString()} VNĐ</td>
              </tr>
            `;
            });

            // Cập nhật bảng doanh thu
            document.getElementById('revenue-stats').innerHTML = revenueHTML;

            // Cập nhật tổng doanh thu
            const totalRevenue = revenueStats.reduce((total, stat) => total + stat.totalRevenue, 0);
            document.getElementById('total-revenue').innerHTML = `
             Doanh thu : <span>${totalRevenue.toLocaleString()} VNĐ</span>
          `;
        } catch (error) {
            console.error('Lỗi khi lấy dữ liệu doanh thu:', error);
            alert('Không thể lấy dữ liệu doanh thu!');
        }
    });
}

// Gọi hàm fetch dữ liệu khi tải trang
fetchStats();

