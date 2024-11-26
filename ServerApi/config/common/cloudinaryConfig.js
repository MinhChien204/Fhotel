const cloudinary = require("cloudinary").v2;

// Cấu hình Cloudinary với thông tin tài khoản
cloudinary.config({
  cloud_name: "dqn9fdeku", // Thay bằng Cloud name của bạn
  api_key: "376853648164198", // Thay bằng API Key
  api_secret: "Fx6ioc36q5YrFENpTMV1rZma9rU", // Thay bằng API Secret
});

module.exports = cloudinary;
