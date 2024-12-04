var express = require("express");
var router = express.Router();

const upload = require("../config/common/upload");
const multer = require("multer");
const path = require("path");


const User = require("../models/users");
const Room = require("../models/room");
const RoomService = require("../models/roomservice");
const Service = require("../models/service");
const Voucher = require("../models/vouchers");
const Booking = require("../models/booking");
const TypeRooms = require("../models/typeRooms");
const Favourite = require("../models/favourite")

const bcrypt = require('bcrypt');
const saltRounds = 10;
const jwt = require('jsonwebtoken');
const SECRET_KEY = process.env.SECRET_KEY;


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

    // Kiểm tra mật khẩu cũ
    if (user.password !== oldPassword) {
      return res.status(400).json({ message: "Current password is incorrect" });
    }

    // Cập nhật mật khẩu mới
    user.password = newPassword;
    await user.save();

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
    let imagePath = req.file ? req.file.path : null; // Kiểm tra xem tệp có được gửi không

    if (imagePath) {
      // Sửa dấu gạch chéo ngược thành gạch chéo xuôi
      imagePath = imagePath.replace(/\\/g, "/"); // Chuyển đổi tất cả gạch chéo ngược thành gạch chéo xuôi
      imagePath = imagePath.replace('public/', '');  // Xóa phần 'public/' nếu có

      // Cập nhật đường dẫn ảnh vào cơ sở dữ liệu
      const updateUser = await User.findByIdAndUpdate(
        id,
        { avatar: imagePath },
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
    } else {
      res.status(400).json({ message: "No image uploaded" });
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
    // Lấy đường dẫn ảnh từ file
    const urlsImage = `uploads/${file.filename}`;
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
      image: urlsImage,
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

    // Nếu có file ảnh mới, lấy đường dẫn ảnh từ file
    let imageUrl = room.image; // Giữ nguyên hình ảnh cũ nếu không có ảnh mới
    if (file) {
      imageUrl = `uploads/${file.filename}`;
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

////// API cập nhật trangj thai yeu thich
router.put("/update_room_favouritestatus/:id", async (req, res) => {
  const { id } = req.params;
  const { favouritestatus } = req.body;

  try {

    const room = await Room.findByIdAndUpdate(
      id,
      { favouritestatus },
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
    const data = req.body;
    const { file } = req; // Multer lưu file tải lên ở đây

    if (!file) {
      return res.status(400).json({ message: "No image uploaded" });
    }

    // Lấy đường dẫn ảnh từ file
    const imageUrl = `uploads/${file.filename}`;

    // Tạo mới Service
    const newService = new Service({
      name: data.name,
      price: data.price,
      description: data.description,
      image: imageUrl, // Lưu đường dẫn ảnh dưới dạng mảng
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

    // Nếu có ảnh mới, xử lý ảnh và cập nhật đường dẫn
    if (req.file) {
      const imageUrl = `uploads/${req.file.filename}`;  // Lưu đường dẫn ảnh
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

    const isPasswordValid = await bcrypt.compare(password, user.password);

    if (!isPasswordValid) {
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
//API Register and email
// const Transporter = require('../config/common/mail')
router.post("/register", async (req, res) => {
  try {
    const data = req.body;

    // Mã hóa mật khẩu
    const hashedPassword = await bcrypt.hash(data.password, saltRounds);

    const newUser = new User({
      username: data.username,
      password: hashedPassword, // Lưu mật khẩu đã mã hóa
      email: data.email,
      phonenumber: data.phonenumber,
      name: "",
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
      code,discount,expirationDate,isActive
    } = req.body;

    const update_voucher = await Voucher.findByIdAndUpdate(
      id,
      {
        code,discount,expirationDate,isActive
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


//booking

router.post("/book_room", async (req, res) => {
  try {
    const { userId, roomId, startDate, endDate, totalPrice } = req.body;

    // Kiểm tra user và phòng có tồn tại không
    const user = await User.findById(userId);
    const room = await Room.findById(roomId);
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }
    if (!room) {
      return res.status(404).json({ message: "Room not found" });
    }

    // Kiểm tra phòng có bị đặt trùng thời gian không (ngoại trừ booking bị "cancelled")
    const overlappingBooking = await Booking.findOne({
      roomId,
      status: { $ne: "cancelled" }, // Bỏ qua booking có trạng thái "cancelled"
      $or: [
        { startDate: { $lte: endDate }, endDate: { $gte: startDate } },
      ],
    });

    if (overlappingBooking) {
      return res.status(400).json({ message: "Room is already booked during the selected dates" });
    }

    // Tạo booking mới
    const newBooking = new Booking({
      userId,
      roomId,
      startDate,
      endDate,
      totalPrice,
      status: "pending", // Trạng thái mặc định ban đầu
    });

    const savedBooking = await newBooking.save();
    res.status(201).json({
      message: "Room booked successfully",
      data: savedBooking,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while booking room", error: error.message });
  }
});

// Lấy tất cả bookings của user
router.get("/user/:userId/bookings", async (req, res) => {
  try {
    const { userId } = req.params;

    // Truy vấn tất cả các booking của user
    const bookings = await Booking.find({ userId });

    if (!bookings || bookings.length === 0) {
      return res.status(200).json({ message: "No bookings found for this user", data: [] });
    }

    // Lấy tất cả roomId của các booking
    const roomIds = bookings.map(booking => booking.roomId);

    // Truy vấn thông tin các phòng dựa trên roomIds
    const rooms = await Room.find({ '_id': { $in: roomIds } });

    if (!rooms || rooms.length === 0) {
      return res.status(200).json({ message: "No bookings found for this user", data: [] });
    }

    // Kết hợp thông tin phòng vào mỗi booking
    const bookingsWithRooms = bookings.map(booking => {
      const room = rooms.find(room => room._id.toString() === booking.roomId.toString());
      return { ...booking.toObject(), room }; // Thêm thông tin phòng vào mỗi booking
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


// Cập nhật trạng thái booking
router.put("/update-status-booking/:id", async (req, res) => {
  const { id } = req.params;
  const { status } = req.body;

  try {
    
    if (!["pending", "confirmed", "cancelled"].includes(status)) {
      return res.status(400).json({ message: "Trạng thái không hợp lệ" });
    }

    const booking = await Booking.findByIdAndUpdate(
      id,
      { status },
      { new: true }
    );

    if (!booking) {
      return res.status(404).json({ message: "Không tìm thấy booking" });
    }

    res.status(200).json({
      message: "Bookings update successfully",
      data: booking,
    });
  } catch (error) {
    res.status(500).json({ message: "Lỗi server", error });
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
// API lấy tất cả các yêu thích
router.get("/user/:userId/favourites", async (req, res) => {
  try {
    const { userId } = req.params;

    // Truy vấn tất cả các favourite của user
    const favourites = await Favourite.find({ userId });

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

    // Kết hợp thông tin phòng vào mỗi favourite
    const favouritesWithRooms = favourites.map(favourite => {
      const room = rooms.find(room => room._id.toString() === favourite.roomId.toString());
      return { ...favourite.toObject(), room }; // Thêm thông tin phòng vào mỗi favourite
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

    // Kiểm tra user và phòng có tồn tại không
    const user = await User.findById(userId);
    const room = await Room.findById(roomId);
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }
    if (!room) {
      return res.status(404).json({ message: "Room not found" });
    }
    // Tạo yeu thich mới
    const newFavourite = new Favourite({
      userId,
      roomId,
    });

    const savedFavourite = await newFavourite.save();
    res.status(201).json({
      message: "Favourite booked successfully",
      data: savedFavourite,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while booking room", error: error.message });
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
    const deleteFavourite = await Favourite.findByIdAndDelete(id);

    if (deleteFavourite) {
      res.json({ 
        message: "deleteFavourite deleted successfully" 
      });
    } else {
      res.status(404).json({ message: "deleteFavourite not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while deleting deleteFavourite" });
  }
});

module.exports = router;
