const mongoose = require('mongoose');

const TodoSchema = new mongoose.Schema({
  text: {
    type: String,
    required: true,
  },
  status: {
    type: String,
    enum: ['completed', 'pending', 'process'],
    required: true,
  },
});

module.exports = mongoose.model('Todo', TodoSchema);
