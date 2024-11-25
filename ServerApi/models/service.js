const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const ServiceSchema = new Schema({
    name: { type: String, required: true },
    description: { type: String },
    price: { type: Number, required: true },
    image: { type: Array, required: true }  // Hình ảnh của dịch vụ
}, { timestamps: true });

module.exports = mongoose.model('Service', ServiceSchema);
