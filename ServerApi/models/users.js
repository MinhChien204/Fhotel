const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const Users = new Schema({
    username: { type: String, unique: true, required: true },
    password: { type: String },
    email: { type: String },
    name: { type: String },
    gender: { type: String },
    address: { type: String },
    avatar: { type: String },
    available: { type: Boolean, default: false },
    phonenumber: { type: String },
    birthday: { type: String },
    role: { type: Number, required: true, default: 1 },
    fcmToken: { type: String }, // Thêm trường FCM Token
}, {
    timestamps: true
});

module.exports = mongoose.model('user', Users);
