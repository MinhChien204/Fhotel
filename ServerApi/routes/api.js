var express = require("express");
var router = express.Router();

const upload = require("../config/common/upload");
const multer = require("multer");
const path = require("path");

const hotels = require("../models/hotels");
const TypeHotels = require("../models/typeHotels");
const users = require("../models/users");
const Room = require("../models/Room");
const RoomService = require("../models/roomservice");
const Service = require("../models/service");
const Voucher = require("../models/vouchers");

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
    let user = await users.find();
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

    const newUser = new users({
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
    const user = await users.findById(id);
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
    const user = await users.findById(id);
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

    const updateUser = await users.findByIdAndUpdate(
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

router.get('/rooms', async (req, res) => {
  try {
    const rooms = await Room.find()

    // Nếu thành công, trả về kết quả
    res.status(200).json({
      status: 200,
      message: 'Rooms fetched successfully',
      data: rooms
    });

  } catch (error) {
    console.error("Error fetching rooms:", error);
    res.status(500).json({
      message: 'An error occurred while fetching rooms'
    });
  }
});

// API thêm phòng mới

router.post('/add_room', upload.array('image', 5), async (req, res) => {
  try {
    const data = req.body; // Lấy dữ liệu từ body
    const { files } = req; // Lấy danh sách file được upload

    // Tạo URLs cho ảnh
    const urlsImage = files.map((file) => {
      return `http://10.0.2.2:3000/uploads/${file.filename}`;
    });

    // Tạo Room mới với dữ liệu
    const newRoom = new Room({
      name: data.name,
      price: data.price,
      rating: data.rating,
      description: data.description,
      capacity: data.capacity,
      status: data.status,
      room_code: data.room_code,
      image: urlsImage // Lưu mảng URLs của ảnh
    });

    const result = await newRoom.save(); // Lưu vào MongoDB

    if (result) {
      res.status(200).json({
        status: 200,
        message: "Thêm Room thành công",
        data: result
      });
    } else {
      res.status(400).json({
        status: 400,
        message: "Thêm Room không thành công",
        data: []
      });
    }
  } catch (error) {
    console.error(error);
    res.status(500).json({
      status: 500,
      message: "Có lỗi xảy ra",
      error
    });
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
    res.status(500).json({ message: "An error occurred while retrieving room" });
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
router.post("/add-service", upload.array('image', 3), async (req, res) => {
  try {
    const data = req.body;
    // Kiểm tra dữ liệu trong request
    console.log("Request Body:", req.body);
    console.log("Uploaded File:", req.file);
    const { files } = req

    // Lưu đường dẫn ảnh vào database
    const urlsImage = files.map((file) => {
      return `http://10.0.2.2:3000/uploads/${file.filename}`;
    });

    // Tạo mới Service
    const newService = new Service({
      name: data.name,
      price: data.price,
      description: data.description,
      image: urlsImage, // Lưu đường dẫn ảnh
    });

    // Lưu vào cơ sở dữ liệu
    const savedService = await newService.save();

    res.status(201).json({
      status: 201,
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
// API xóa dịch vụ theo ID
router.delete("/delete-service/:id", async (req, res) => {
  try {
    const { id } = req.params;
    console.log("Delete request for ID:", id); // Log để kiểm tra id nhận được

    const service = await Service.findById(id);
    if (!service) {
      return res.status(404).json({
        status: 404,
        message: "Service not found",
      });
    }

    const deletedService = await Service.findByIdAndDelete(id);
    res.status(200).json({
      status: 200,
      message: "Service deleted successfully",
      data: deletedService,
    });
  } catch (error) {
    console.error("Error deleting service:", error);
    res.status(500).json({
      status: 500,
      message: "Internal server error",
    });
  }
});

// API lấy thông tin chi tiết dịch vụ theo ID
router.get("/service/:id", async (req, res) => {
  try {
    const { id } = req.params; // Lấy ID từ đường dẫn
    console.log("Fetching service details for ID:", id); // Log kiểm tra

    // Tìm dịch vụ trong cơ sở dữ liệu
    const service = await Service.findById(id);

    if (!service) {
      // Nếu không tìm thấy dịch vụ
      return res.status(404).json({
        status: 404,
        message: "Service not found",
      });
    }

    // Trả về chi tiết dịch vụ nếu tìm thấy
    res.status(200).json({
      status: 200,
      message: "Service details retrieved successfully",
      data: service,
    });
  } catch (error) {
    console.error("Error fetching service details:", error);
    res.status(500).json({
      status: 500,
      message: "An error occurred while retrieving the service details",
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
// const JWT  = require('jsonwebtoken');
// const SECRETKEY = 'FPTPOLYTECHNIC'
router.post("/login", async (req, res) => {
  try {
    const { username, password } = req.body;
    const user = await users.findOne({ username, password });
    if (user) {
      //Token người dùng sẽ dược sử dụng gửi lên trên header mỗi lần gọi API
      // const token = JWT.sign({id: user._id},SECRETKEY,{expiresIn:'1h'});
      //Khi token hết hạn , người dùng sẽ call 1 api khác để lấy token mới
      //người dùng truyền refreshToken lên để nhận 1 cặp token ,refreshToken mới
      // const refreshToken = JWT.sign({id:user._id},SECRETKEY,{expiresIn:'1d'});
      //expiresIn thời gian token
      res.status(200).json({
        status: 200,
        message: "Login successful",
        role: user.role,
        id: user.id


        // "token":token,
        // "refreshToken":refreshToken
      });
    } else {
      res.status(400).json({
        status: 400,
        message: "Login failed: Incorrect username or password",
        data: [],
      });
    }
  } catch (error) {
    console.log(error);
    res.status(500).json({ message: "Internal server error" });
  }
});

//API Register and email
// const Transporter = require('../config/common/mail')
router.post("/register", async (req, res) => {
  try {
    const data = req.body;
    // const { file } = req;
    // const avatar = `${req.protocol}://${req.get("host")}/uploads/${file.filename}`;

    const newUser = new users({
      username: data.username,
      password: data.password,
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
        message: "Thêm thành công",
        data: result,
      });
    } else {
      res.status(400).json({
        status: 400,
        message: "Lỗi, thêm không thành công",
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
}
);

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
module.exports = router;
