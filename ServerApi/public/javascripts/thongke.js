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
}

// Gọi hàm fetch dữ liệu khi tải trang
fetchStats();