const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const Favourite = new Schema({
    roomId: { type: mongoose.Schema.Types.ObjectId, ref: "room", required: true },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: "user", required: true },
   
}, {
    timestamps: true
});

module.exports = mongoose.model('favourite', Favourite); // Đặt tên model viết hoa 'Room' để nhất quán với tên file
