const mongoose = require('mongoose');

const typeroomSchema = new mongoose.Schema({
    name: { type: String, required: true },
    imageRoom: { type: String, required: true }
});

module.exports = mongoose.model('typeRooms', typeroomSchema);
