var express = require('express');
var router = express.Router();

const hotels = require('../models/hotels');
const TypeHotels = require('../models/typeHotels')
const users = require('../models/users')
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
module.exports = router; 