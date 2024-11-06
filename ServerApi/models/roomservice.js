// models/RoomService.js
const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const RoomServiceSchema = new Schema({
    roomId: { type: Schema.Types.ObjectId, ref: 'room', required: true },
    serviceId: { type: Schema.Types.ObjectId, ref: 'service', required: true }
}, {
    timestamps: true
});

module.exports = mongoose.model('roomservice', RoomServiceSchema);
