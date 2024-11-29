var express = require('express');
const { authenticateToken, logout } = require("../middleware/middleware");
var router = express.Router();

/* GET home page. */
router.get('/', (req, res) => {
  res.render('login',{ title:'Fhotel - Space to live your life'});  // Render view login.hbs
});
router.get('/dashbroad', function(req, res, next) {
  res.render('index', { title: 'Dashbroad Admin' });
});
router.get('/usermanagerment', function(req, res, next) {
  res.render('usermanagerment', { title: 'Users Managerment' });
});
router.get('/roommanagerment', function(req, res, next) {
  res.render('roommanagerment', { title: 'Rooms Managerment' });
});
router.get('/servicemanagerment', function(req, res, next) {
  res.render('servicemanagerment', { title: 'Services Managerment' });
});
router.get('/bookingmanagerment', function(req, res, next) {
  res.render('bookingmanagerment', { title: 'Booking Managerment' });
});
router.get('/setting', function(req, res, next) {
  res.render('setting', { title: 'Setting' });
});
router.post("/logout", authenticateToken, logout);
// Route GET cho trang login



module.exports = router;
