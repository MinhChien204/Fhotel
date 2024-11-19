const mongoose = require('mongoose');
const Scheme = mongoose.Schema;

const Users = new Scheme({
    username: { type: String, unique: true,  required: true},
    password: { type: String },
    email: { type: String, unique: true },
    name: { type: String },
    gender:{type:String},
    address:{type:String},
    avatar: { type: String },
    available: { type: Boolean, default: false },
    phonenumber:{type:String},
    birthday:{type:String},
    role: { type: Number,  required: true,default: 1 }
}, {
    timestamps: true
}
)

module.exports = mongoose.model('user', Users)