# SayHi – Java Chat Application

**SayHi** is a real-time, desktop-based chat application developed in Java using AWT and Swing for the graphical user interface (GUI), and Java Sockets for client-server communication. The application supports user registration, login, and one-to-one messaging, with data stored in a MySQL database.

---

## 📂 Project Structure

```
SayHiApp-Java-Chat-Application/
├── client/          # Client-side application
├── server/          # Server-side application
├── model/           # Data models
├── util/            # Utility classes
├── db/              # Database scripts
├── lib/             # External libraries
├── images/          # Application assets
├── sayhi_chat.sql   # MySQL database schema
└── .gitignore       # Git ignore file
```

---

## 🚀 Features

* **Real-Time Messaging:** Instant one-to-one communication between users.
* **User Authentication:** Secure login and registration system.
* **Database Integration:** User data and chat history stored in a MySQL database.
* **GUI Interface:** User-friendly interface built with Java AWT and Swing.
* **Client-Server Architecture:** Utilizes Java Sockets for communication.

---

## 🛠️ Technologies Used

* **Programming Language:** Java
* **GUI Framework:** AWT & Swing
* **Database:** MySQL
* **Communication Protocol:** Java Sockets
* **IDE:** Eclipse / IntelliJ IDEA / NetBeans

---

## ⚙️ Setup Instructions

### Prerequisites

* Java Development Kit (JDK) 8 or higher
* MySQL Server
* Integrated Development Environment (IDE) like Eclipse, IntelliJ IDEA, or NetBeans

### Steps to Run

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/chaithali2003/SayHiApp-Java-Chat-Application.git
   cd SayHiApp-Java-Chat-Application
   ```

2. **Set Up the Database:**

   * Import the `sayhi_chat.sql` file into your MySQL server to create the necessary database and tables.

3. **Configure Database Connection:**

   * Update the database connection details in the client and server applications to match your MySQL setup.

4. **Run the Server:**

   * Compile and run the server-side application located in the `server/` directory.

5. **Run the Client:**

   * Compile and run the client-side application located in the `client/` directory.

---

## 📝 License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
