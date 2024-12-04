const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const UserVoucher = new Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: "user", required: true },
    voucherId: { type: mongoose.Schema.Types.ObjectId, ref: "voucher", required: true },
}, {
    timestamps: true
});

module.exports = mongoose.model('uservoucher', UserVoucher); // Đặt tên model viết hoa 'Room' để nhất quán với tên file
