const jwt = require("jsonwebtoken");

const SECRET_KEY = process.env.SECRET_KEY;

// Hàm xác thực token
function authenticateToken(req, res, next) {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return res.status(401).json({ message: "Token không được cung cấp" });
  }

  const token = authHeader.split(" ")[1];

  jwt.verify(token, SECRET_KEY, (err, user) => {
    if (err) {
      return res.status(403).json({ message: "Token không hợp lệ hoặc đã hết hạn" });
    }

    req.user = user; // Gắn thông tin người dùng vào request
    next();
  });
}

// Hàm đăng xuất
function logout(req, res) {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return res.status(400).json({ message: "Token không được cung cấp" });
  }

  const token = authHeader.split(" ")[1];

  // Lưu token vào danh sách bị thu hồi (tùy vào ứng dụng của bạn)
  // revokedTokens.push(token);

  res.status(200).json({ message: "Đăng xuất thành công" });
}

module.exports = {
  authenticateToken,
  logout,
};
