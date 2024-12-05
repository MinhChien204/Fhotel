const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const Favourite = new Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: "user", required: true },
    roomId: { type: mongoose.Schema.Types.ObjectId, ref: "room", required: true },
    status: { type: Number, required: true, default: 1 } // 1: Đã yêu thích, 0: Bỏ yêu thích
}, {
    timestamps: true
});

module.exports = mongoose.model('favourite', Favourite);