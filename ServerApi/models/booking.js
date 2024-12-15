const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const bookingSchema = new Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: "user", required: true },
  roomId: { type: mongoose.Schema.Types.ObjectId, ref: "room", required: true },
  startDate: { type: String, required: true },
  endDate: { type: String, required: true },
  totalPrice: { type: Number, required: true },
  status: { type: String, enum: ["pending", "confirmed", "cancelled"], default: "pending" },
  paymentStatus: { type: String, enum: ["unpaid", "paid"], default: "unpaid" }, // Trạng thái thanh toán
  createdAt: { type: Date, default: Date.now },
});

module.exports = mongoose.model("booking", bookingSchema);
