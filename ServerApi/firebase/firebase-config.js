// firebase-config.js

// Cấu hình Firebase (lấy từ Firebase Console)
const firebaseConfig = {
    apiKey:process.env.API_KEY_FIREBASE,
    authDomain: process.env.AUTH_DOMAIN,
    projectId: process.env.PROJECT_ID,
    storageBucket: process.env.STORAGE_BUCKET,
    messagingSenderId: process.env.MESSAGINGSENDER_ID,
    appId: process.env.APP_ID,
    measurementId: process.env.MEASUREMENT_ID
  }; 
  
  // Khởi tạo Firebase
  const app = firebase.initializeApp(firebaseConfig);
  const messaging = firebase.messaging();
  