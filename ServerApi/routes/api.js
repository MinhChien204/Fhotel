var express = require('express');
var router = express.Router();

const hotels = require('../models/hotels');
const TypeHotels = require('../models/typeHotels')
const users = require('../models/users')
const Room = require('../models/Room'); 
const RoomService=require('../models/roomservice')
const Service=require('../models/service')
const Upload = require('../config/common/upload');

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
      const { username, password, email, name,gioitinh, phonenumber, address, avartar } = req.body;
  
      const newUser = new users({
        username,
        password,
        email,
        name,
        gioitinh,
        phonenumber,
        address,
        avartar,
      });
  
      const result = await newUser.save();
      res.status(201).json({
        status: 200,
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
      res.status(500).json({ message: "An error occurred while retrieving user" });
    }
  });

//API update user
router.put("/update_user/:id", async (req, res) => {
    try {
      const { id } = req.params;
      const { username, password, email, name,gioitinh, phonenumber, address, avartar } = req.body;
  
      const updateUser = await users.findByIdAndUpdate(id, {
        username,
        password,
        email,
        name,
        gioitinh,
        phonenumber,
        address,
        avartar,
      }, { new: true });
  
      if (updateUser) {
        res.json({
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
  try {
      const rooms = await Room.find();
      res.status(200).json({
          status: 200,
          message: "Rooms retrieved successfully",
          data: rooms,
      });
  } catch (error) {
      console.error("Error:", error);
      res.status(500).json({ message: "An error occurred while retrieving rooms" });
  }
});

// API thêm phòng mới
router.post("/add_room", async (req, res) => {
  try {
      const { name, price, rating, description, image, capacity, status, room_code } = req.body;

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
      res.status(500).json({ message: "An error occurred while retrieving room" });
  }
});

// API cập nhật phòng
router.put("/update_room/:id", async (req, res) => {
  try {
      const { id } = req.params;
      const { name, price, rating, description, image, capacity, status, room_code } = req.body;

      const updateRoom = await Room.findByIdAndUpdate(id, {
          name,
          price,
          rating,
          description,
          image,
          capacity,
          status,
          room_code,
      }, { new: true });

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
      const roomServices = await RoomService.find({ roomId: id }).populate('serviceId');

      // Lấy danh sách dịch vụ từ roomServices
      const services = roomServices.map(rs => rs.serviceId);

      res.status(200).json({
          status: 200,
          message: "Services retrieved successfully",
          data: services
      });
  } catch (error) {
      console.error("Error:", error);
      res.status(500).json({ message: "An error occurred while retrieving services", error: error.message });
  }
});

router.post("/room/:roomId/add_service", async (req, res) => {
  try {
      const { roomId } = req.params;
      const { serviceId } = req.body;

      const newRoomService = new RoomService({
          roomId,
          serviceId
      });

      const result = await newRoomService.save();
      res.status(201).json({
          status: 200,
          message: "Service added to room successfully",
          data: result
      });
  } catch (error) {
      console.error("Error:", error);
      res.status(500).json({ message: "An error occurred while adding service to room", error: error.message });
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
      res.status(500).json({ message: "An error occurred while retrieving hotels" });
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

//API Login 
router.post("/login",async (req, res) => {
  try {
    const {username,password} = req.body;
    const user = await users.findOne({ username,password})
    if(user){
      res.json({
        status: 200,
        message: "Login successful",
        data: user,
      });
    }else{
      res.json({
        status: 400,
        message: "Login failed",
        data:[]
      })
    }
  } catch (error) {
    console.log(error);
    
  }
})
module.exports = router; 