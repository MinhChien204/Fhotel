const mongoose = require("mongoose");
const Schema = mongoose.Schema;
const Notification = new Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: "user", required: true },
    message: {type: String,required: true},
    type: {type: String,enum: ["password_change", "booking_confirmed", "booking_cancelled", "voucher_received"],required: true},
    createdAt: {type: Date,default: Date.now},
}, {
    timestamps: true
});

module.exports = mongoose.model("notification", Notification);
