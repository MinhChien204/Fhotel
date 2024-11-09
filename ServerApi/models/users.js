const mongoose = require('mongoose');
const Scheme = mongoose.Schema;

const Users = new Scheme({
    username: { type: String, unique: true,  required: true},
    password: { type: String },
    email: { type: String, unique: true },
    name: { type: String },
    gioitinh:{type:String},
    address:{type:String},
    avartar: { type: String },
    available: { type: Boolean, default: false },
    phonenumber:{type:Number},
    role: { type: Number,  required: true }
}, {
    timestamps: true
}
)

module.exports = mongoose.model('user', Users)