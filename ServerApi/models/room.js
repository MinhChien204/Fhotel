const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const Room = new Schema({
    name: { type: String, required: true },
    price: { type: Number, required: true },  // Đổi sang Number nếu `price` là số
    rating: { type: Number, required: true },
    description: { type: String, required: true },
    image: { type: String, required: true },
    capacity: { type: Number, required: true },
    status: { type: String, required: true },  // Chuyển sang String để lưu trạng thái như "available"
    room_code: { type: String, required: true },
    services: [{ type: mongoose.Schema.Types.ObjectId, ref: 'service' }]
}, {
    timestamps: true
});

module.exports = mongoose.model('room', Room); // Đặt tên model viết hoa 'Room' để nhất quán với tên file
