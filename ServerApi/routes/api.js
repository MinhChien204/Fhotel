var express = require("express");
var router = express.Router();
const { OAuth2Client } = require('google-auth-library');
const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);
const multer = require("multer");
const { CloudinaryStorage } = require('multer-storage-cloudinary');
const cloudinary = require('../config/common/cloudinaryConfig');
const path = require("path");
const admin = require("firebase-admin");
const storage = new CloudinaryStorage({
  cloudinary: cloudinary,
  params: {
    folder: 'Fhotel', // Tên thư mục trong Cloudinary
    allowed_formats: ['jpg', 'png', 'jpeg'], // Định dạng file cho phép
  },
});

const upload = multer({ storage });

const User = require("../models/users");
const Room = require("../models/room");
const Service = require("../models/service");
const Voucher = require("../models/vouchers");
const Booking = require("../models/booking");
const TypeRooms = require("../models/typeRooms");
const Favourite = require("../models/favourite");
const UserVoucher = require("../models/uservoucher");
const Notification = require("../models/notification");
const Bill = require("../models/bill");
const bcrypt = require('bcrypt');
const saltRounds = 10;
const jwt = require('jsonwebtoken');
const SECRET_KEY = process.env.SECRET_KEY;
const AdminFCM = require("../models/AdminFCM");
// hàm thông báo từ administrator
admin.initializeApp({
  credential: admin.credential.cert(__dirname + '/../config/serviceAccountKey.json'), // Điều chỉnh đường dẫn
});



const sendNotification = async (fcmToken, title, body, data = {}) => {
  const message = {
    notification: {
      title: title,
      body: body,
    },
    data: data, // Thêm data nếu cần
    token: fcmToken, // FCM Token của người dùng
  };

  try {
    const response = await admin.messaging().send(message);
    console.log("Notification sent successfully:", response);
    return response;
  } catch (error) {
    console.error("Error sending notification:", error);
    throw error;
  }
};

router.post("/send-notification", async (req, res) => {
  const { userId, title, body } = req.body;

  try {
    // Lấy thông tin user từ database
    const user = await User.findById(userId);
    if (!user || !user.fcmToken) {
      return res.status(404).json({ message: "User or FCM Token not found" });
    }

    // Gửi thông báo
    const response = await sendNotification(user.fcmToken, title, body);
    res.status(200).json({ message: "Notification sent successfully", response });
  } catch (error) {
    res.status(500).json({ message: "Error sending notification", error: error.message });
  }
});


//   CRUD User
//   API hiển thị danh sách người dùng
router.get("/user", async (req, res) => {
  try {
    let user = await User.find();
    res.send(user);
  } catch (error) {
    console.log("error");
  }
});

//API thêm user
router.post("/add_user", async (req, res) => {
  try {
    const {
      username,
      password,
      email,
      name,
      gender,
      birthday,
      phonenumber,
      address,
      avatar,
    } = req.body;

    const newUser = new User({
      username,
      password,
      email,
      name,
      gender,
      birthday,
      phonenumber,
      address,
      avatar,
    });

    const result = await newUser.save();
    res.status(201).json({
      status: 201,
      message: "User added successfully",
      data: result,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while adding user" });
  }
});
//API detail user
router.get("/detail_user/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const user = await User.findById(id);
    if (user) {
      res.status(200).json({
        status: 200,
        message: "User retrieved successfully",
        data: user,
      });
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({ message: "An error occurred while retrieving user" });
  }
});
/// get user by id
router.get("/getuserbyid/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const user = await User.findById(id);
    if (user) {
      res.status(200).json({
        status: 200,
        message: "User retrieved successfully",
        data: user,
      });
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({ message: "An error occurred while retrieving user" });
  }
});

//API update user
router.put("/update_user/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const {
      email,
      name,
      gender,
      birthday,
      phonenumber,
      address,
      avatar,
    } = req.body;

    const updateUser = await User.findByIdAndUpdate(
      id,
      {
        email,
        name,
        gender,
        birthday,
        phonenumber,
        address,
        avatar,
      },
      { new: true }
    );

    if (updateUser) {
      res.status(200).json({
        status: 200,
        message: "User updated successfully",
        data: updateUser,
      });
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while updating user" });
  }
});

// API xác minh mật khẩu cũ và cập nhật mật khẩu mới
router.put("/update_password/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const { oldPassword, newPassword } = req.body;

    // Tìm người dùng theo ID
    const user = await User.findById(id);

    if (!user) {
      return res.status(404).json({ message: "User not found!" });
    }

    // Kiểm tra nếu tài khoản này là tài khoản Google (user không có mật khẩu)
    if (!user.password) {
      return res.status(400).json({ message: "You cannot change the password for Google login accounts." });
    }

    // Kiểm tra mật khẩu cũ
    if (user.password !== oldPassword) {
      return res.status(400).json({ message: "Current password is incorrect" });
    }

    // Cập nhật mật khẩu mới
    user.password = newPassword;
    await user.save();

    // Gửi thông báo FCM
    if (user.fcmToken) {
      const title = "Đổi mật khẩu";
      const body = "Bạn đã đổi mật khẩu thành công vui lòng đăng nhập lại.";
      await sendNotification(user.fcmToken, title, body); // Gửi thông báo
    }

    res.status(200).json({
      message: "Password updated successfully",
      data: user,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while updating password", error: error.message });
  }
});



router.put("/upload_user_image/:id", upload.single("avatar"), async (req, res) => {
  try {
    const { id } = req.params;
    const { file } = req; // Multer lưu file tải lên ở đây

    if (!file) {
      return res.status(400).json({ message: "No image uploaded" });
    }

    // Lấy đường dẫn ảnh từ Cloudinary (URL)
    const imageUrl = file.path; // Đây là đường dẫn ảnh Cloudinary

    // Cập nhật avatar của người dùng trong cơ sở dữ liệu
    const updateUser = await User.findByIdAndUpdate(
      id,
      { avatar: imageUrl },
      { new: true }
    );

    if (updateUser) {
      res.status(200).json({
        status: 200,
        message: "Image uploaded successfully",
        data: updateUser,
      });
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while uploading image", error: error.message });
  }
});


//API delete user
router.delete("/delete_user/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const deleteUser = await User.findByIdAndDelete(id);

    if (deleteUser) {
      res.json({ message: "User deleted successfully" });
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while deleting user" });
  }
});

// CRUD Room
router.get('/rooms', async (req, res) => {
  try {
    // Lấy danh sách phòng và populate thông tin dịch vụ
    const rooms = await Room.find().populate('services');

    // Nếu thành công, trả về kết quả
    res.status(200).json({
      status: 200,
      message: 'Rooms fetched successfully',
      data: rooms, // Bao gồm cả thông tin dịch vụ
    });
  } catch (error) {
    console.error("Error fetching rooms:", error);
    res.status(500).json({
      message: 'An error occurred while fetching rooms',
    });
  }
});

// API thêm phòng mới
router.post('/add_room', upload.single('image'), async (req, res) => {
  try {
    const {
      name,
      price,
      rating,
      description,
      capacity,
      status,
      room_code,
      services,
    } = req.body;
    const { file } = req; // Multer lưu file tải lên ở đây

    if (!file) {
      return res.status(400).json({ message: "No image uploaded" });
    }

    // Lấy URL ảnh từ Cloudinary
    const imageUrl = file.path;  // Đường dẫn ảnh trên Cloudinary

    const servicesArray = services.split(',').map((id) => id.trim());

    // Tạo đối tượng mới cho phòng
    const newRoom = new Room({
      name,
      price,
      rating,
      description,
      capacity,
      status,
      room_code,
      image: imageUrl,  // Sử dụng URL ảnh từ Cloudinary
      services: servicesArray,
    });

    // Lưu thông tin phòng vào cơ sở dữ liệu
    const result = await newRoom.save();

    // Trả về kết quả thành công
    res.status(200).json({
      status: 200,
      message: "Thêm Room thành công",
      data: result,
    });
  } catch (error) {
    console.error("Error adding room:", error);
    res.status(500).json({
      status: 500,
      message: "Có lỗi xảy ra",
      error: error.message || error,
    });
  }
});
// API xem chi tiết phòng
router.get('/room/:id', async (req, res) => {
  try {
    const { id } = req.params; // Lấy id từ tham số URL

    // Tìm phòng theo ID và populate thông tin dịch vụ
    const room = await Room.findById(id)
      .populate('services'); // Populate thông tin dịch vụ nếu cần

    if (!room) {
      return res.status(404).json({
        status: 404,
        message: "Room not found",
      });
    }

    // Trả về thông tin phòng nếu tìm thấy
    return res.status(200).json({
      status: 200,
      message: "Room found",
      data: room,  // Trả về phòng thay vì data
    });
  } catch (error) {
    console.error("Error getting room:", error);
    return res.status(500).json({
      status: 500,
      message: "Có lỗi xảy ra khi lấy phòng",
      error: error.message || error,
    });
  }
});

// API cập nhật phòng
router.put('/update_room/:id', upload.single('image'), async (req, res) => {
  try {
    const { id } = req.params; // Lấy id từ tham số URL
    const {
      name,
      price,
      rating,
      description,
      capacity,
      status,
      room_code,
      services,
    } = req.body;
    const { file } = req; // Multer lưu file tải lên ở đây

    // Tìm phòng theo ID
    const room = await Room.findById(id);

    if (!room) {
      return res.status(404).json({ message: "Room not found" });
    }

    // Nếu có file ảnh mới, upload lên Cloudinary và lấy URL ảnh
    let imageUrl = room.image; // Giữ nguyên hình ảnh cũ nếu không có ảnh mới
    if (file) {
      imageUrl = file.path; // Lấy đường dẫn ảnh từ Cloudinary
    }

    // Chuyển đổi dữ liệu services từ chuỗi thành mảng
    const servicesArray = services ? services.split(',').map((id) => id.trim()) : room.services;

    // Cập nhật thông tin phòng
    room.name = name || room.name;
    room.price = price || room.price;
    room.rating = rating || room.rating;
    room.description = description || room.description;
    room.capacity = capacity || room.capacity;
    room.status = status || room.status;
    room.room_code = room_code || room.room_code;
    room.image = imageUrl;
    room.services = servicesArray;

    // Lưu thông tin phòng đã được cập nhật
    const updatedRoom = await room.save();

    // Trả về kết quả thành công
    res.status(200).json({
      status: 200,
      message: "Cập nhật Room thành công",
      data: updatedRoom,
    });
  } catch (error) {
    console.error("Error updating room:", error);
    res.status(500).json({
      status: 500,
      message: "Có lỗi xảy ra",
      error: error.message || error,
    });
  }
});

// API xóa phòng
router.delete('/delete_room/:id', async (req, res) => {
  try {
    const { id } = req.params; // Lấy id phòng từ tham số URL

    // Tìm và xóa phòng theo ID
    const result = await Room.findByIdAndDelete(id);

    if (!result) {
      return res.status(404).json({ message: "Room not found" });
    }

    // Trả về kết quả thành công
    res.status(200).json({
      status: 200,
      message: "Room deleted successfully",
      data: result,
    });
  } catch (error) {
    console.error("Error deleting room:", error);
    res.status(500).json({
      status: 500,
      message: "Có lỗi xảy ra",
      error: error.message || error,
    });
  }
});


// API lấy danh sách dịch vụ
router.get("/services", async (req, res) => {
  try {
    const services = await Service.find();
    res.status(200).json({
      status: 200,
      message: "Services retrieved successfully",
      data: services,
    });
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({ message: "An error occurred while retrieving services" });
  }
});

// add
router.post("/add-service", upload.single('image'), async (req, res) => {
  try {
    const { file } = req; // File upload lưu trên Cloudinary
    const data = req.body;

    if (!file) {
      return res.status(400).json({ message: "No image uploaded" });
    }

    // URL ảnh được lưu trên Cloudinary
    const imageUrl = file.path;

    // Tạo mới Service
    const newService = new Service({
      name: data.name,
      price: data.price,
      description: data.description,
      image: imageUrl, // Lưu URL ảnh trên Cloudinary
    });

    // Lưu vào cơ sở dữ liệu
    const savedService = await newService.save();

    res.status(200).json({
      status: 200,
      message: "Service added successfully",
      data: savedService,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({
      status: 500,
      message: "An error occurred while adding the service",
    });
  }
});

router.put("/update_service/:id", upload.single('image'), async (req, res) => {
  try {
    const serviceId = req.params.id;  // Lấy serviceId từ URL
    const { name, price, description } = req.body;  // Lấy các dữ liệu từ body

    // Tìm kiếm dịch vụ trong cơ sở dữ liệu
    const service = await Service.findById(serviceId);
    if (!service) {
      return res.status(404).json({ message: "Service not found" });
    }

    // Cập nhật các trường dữ liệu
    service.name = name || service.name;
    service.price = price || service.price;
    service.description = description || service.description;

    // Nếu có ảnh mới, upload lên Cloudinary và cập nhật đường dẫn ảnh
    if (req.file) {
      const imageUrl = req.file.path;  // Đường dẫn ảnh từ Cloudinary trả về
      service.image = imageUrl;
    }

    // Lưu lại dịch vụ đã cập nhật
    const updatedService = await service.save();

    res.status(200).json({
      status: 200,
      message: "Service updated successfully",
      data: updatedService,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({
      status: 500,
      message: "An error occurred while updating the service",
    });
  }
});

// Lấy service theo ID
router.get('/service/:id', async (req, res) => {
  try {
    const serviceId = req.params.id;

    // Tìm dịch vụ trong cơ sở dữ liệu theo ID
    const service = await Service.findById(serviceId);

    if (!service) {
      return res.status(404).json({
        status: 404,
        message: 'Service not found',
      });
    }

    // Trả về thông tin dịch vụ nếu tìm thấy
    return res.status(200).json({
      status: 200,
      service: service,
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({
      status: 500,
      message: 'An error occurred while fetching the service',
    });
  }
});

// Xóa service theo ID
router.delete('/delete-service/:id', async (req, res) => {
  try {
    const serviceId = req.params.id;

    // Tìm và xóa dịch vụ trong cơ sở dữ liệu theo ID
    const service = await Service.findByIdAndDelete(serviceId);

    if (!service) {
      return res.status(404).json({
        status: 404,
        message: 'Service not found',
      });
    }

    // Trả về phản hồi thành công khi dịch vụ được xóa
    return res.status(200).json({
      status: 200,
      message: 'Service deleted successfully',
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({
      status: 500,
      message: 'An error occurred while deleting the service',
    });
  }
});


//API Login with JsonWebToken
router.post("/login", async (req, res) => {
  try {
    const { username, password } = req.body;
    const user = await User.findOne({ username });

    if (!user) {
      return res.status(400).json({ message: "Tên đăng nhập không tồn tại" });
    }

    // So sánh mật khẩu mà không sử dụng bcrypt
    if (password !== user.password) {
      return res.status(400).json({ message: "Mật khẩu không chính xác" });
    }

    const token = jwt.sign({ id: user._id, role: user.role }, SECRET_KEY, {
      expiresIn: "1h",
    });
    const refreshToken = jwt.sign({ id: user._id }, SECRET_KEY, {
      expiresIn: "1d",
    });

    res.status(200).json({
      status: 200,
      message: "Login thành công",
      role: user.role,
      id: user._id,
      token: token,
      refreshToken: refreshToken,
    });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: "Lỗi máy chủ nội bộ" });
  }
});

router.post('/login/google', async (req, res) => {
  const { idToken } = req.body;


  try {
    // Xác thực token
    if (!idToken) {
      return res.status(400).json({ message: 'ID Token is required' });
    }
    const ticket = await client.verifyIdToken({
      idToken: idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });
    const payload = ticket.getPayload();
    const { sub, email, name, picture } = payload;
    console.log(sub);

    // Kiểm tra xem người dùng đã tồn tại trong MongoDB chưa
    let user = await User.findOne({ username: sub });
    if (!user) {
      // Nếu chưa tồn tại, tạo người dùng mới
      user = new User({
        username: sub,
        email,
        name,
        gender: "",
        address: "",
        birthday: "",
        avatar: picture,
        phonenumber: "",
        role: 1,
      });
      await user.save();
    }

    // Tạo token JWT cho phiên đăng nhập
    const accessToken = jwt.sign({ id: user._id, role: user.role }, SECRET_KEY, { expiresIn: '1h' });

    res.status(200).json({
      status: 200,
      message: 'Login successful',
      data: user,
      token: accessToken,
    });
  } catch (error) {
    console.error('Google login error:', error);
    res.status(500).json({ message: 'Login failed', error: error.message });
  }
});
router.post('/login/facebook', async (req, res) => {
  const { accessToken } = req.body;

  if (!accessToken) {
    return res.status(400).json({ message: 'Access Token is required' });
  }

  try {
    const response = await fetch(`https://graph.facebook.com/me?access_token=${accessToken}&fields=id,name,email,picture`);
    const data = await response.json();

    if (response.status !== 200) {
      return res.status(400).json({ message: 'Facebook login failed', data });
    }

    const { id, name, email, picture } = data;
    console.log(id);

    // Kiểm tra email, nếu không có thì tạo giá trị duy nhất
    const userEmail = email || `unknown_${id}@facebook.com`;

    const user = await User.findOne({ username: id });
    if (!user) {
      const newUser = new User({
        username: id,
        email: userEmail,
        name: name,
        gender: "",
        address: "",
        birthday: "",
        phonenumber: "",
        avatar: picture?.data?.url || "",
        role: 1,
      });

      await newUser.save();
      res.status(200).json({ message: 'Login successful', data: newUser });
    } else {
      res.status(200).json({ message: 'Login successful', data: user });
    }
  } catch (error) {
    console.error('Facebook login error:', error);
    res.status(500).json({ message: 'Server error', data: error.message });
  }
});


//API Register and email
// const Transporter = require('../config/common/mail')
router.post("/register", async (req, res) => {
  try {
    const data = req.body;

    const newUser = new User({
      username: data.username,
      password: data.password, // Lưu mật khẩu chưa mã hóa
      email: data.email,
      phonenumber: data.phonenumber,
      name: data.name,
      gender: "",
      address: "",
      birthday: "",
      role: data.role,
      avatar: "",
    });

    const result = await newUser.save();

    if (result) {
      res.json({
        status: 200,
        message: "Đăng ký thành công",
        data: result,
      });
    } else {
      res.status(400).json({
        status: 400,
        message: "Đăng ký không thành công",
        data: [],
      });
    }
  } catch (error) {
    console.log(error);
    res.status(500).json({
      status: 500,
      message: "Đã xảy ra lỗi trong quá trình đăng ký",
      error: error.message,
    });
  }
});


//API voucher
router.post('/add_voucher', async (req, res) => {
  try {
    const voucher = new Voucher(req.body);
    await voucher.save();
    res.status(201).json(voucher);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

// Lấy danh sách voucher
router.get('/voucher', async (req, res) => {
  try {
    const vouchers = await Voucher.find();
    res.status(200).json(vouchers);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Lấy chi tiết voucher theo ID
router.get("/detail_voucher/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const voucher = await Voucher.findById(id);
    if (voucher) {
      res.status(200).json({
        status: 200,
        message: "voucher retrieved successfully",
        data: voucher,
      });
    } else {
      res.status(404).json({ message: "voucher not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({ message: "An error occurred while retrieving voucher" });
  }
});

// Cập nhật voucher
router.put("/update_voucher/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const {
      code, discount, expirationDate, isActive
    } = req.body;

    const update_voucher = await Voucher.findByIdAndUpdate(
      id,
      {
        code, discount, expirationDate, isActive
      },
      { new: true }
    );

    if (update_voucher) {
      res.status(200).json({
        status: 200,
        message: "voucher updated successfully",
        data: update_voucher,
      });
    } else {
      res.status(404).json({ message: "voucher not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while updating voucher" });
  }
});

// Xóa voucher
router.delete("/delete_voucher/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const deleteVoucher = await Voucher.findByIdAndDelete(id);

    if (deleteVoucher) {
      res.status(200).json({ message: "voucher deleted successfully" });
    } else {
      res.status(404).json({ message: "voucher not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while deleting voucher" });
  }
});
//API them voucher cua 1 thg user cu the
router.post("/user/:userId/add_voucher", async (req, res) => {
  try {
    const { userId } = req.params;
    const { voucherId } = req.body;

    const newUserVoucher = new UserVoucher({
      userId,
      voucherId,
    });

    const result = await newUserVoucher.save();
    res.status(201).json({
      status: 201,
      message: "Voucher added to user successfully",
      data: result,
    });
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({
        message: "An error occurred while adding Voucher to User",
        error: error.message,
      });
  }
});
//API hien thi list voucher cua 1 thg user cu the
router.get("/user/:userId/vouchers", async (req, res) => {
  try {
    const { userId } = req.params;

    const uservouchers = await UserVoucher.find({ userId });

    if (!uservouchers || uservouchers.length === 0) {
      return res.status(200).json({ message: "No uservouchers found for this user", data: [] });
    }

    const voucherIds = uservouchers.map(uservoucher => uservoucher.voucherId);

    const vouchers = await Voucher.find({ '_id': { $in: voucherIds } });

    if (!vouchers || vouchers.length === 0) {
      return res.status(200).json({ message: "No vouchers found for this user", data: [] });
    }
    const vouchersWithUsers = uservouchers.map(uservoucher => {
      const voucher = vouchers.find(voucher => voucher._id.toString() === uservoucher.voucherId.toString());
      return { ...uservoucher.toObject(), voucher };
    });

    res.status(200).json({
      message: "Vouchers retrieved successfully",
      data: vouchersWithUsers,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while retrieving Vouchers" });
  }
});
//API xoa voucher cua 1 thg user cu the
router.delete("/delete_uservoucher/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const deleteVoucher = await UserVoucher.findByIdAndDelete(id);

    if (deleteVoucher) {
      res.status(200).json({ message: "UserVoucher deleted successfully" });
    } else {
      res.status(404).json({ message: "UserVoucher not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while deleting voucher" });
  }
});

//booking

router.post("/book_room", async (req, res) => {
  try {
    const { userId, roomId, startDate, endDate, totalPrice, paymentStatus } = req.body;

    const user = await User.findById(userId);
    const room = await Room.findById(roomId);

    if (!user || !room) {
      return res.status(404).json({ message: "User or Room not found" });
    }

    const overlappingBooking = await Booking.findOne({
      roomId,
      status: { $ne: "cancelled" },
      $or: [
        { startDate: { $lte: endDate }, endDate: { $gte: startDate } },
      ],
    });

    if (overlappingBooking) {
      return res.status(400).json({ message: "Room is already booked during the selected dates" });
    }

    const newBooking = new Booking({
      userId,
      roomId,
      startDate,
      endDate,
      totalPrice,
    });

    const savedBooking = await newBooking.save();

    // Gửi thông báo cho người dùng
    const adminTokens = await AdminFCM.find().select("token -_id");

    // Gửi thông báo
    // const message = {
    //   notification: {
    //     title: "Booking mới!",
    //     body: "Một phòng mới đã được đặt thành công.",
    //   },
    //   tokens: adminTokens.map((admin) => admin.token), // Danh sách token FCM
    // };

    // const response = await admin.messaging().sendEachForMulticast(message);
    // console.log("Notification sent:", response);

    // Gửi thông báo cho người dùng
    if (user.fcmToken) {
      sendNotification(
        user.fcmToken,
        "Đặt phòng thành công",
        `Phòng ${room.name} đã được đặt. Vui lòng chờ xác nhận.`
      );
    }
    const bookingWithRoom = {
      ...savedBooking.toObject(),  // Thông tin booking
      room: {                 // Thông tin phòng
        _id: room._id,
        name: room.name,
        price: room.price,
        image: room.image,
        description: room.description,
        status: room.status,
      },
    };
    res.status(201).json({ message: "Room booked successfully", data: bookingWithRoom });
  } catch (error) {
    res.status(500).json({ message: "An error occurred while booking room", error: error.message });
  }
});

// Cập nhật trạng thái booking
router.put("/update-status-booking/:id", async (req, res) => {
  const { id } = req.params;
  const { status } = req.body;
  try {
    const booking = await Booking.findById(id);
    if (!booking) {
      return res.status(404).json({ message: "Không tìm thấy booking" });
    }

    // Cập nhật trạng thái booking
    booking.status = status;
    const updatedBooking = await booking.save();

    // Tìm người dùng và phòng liên quan đến booking
    const user = await User.findById(booking.userId);
    const room = await Room.findById(booking.roomId);

    if (!user || !room) {
      return res.status(404).json({ message: "User or Room not found" });
    }

    // Gửi thông báo cho người dùng khi admin xác nhận
    if (user.fcmToken && booking.status === "confirmed") {
      sendNotification(
        user.fcmToken,
        "Xác nhận đặt phòng",
        `Đặt phòng của bạn từ ${booking.startDate} đến ${booking.endDate} đã được xác nhận.`
      );
      
      // Tạo hóa đơn nếu trạng thái booking là "confirmed"
      if (status === "confirmed") {
        await createBill(booking);
      }
    } else if (user.fcmToken && booking.status === "cancelled") {
      sendNotification(
        user.fcmToken,
        "Hủy đặt phòng",
        `Đặt phòng của bạn từ ${booking.startDate} đến ${booking.endDate} đã bị hủy.`
      );
    }

    res.status(200).json({ message: "Booking updated successfully", data: updatedBooking });
  } catch (error) {
    console.error("Error updating booking:", error);
    res.status(500).json({ message: "Error updating booking", error: error.message });
  }
});

// API tạo hóa đơn
async function createBill(booking) {
  try {
    const { userId, roomId, startDate, endDate, totalPrice } = booking;

    // Tạo hóa đơn với trạng thái thanh toán là "paid"
    const newBill = new Bill({
      userId,
      roomId,
      startDate,
      endDate,
      totalPrice,
      status: "confirmed",  // Trạng thái hóa đơn là "confirmed"
      paymentStatus: "paid",  // Thanh toán đã hoàn tất
    });

    const savedBill = await newBill.save();
    console.log("Tạo hóa đơn thành công!", savedBill);
    return savedBill;
  } catch (error) {
    console.error("Error creating bill:", error);
    throw new Error("Không thể tạo hóa đơn.");
  }
}
router.post("/save-fcm-token", async (req, res) => {
  const { token } = req.body;

  try {
    if (!token) return res.status(400).json({ message: "Token is required" });

    const existingToken = await AdminFCM.findOne({ token });
    if (!existingToken) {
      await AdminFCM.create({ token });
    }

    res.status(200).json({ message: "FCM Token saved successfully" });
  } catch (error) {
    console.error("Error saving token:", error);
    res.status(500).json({ message: "Failed to save token" });
  }
});


router.post("/update-fcm-token", async (req, res) => {
  const { userId, fcmToken } = req.body;

  try {
    const user = await User.findById(userId);
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    // Cập nhật FCM Token
    user.fcmToken = fcmToken;
    await user.save();

    res.status(200).json({ message: "FCM Token updated successfully" });
  } catch (error) {
    res.status(500).json({ message: "Error updating FCM Token", error: error.message });
  }
});

router.get("/check-fcm-token/:userId", async (req, res) => {
  const { userId } = req.params;

  try {
    const user = await User.findById(userId);
    if (!user || !user.fcmToken) {
      return res.status(404).json({ message: "FCM Token not found" });
    }

    res.status(200).json({ message: "FCM Token ", fcmToken: user.fcmToken });
  } catch (error) {
    res.status(500).json({ message: "Error checking FCM Token", error: error.message });
  }
});


// Lấy tất cả bookings của user
router.get("/user/:userId/bookings", async (req, res) => {
  try {
    const { userId } = req.params;

    // Truy vấn tất cả các booking của user
    const bookings = await Booking.find({ userId }).sort({ createdAt: -1 });

    if (!bookings || bookings.length === 0) {
      return res.status(200).json({ message: "No bookings found for this user", data: [] });
    }

    // Lấy tất cả roomId của các booking
    const roomIds = bookings.map(booking => booking.roomId);

    // Truy vấn thông tin các phòng dựa trên roomIds
    const rooms = await Room.find({ '_id': { $in: roomIds } });

    if (!rooms || rooms.length === 0) {
      return res.status(200).json({ message: "No rooms found for the bookings", data: [] });
    }

    // Kết hợp thông tin phòng vào mỗi booking, loại bỏ trường 'services'
    const bookingsWithRooms = bookings.map(booking => {
      const room = rooms.find(room => room._id.toString() === booking.roomId.toString());

      // Loại bỏ trường 'services' khỏi phòng
      const roomWithoutServices = { ...room.toObject() };
      delete roomWithoutServices.services;

      return { ...booking.toObject(), room: roomWithoutServices }; // Thêm thông tin phòng vào mỗi booking
    });

    res.status(200).json({
      message: "Bookings retrieved successfully",
      data: bookingsWithRooms,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while retrieving bookings" });
  }
});


router.put("/update-status-room/:id", async (req, res) => {
  const { id } = req.params;
  const { status } = req.body;

  try {
    const room = await Room.findByIdAndUpdate(
      id,
      { status },
      { new: true }
    );

    if (!room) {
      return res.status(404).json({ message: "Không tìm thấy room" });
    }

    res.status(200).json({
      message: "room update successfully",
      data: room,
    });
  } catch (error) {
    res.status(500).json({ message: "Lỗi server", error });
  }
});

// Hủy booking
router.delete("/cancel_booking/:id", async (req, res) => {
  try {
    const { id } = req.params;

    const deletedBooking = await Booking.findByIdAndDelete(id);

    if (!deletedBooking) {
      return res.status(404).json({ message: "Booking not found" });
    }

    res.status(200).json({
      message: "Booking cancelled successfully",
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while cancelling booking" });
  }
});

// API lấy tất cả các booking
router.get('/bookings', async (req, res) => {
  try {
    const bookings = await Booking.find()
      .populate("userId", "name email phonenumber avatar") // Lấy tên và email của người dùng từ User
      .populate("roomId", "name price") // Lấy tên và giá của phòng từ Room
      .exec();
    res.status(200).json(bookings); // Trả về danh sách các booking dưới dạng JSON
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' }); // Nếu có lỗi server
  }
});
// API lấy thong tin 1 booking
router.get("/bookings/:id", async (req, res) => {
  try {
    const { id } = req.params;

    // Tìm Booking bằng ID
    const booking = await Booking.findById(id);

    if (!booking) {
      return res.status(404).json({ message: "Booking not found" });
    }

    // Tìm thông tin Room dựa trên roomId từ Booking
    const room = await Room.findById(booking.roomId);

    if (!room) {
      return res.status(404).json({ message: "Room not found" });
    }

    // Kết hợp thông tin Booking và Room vào một đối tượng
    const bookingWithRoom = {
      ...booking.toObject(),  // Thông tin booking
      room: {                 // Thông tin phòng
        _id: room._id,
        name: room.name,
        price: room.price,
        image: room.image,
        description: room.description,
        status: room.status,
      },
    };

    // Trả về thông tin Booking và Room
    res.status(200).json({
      message: "Booking details with room fetched successfully",
      data: bookingWithRoom,
    });
  } catch (error) {
    console.error("Error fetching booking details:", error);
    res.status(500).json({
      message: "An error occurred while fetching booking details",
      error: error.message,
    });
  }
});

// API lấy tất cả các yêu thích
router.get("/user/:userId/favourites", async (req, res) => {
  try {
    const { userId } = req.params;

    // Truy vấn tất cả các favourite của user
    const favourites = await Favourite.find({ userId }).sort({ createdAt: -1 });

    if (!favourites || favourites.length === 0) {
      // Trả về danh sách rỗng thay vì lỗi 404
      return res.status(200).json({ message: "No favourites found for this user", data: [] });
    }

    // Lấy tất cả roomId của các favourite
    const roomIds = favourites.map(favourite => favourite.roomId);

    // Truy vấn thông tin các phòng dựa trên roomIds
    const rooms = await Room.find({ '_id': { $in: roomIds } });

    if (!rooms || rooms.length === 0) {
      return res.status(200).json({ message: "No rooms found for the favourites", data: [] });
    }

    // Kết hợp thông tin phòng vào mỗi favourite, loại bỏ trường 'services' trong phòng
    const favouritesWithRooms = favourites.map(favourite => {
      const room = rooms.find(room => room._id.toString() === favourite.roomId.toString());

      // Loại bỏ trường 'services' khỏi phòng
      const roomWithoutServices = { ...room.toObject() };
      delete roomWithoutServices.services;

      const favouriteObject = favourite.toObject();  // Chuyển thành object thông thường

      return { ...favouriteObject, room: roomWithoutServices }; // Thêm thông tin phòng vào mỗi favourite
    });

    res.status(200).json({
      message: "Favourites retrieved successfully",
      data: favouritesWithRooms,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while retrieving Favourites" });
  }
});

//// add yêu thích {date test: userid = 6746aea316aac83085b8afef romid =6724d8252c88291f6771f7f2
router.post("/addFavourite", async (req, res) => {
  try {
    const { userId, roomId } = req.body;

    const user = await User.findById(userId);
    const room = await Room.findById(roomId);
    if (!user || !room) {
      return res.status(404).json({ message: "User or Room not found" });
    }

    const newFavourite = new Favourite({ userId, roomId, status: 1 });
    const savedFavourite = await newFavourite.save();

    res.status(201).json({ message: "Added to favourites", data: savedFavourite });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});


router.get('/favourites/check', async (req, res) => {
  const { userId, roomId } = req.query;
  try {
    const favourite = await Favourite.findOne({ userId, roomId });
    if (favourite) {
      return res.status(200).json({ status: 200, data: favourite });
    } else {
      return res.status(404).json({ status: 404, message: "Favourite not found" });
    }
  } catch (err) {
    res.status(500).json({ status: 500, message: err.message });
  }
});
//Type Room
router.post('/add_typeroom', async (req, res) => {
  const { name, imageRoom } = req.body;
  const newTypeRoom = new TypeRooms({ name, imageRoom });

  try {
    const savedTypeRoom = await newTypeRoom.save();
    res.status(201).json(savedTypeRoom);
  } catch (error) {
    res.status(400).json({ message: 'Error creating typeRoom', error });
  }
});

/* 2. Read all (GET) */
router.get('/typerooms', async (req, res) => {
  try {
    const typeRooms = await TypeRooms.find();
    res.status(200).json(typeRooms);
  } catch (error) {
    res.status(400).json({ message: 'Error fetching typeRooms', error });
  }
});

/* 3. Read single (GET by ID) */
router.get('/typeroom/:id', async (req, res) => {
  try {
    const typeRoom = await TypeRooms.findById(req.params.id);
    if (!typeRoom) {
      return res.status(404).json({ message: 'TypeRoom not found' });
    }
    res.status(200).json(typeRoom);
  } catch (error) {
    res.status(400).json({ message: 'Error fetching typeRoom', error });
  }
});

/* 4. Update (PUT) */
router.put('/update_typeroom/:id', async (req, res) => {
  try {
    const updatedTypeRoom = await TypeRooms.findByIdAndUpdate(
      req.params.id,
      { name: req.body.name, imageRoom: req.body.imageRoom },
      { new: true }  // Return the updated document
    );
    if (!updatedTypeRoom) {
      return res.status(404).json({ message: 'TypeRoom not found' });
    }
    res.status(200).json(updatedTypeRoom);
  } catch (error) {
    res.status(400).json({ message: 'Error updating typeRoom', error });
  }
});

/* 5. Delete (DELETE) */
router.delete('/delete_typeroom/:id', async (req, res) => {
  try {
    const deletedTypeRoom = await TypeRooms.findByIdAndDelete(req.params.id);
    if (!deletedTypeRoom) {
      return res.status(404).json({ message: 'TypeRoom not found' });
    }
    res.status(200).json({ message: 'TypeRoom deleted' });
  } catch (error) {
    res.status(400).json({ message: 'Error deleting typeRoom', error });
  }
});


//API delete yeu thich
router.delete("/delete_Favourite/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const deletedFavourite = await Favourite.findByIdAndDelete(id);

    if (!deletedFavourite) {
      return res.status(404).json({ message: "Favourite not found" });
    }

    res.json({ message: "Favourite removed successfully" });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});


// Lấy danh sách thông báo của một user
router.get("/get-notification/:id", async (req, res) => {
  const { userId } = req.query;
  console.log(userId);

  try {
    const notifications = await Notification.find({ userId: userId }).sort({ createdAt: -1 });
    if (!notifications || notifications.length === 0) {
      return res.status(200).json({ message: "No notifications found for this user", data: [] });
    }
    res.status(200).json({
      message: "Notifications retrieved successfully",
      data: notifications,
    });
  } catch (error) {
    res.status(500).json({ error: "Internal Server Error" });
  }
});

// Lấy tất cả thông báo
router.get("/notifications", async (req, res) => {
  try {
    const notifications = await Notification.find();
    res.status(200).json({
      status: 200,
      message: "Notification retrieved successfully",
      data: notifications,
    });
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({ message: "An error occurred while retrieving notifications" });
  }
});
//API add notification
router.post("/add_notification", async (req, res) => {
  try {
    const { userId, message, type } = req.body;
    const user = await User.findById(userId);
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }
    // Tạo yeu thich mới
    const newNotification = new Notification({
      userId,
      message,
      type
    });

    const savedNotification = await newNotification.save();
    res.status(201).json({
      message: "Notification booked successfully",
      data: savedNotification,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while booking room", error: error.message });
  }
});


router.get("/top-rooms", async (req, res) => {
  try {
    const rooms = await Booking.aggregate([
      { $group: { _id: "$roomId", bookings: { $sum: 1 } } }, // Đếm số lần đặt phòng của từng phòng
      { $sort: { bookings: -1 } }, // Sắp xếp theo số lần đặt giảm dần
      { $limit: 5 }, // Lấy top 5 phòng phổ biến nhất
      {
        $lookup: {
          from: "rooms",
          localField: "_id",
          foreignField: "_id",
          as: "roomInfo"
        }
      }, // Kết nối với bảng (collection) rooms để lấy thông tin chi tiết
      { $unwind: "$roomInfo" }, // Mở rộng roomInfo thành object thay vì array
      {
        $project: {
          name: "$roomInfo.name", // Chỉ lấy trường name từ roomInfo
          image: "$roomInfo.image", // Thêm trường ảnh của phòng
          room_code: "$roomInfo.room_code", // Thêm trường ảnh của phòng
          price: "$roomInfo.price", // Thêm trường ảnh của phòng
          rating: "$roomInfo.rating", // Thêm trường ảnh của phòng
          bookings: 1 // Giữ lại trường bookings
        }
      }
    ]);

    res.status(200).json({
      status: 200,
      message: 'Rooms fetched successfully',
      data: rooms, // Trả về danh sách phòng phổ biến cùng với dịch vụ
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

router.get('/search-rooms', async (req, res) => {
  try {
    const { query } = req.query;
    const rooms = await Room.find({
      name: { $regex: query, $options: "i" }
    }).select("name image");

    res.status(200).json({
      status: 200,
      message: "Rooms fetched successfully",
      data: rooms,
    });
  } catch (error) {
    res.status(500).json({
      message: "An error occurred while searching for rooms",
    });
  }
});

//bill
router.post("/create_bill", async (req, res) => {
  try {
    const { userId, roomId, startDate, endDate, totalPrice, paymentStatus } = req.body;

    const user = await User.findById(userId);
    const room = await Room.findById(roomId);

    if (!user || !room) {
      return res.status(404).json({ message: "User or Room not found" });
    }


    const newBill = new Bill({
      userId,
      roomId,
      startDate,
      endDate,
      totalPrice,
      status: "confirmed",
      paymentStatus: "paid"
    });

    const savedBill = await newBill.save();

    res.status(201).json({ message: "Room booked successfully", data: savedBill });
  } catch (error) {
    res.status(500).json({ message: "An error occurred while booking room", error: error.message });
  }
});
router.get("/user/:userId/bills", async (req, res) => {
  try {
    const { userId } = req.params;

    // Truy vấn tất cả các booking của user
    const bills = await Bill.find({ userId }).sort({ createdAt: -1 });

    if (!bills || bills.length === 0) {
      return res.status(200).json({ message: "No bills found for this user", data: [] });
    }

    // Lấy tất cả roomId của các booking
    const roomIds = bills.map(bill => bill.roomId);
    const userIds = bills.map(bill => bill.userId);

    // Truy vấn thông tin các phòng dựa trên roomIds
    const rooms = await Room.find({ '_id': { $in: roomIds } });
    const users = await User.find({ '_id': { $in: userIds } });

    if (!rooms || rooms.length === 0) {
      return res.status(200).json({ message: "No rooms found for the bookings", data: [] });
    }

    
    const billsWithRooms = bills.map(bill => {
      const room = rooms.find(room => room._id.toString() === bill.roomId.toString());
      const user = users.find(user => user._id.toString() === bill.userId.toString());
      const roomWithoutServices = { ...room.toObject() };
      delete roomWithoutServices.services;

      return { ...bill.toObject(), room: roomWithoutServices, user: user };
    });

    res.status(200).json({
      message: "Bookings retrieved successfully",
      data: billsWithRooms,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while retrieving bookings" });
  }
});
//get all bill
router.get('/bills', async (req, res) => {
  try {
    // Lấy danh sách phòng và populate thông tin dịch vụ
    const bills = await Bill.find()
      .populate("userId", "name") 
      .populate("roomId", "name")
      .exec();
    // Nếu thành công, trả về kết quả
    res.status(200).json({
      status: 200,
      message: 'bills fetched successfully',
      data: bills, // Bao gồm cả thông tin dịch vụ
    });
  } catch (error) {
    console.error("Error fetching bills:", error);
    res.status(500).json({
      message: 'An error occurred while fetching bills',
    });
  }
});

router.get("/check_room_availability", async (req, res) => {
  const { roomId, startDate, endDate } = req.query;
  console.log(roomId);

  try {
    const room = await Room.findById(roomId);

    if (!room) {
      return res.status(404).json({ message: "Room not found" });
    }
    const overlappingBooking = await Booking.findOne({
      roomId,
      status: { $ne: "cancelled" },
      $or: [
        { startDate: { $lte: endDate }, endDate: { $gte: startDate } },
      ],
    });

    if (overlappingBooking) {
      return res.status(400).json({ message: "An error occurred while retrieving bookings" });
    }

    return res.status(200).json({ data: true });
  } catch (error) {
    res.status(500).json({ message: "An error occurred", error: error.message });
  }
});


router.get("/searchUsersByName", async (req, res) => {
  try {
    const { name } = req.query; // Lấy tên từ query parameter

    // Kiểm tra nếu tham số name không được truyền vào
    if (!name) {
      return res.status(400).json({ message: "Tên không được để trống" });
    }

    // Tìm kiếm không phân biệt chữ hoa chữ thường
    const users = await User.find({
      name: { $regex: name, $options: "i" },
    });

    // Trả về kết quả
    res.status(200).json(users);
  } catch (error) {
    console.error("Lỗi khi tìm kiếm người dùng:", error);
    res.status(500).json({ message: "Lỗi khi tìm kiếm người dùng" });
  }
})
//thongke
router.get("/bookings-by-date", async (req, res) => {
  try {
    const bookingsByDate = await Booking.aggregate([
      {
        $group: {
          _id: { $dateToString: { format: "%Y-%m-%d", date: "$createdAt" } },
          count: { $sum: 1 }
        }
      },
      { $sort: { _id: 1 } }
    ]);
    res.json(bookingsByDate);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});


// API handler to filter rooms by price and rating
router.get('/searchRooms', (req, res) => {
  const { minPrice, maxPrice } = req.query;

  let filter = {};

  if (minPrice) filter.price = { $gte: minPrice }; // Giá lớn hơn hoặc bằng minPrice
  if (maxPrice) filter.price = { ...filter.price, $lte: maxPrice }; // Giá nhỏ hơn hoặc bằng maxPrice

  Room.find(filter) // Tìm các phòng với điều kiện giá
    .then(rooms => {
      res.json({ status: 200, data: rooms });
    })
    .catch(error => {
      res.json({ status: 500, message: error.message });
    });
});


router.get('/searchbookings', async (req, res) => {
  try {
    const { startDate, endDate } = req.query;

    if (!startDate || !endDate) {
      return res.status(400).json({ message: "Vui lòng cung cấp cả ngày bắt đầu và ngày kết thúc." });
    }

    console.log("Received Start Date:", startDate);
    console.log("Received End Date:", endDate);

    const filter = {
      startDate: { $gte: new Date(startDate) },
      endDate: { $lte: new Date(endDate) },
    };

    const bookings = await Booking.find(filter)
      .populate("userId", "name email phonenumber avatar")
      .populate("roomId", "name price")
      .exec();

    console.log("Filtered Bookings:", bookings);
    res.status(200).json(bookings);
  } catch (err) {
    console.error("Lỗi khi tìm kiếm booking theo ngày:", err);
    res.status(500).json({ message: 'Server error' });
  }
});



router.get("/revenue-by-room", async (req, res) => {
  try {
    const revenueByRoom = await Booking.aggregate([
      { $match: { status: "confirmed" } },
      { $group: { _id: "$roomId", totalRevenue: { $sum: "$totalPrice" } } },
      { $lookup: { from: "rooms", localField: "_id", foreignField: "_id", as: "roomInfo" } },
      { $unwind: "$roomInfo" },
      { $project: { "roomInfo.name": 1, "roomInfo.image": 1, totalRevenue: 1 } },
      { $sort: { totalRevenue: -1 } }
    ]);
    res.json(revenueByRoom);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

router.get("/total-revenue", async (req, res) => {
  try {
    const revenue = await Booking.aggregate([
      { $match: { status: "confirmed" } },
      { $group: { _id: null, totalRevenue: { $sum: "$totalPrice" } } }
    ]);
    res.json(revenue.length > 0 ? revenue[0] : { totalRevenue: 0 });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});
router.get("/booking-stats", async (req, res) => {
  try {
    const stats = await Booking.aggregate([
      { $group: { _id: "$status", count: { $sum: 1 } } }
    ]);
    res.json(stats);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});
router.get("/popular-rooms", async (req, res) => {
  try {
    const popularRooms = await Booking.aggregate([
      { $group: { _id: "$roomId", bookings: { $sum: 1 } } },
      { $lookup: { from: "rooms", localField: "_id", foreignField: "_id", as: "roomInfo" } },
      { $unwind: "$roomInfo" },
      { $project: { "roomInfo.name": 1, "roomInfo.image": 1, bookings: 1 } },
      { $sort: { bookings: -1 } },
      { $limit: 5 }
    ]);
    res.json(popularRooms);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});
router.get('/revenue', async (req, res) => {
  const { startDate, endDate } = req.query;

  try {
    const bills = await Bill.aggregate([
      {
        $match: {
          createdAt: {
            $gte: new Date(startDate),  // Ngày bắt đầu
            $lte: new Date(endDate),    // Ngày kết thúc
          },
        },
      },
      {
        $group: {
          _id: { $dateToString: { format: "%Y-%m-%d", date: "$createdAt" } }, // Nhóm theo ngày
          totalRevenue: { $sum: '$totalPrice' }, // Tổng doanh thu theo ngày
        },
      },
      { $sort: { _id: 1 } },  // Sắp xếp theo ngày từ trước đến sau
    ]);

    res.json({ revenueStats: bills });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Server Error' });
  }
});
module.exports = router;