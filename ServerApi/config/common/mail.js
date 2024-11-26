var nodemailer = require("nodemailer");
const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: "fhotel.booking@gmail.com",
        pass: "Chien202204@"
    }
})

module.exports =transporter;