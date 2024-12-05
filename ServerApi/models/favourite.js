const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const Favourite = new Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: "user", required: true },
    roomId: { type: mongoose.Schema.Types.ObjectId, ref: "room", required: true },
    favouritestatus: { type: Number, required: true, default: 0 }, // chuyển trạng thái yêu thích (0:1)
    
   
}, {
    timestamps: true
});

module.exports = mongoose.model('favourite', Favourite); // Đặt tên model viết hoa 'Room' để nhất quán với tên file
