const cloudinary = require("cloudinary").v2;

// Cấu hình Cloudinary với thông tin tài khoản
cloudinary.config({
  cloud_name:process.env.CLOUD_NAME, // Thay bằng Cloud name của bạn
  api_key:process.env.API_KEY, // Thay bằng API Key
  api_secret:process.env.API_SECRET, // Thay bằng API Secret
});

module.exports = cloudinary;
