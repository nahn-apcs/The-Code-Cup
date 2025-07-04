# ☕ The Code Cup – CS426 Midterm Project

Welcome to **The Code Cup** – a modern mobile coffee ordering application developed as part of the **CS426 - Mobile Device Application Development** course.  
This app provides a seamless and visually appealing experience for browsing, customizing, and ordering coffee.

🎥 **Demo Video**: [Watch on YouTube](https://www.youtube.com/watch?v=I8ElBIAQD5c)

---

## 📱 Features

- 🌐 **Language Selection**: Toggle between English and Vietnamese.
- 👤 **Phone Number Login**: Authenticate using your phone number.
- 🏠 **Home Screen**:
  - Best Seller carousel with auto-slide and focus animation.
  - Loyalty points display (stamp-based system).
- ☕ **Coffee Details Screen**:
  - Product customization (size, temperature, ice).
  - Real-time price calculation with VND/USD toggle.
  - Add to cart functionality.
- 🛒 **Cart Screen**:
  - View and manage current cart items.
  - Proceed to checkout.
- 👤 **Profile Screen**:
  - Display user information from Firebase.
- 🎁 **Rewards**:
  - View and redeem loyalty-based rewards.

---

## 🛠️ Built With

- **Kotlin**
- **Jetpack Compose**
- **Firebase Firestore** – for storing user data and orders
- **Firebase Authentication** – phone number based login
- **Navigation Compose** – smooth animated transitions
- **Accompanist** – for advanced animations and effects

---

## 📂 Project Structure

```
📁 coffee_application/
├── ui/
│   ├── navigation/            # AppNavGraph & transitions
│   ├── screen/
│   │   ├── splash/
│   │   ├── home/
│   │   ├── details/
│   │   ├── cart/
│   │   └── profile/
├── model/                     # Data classes
├── manager/                   # Firebase and logic managers
├── viewmodel/                 # State management
└── res/
    ├── drawable/              # Icons and images
    └── font/                  # Fonts (e.g., Poppins)
```

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/YOUR_USERNAME/the-code-cup.git
cd the-code-cup
```

### 2. Setup Firebase
- Add your `google-services.json` to `app/` directory.
- Enable **Phone Authentication** and **Cloud Firestore** in Firebase console.

### 3. Run the App
Open in Android Studio, sync Gradle, and run on emulator or physical device.

---

## 🙋‍♂️ Author

**Thành Nhân Nguyễn**  
- 📧 kenpro030709@gmail.com
- 📹 [Demo Video on YouTube](https://www.youtube.com/watch?v=I8ElBIAQD5c)

---

## 📄 License

This project is part of coursework for CS426 and is intended for educational purposes.
