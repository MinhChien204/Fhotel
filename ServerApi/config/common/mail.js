var nodemailer = require("nodemailer");
const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: "chiennmph39754@fpt.edu.vn",
        pass: "112233445"
    }
})

module.exports =transporter;