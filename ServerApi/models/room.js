const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const Room = new Schema({
    name: { type: String, required: true },         // Tên phòng
    price: { type: Number, required: true },        // Giá phòng
    rating: { type: Number, required: true },       // Đánh giá phòng
    description: { type: String, required: true },  // Mô tả phòng
    image: { type: Array, required: true },      //  URL ảnh phòng
    capacity: { type: Number, required: true },     // Sức chứa phòng
    status: { type: String, default: "available" }, // Giá trị mặc định là "available", Trạng thái phòng (ví dụ: "available", "occupied")
    room_code: { type: String, required: true },    // Mã phòng (ví dụ: "D123")
}, {
    timestamps: true
});

module.exports = mongoose.model('room', Room);
