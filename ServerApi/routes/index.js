var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', (req, res) => {
  res.render('login');  // Render view login.hbs
});
router.get('/dashbroad', function(req, res, next) {
  res.render('index', { title: 'Dashbroad Admin' });
});
// Route GET cho trang login



module.exports = router;
