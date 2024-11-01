const mongoose = require('mongoose');
const Scheme = mongoose.Schema;

const Hotels = new Scheme({
    imgHotel: { type: Array },
    tenHotel: { type: String, unique: true},
    chiTietHotel: { type: String },
    soLuongNguoi: { type: Number },
    tienIchPhong: { type: String },  
    danhGiaHotel: { type: Number },  
    diaChiHotel: { type: String },  
    giaHotel: { type: Number },  
    phongBepHotel: { type: Number },  
    phongKhachHotel: { type: Number },  
    phongNguHotel: { type: Number },  
    trangThaiHotel: { type: Boolean, default: false },
    yeuThichHotel: { type: Boolean, default: false },
}, {
    timestamps: true
}
)

module.exports = mongoose.model('hotel', Hotels)