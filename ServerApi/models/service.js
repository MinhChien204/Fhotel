// models/Service.js
const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const ServiceSchema = new Schema({
    name: { type: String, required: true },
    description: { type: String },
    price: { type: Number, required: true }
}, {
    timestamps: true
});

module.exports = mongoose.model('service', ServiceSchema);
