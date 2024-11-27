var express = require("express");
var router = express.Router();

const upload = require("../config/common/upload");
const multer = require("multer");
const path = require("path");

const hotels = require("../models/hotels");
const TypeHotels = require("../models/typeHotels");
const User = require("../models/users");
const Room = require("../models/Room");
const RoomService = require("../models/roomservice");
const Service = require("../models/service");
const Voucher = require("../models/vouchers");
const Booking = require("../models/booking");

const bcrypt = require('bcrypt');
const saltRounds = 10;
const jwt = require('jsonwebtoken');
const SECRET_KEY = process.env.SECRET_KEY;
// CRUD Hotel
//APi hiển thi danh sách khách sạn
router.get("/hotel", async (req, res) => {
  try {
    let hotel = await hotels.find();
    res.send(hotel);
  } catch (error) {
    console.log("lỗi");
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
    const user = await users.findById(id);

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
      const updateUser = await users.findByIdAndUpdate(
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
    const deleteUser = await users.findByIdAndDelete(id);

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
// API hiển thị danh sách phòng
router.get("/rooms", async (req, res) => {
  // //authorization thêm từ khóa Bearer token
  // const authHeader = req.headers['authorization'];
  // const token = authHeader && authHeader.split(' ')[1];
  // // nếu không có token chuyển về 401
  // if(token==null) return res.sendStatus(401);
  // let payload;
  // // JWT.verify(token,SECRETKEY,(err,_payload)=>{
  // //   //kiểm tra token, nếu không đúng hoặc hết hạn
  // //   //trả status code 403
  // //   //trả status hết hạn 301 khi token hết hạn
  // //   if(err instanceof JWT.TokenExpiredError) return res.status(401)
  // //   if(err) return res.status(403)
  // //     //Nếu đúng log ra toàn bộ dữ liệu
  // //   payload=_payload;
  // // })
  // console.log(payload);

  try {
    const rooms = await Room.find();
    res.status(200).json({
      status: 200,
      message: "Rooms retrieved successfully",
      data: rooms,
    });
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({ message: "An error occurred while retrieving rooms" });
  }
});

// API thêm phòng mới
router.post("/add_room", async (req, res) => {
  try {
    const {
      name,
      price,
      rating,
      description,
      image,
      capacity,
      status,
      room_code,
    } = req.body;

    const newRoom = new Room({
      name,
      price,
      rating,
      description,
      image,
      capacity,
      status,
      room_code,
    });

    const result = await newRoom.save();
    res.status(201).json({
      status: 200,
      message: "Room added successfully",
      data: result,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while adding room" });
  }
});

// API xem chi tiết phòng
router.get("/room/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const room = await Room.findById(id);

    if (room) {
      res.status(200).json({
        status: 200,
        message: "Room retrieved successfully",
        data: room,
      });
    } else {
      res.status(404).json({ message: "Room not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({ message: "An error occurred while retrieving room" });
  }
});

// API cập nhật phòng
router.put("/update_room/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const {
      name,
      price,
      rating,
      description,
      image,
      capacity,
      status,
      room_code,
    } = req.body;

    const updateRoom = await Room.findByIdAndUpdate(
      id,
      {
        name,
        price,
        rating,
        description,
        image,
        capacity,
        status,
        room_code,
      },
      { new: true }
    );

    if (updateRoom) {
      res.status(200).json({
        status: 200,
        message: "Room updated successfully",
        data: updateRoom,
      });
    } else {
      res.status(404).json({ message: "Room not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while updating room" });
  }
});

// API xóa phòng
router.delete("/delete_room/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const deleteRoom = await Room.findByIdAndDelete(id);

    if (deleteRoom) {
      res.status(200).json({ message: "Room deleted successfully" });
    } else {
      res.status(404).json({ message: "Room not found" });
    }
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while deleting room" });
  }
});

// API lấy danh sách dịch vụ theo phòng
router.get("/room/:id/services", async (req, res) => {
  try {
    const { id } = req.params;
    const roomServices = await RoomService.find({ roomId: id }).populate(
      "serviceId"
    );

    // Lấy danh sách dịch vụ từ roomServices
    const services = roomServices.map((rs) => rs.serviceId);

    res.status(200).json({
      status: 200,
      message: "Services retrieved successfully",
      data: services,
    });
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({
        message: "An error occurred while retrieving services",
        error: error.message,
      });
  }
});

router.post("/room/:roomId/add_service", async (req, res) => {
  try {
    const { roomId } = req.params;
    const { serviceId } = req.body;

    const newRoomService = new RoomService({
      roomId,
      serviceId,
    });

    const result = await newRoomService.save();
    res.status(201).json({
      status: 200,
      message: "Service added to room successfully",
      data: result,
    });
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({
        message: "An error occurred while adding service to room",
        error: error.message,
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

router.get("/roomservice", async (req, res) => {
  try {
    const rservices = await RoomService.find();
    res.status(200).json({
      status: 200,
      message: "RoomService retrieved successfully",
      data: rservices,
    });
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({ message: "An error occurred while retrieving services" });
  }
});

// CRUD Hotel

// API hiển thị danh sách khách sạn
router.get("/hotels", async (req, res) => {
  try {
    let hotels = await Hotel.find();
    res.status(200).json({
      status: 200,
      message: "Hotels retrieved successfully",
      data: hotels,
    });
  } catch (error) {
    console.error("Error:", error);
    res
      .status(500)
      .json({ message: "An error occurred while retrieving hotels" });
  }
});

// API hiển thị danh sách người dùng
// router.get("/users", async (req, res) => {
//   try {
//       let users = await User.find();
//       res.status(200).json({
//           status: 200,
//           message: "Users retrieved successfully",
//           data: users,
//       });
//   } catch (error) {
//       console.error("Error:", error);
//       res.status(500).json({ message: "An error occurred while retrieving users" });
//   }
// });

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

    // Kiểm tra phòng có bị đặt trùng thời gian không
    const overlappingBooking = await Booking.findOne({
      roomId,
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
      return res.status(404).json({ message: "No bookings found for this user" });
    }

    // Lấy tất cả roomId của các booking
    const roomIds = bookings.map(booking => booking.roomId);

    // Truy vấn thông tin các phòng dựa trên roomIds
    const rooms = await Room.find({ '_id': { $in: roomIds } });

    if (!rooms || rooms.length === 0) {
      return res.status(404).json({ message: "No rooms found for the bookings" });
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
router.put("/update_booking/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const { status } = req.body;

    const updatedBooking = await Booking.findByIdAndUpdate(
      id,
      { status },
      { new: true }
    );

    if (!updatedBooking) {
      return res.status(404).json({ message: "Booking not found" });
    }

    res.status(200).json({
      message: "Booking status updated successfully",
      data: updatedBooking,
    });
  } catch (error) {
    console.error("Error:", error);
    res.status(500).json({ message: "An error occurred while updating booking" });
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

module.exports = router;
