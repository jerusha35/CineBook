# 🎟 CinePass Tamil Nadu - Movie Ticket Booking App (Java Swing)

A modern, cinema-themed Java Swing desktop application featuring authentication, role-based controls (Admin & Customer), interactive movie seat selection & booking, and Kollywood / Tamil Nadu current cinema details.

---

## 🌟 Key Features

### 🗄️ Database Integration: Oracle Database 19c
- **Database Engine**: Oracle 19c Enterprise Edition (`localhost:1521:orcl`)
- **Credentials**: User `system`, Password `livewire`
- **JDBC Driver**: `lib/ojdbc.jar`
- **Tables Auto-Created & Managed in Oracle**:
  - `cinepass_users`: User logins, customer profiles, admin flags (`is_admin`)
  - `cinepass_movies`: Movie IDs, titles, ratings, runtime, Tamil Nadu theatres, prices
  - `cinepass_bookings`: Confirmed ticket bookings, customer references, amounts, timestamps
  - `cinepass_booked_seats`: Real-time seat occupancy (`A1`-`F10`) per movie/theatre/date/time with primary key collision safety.
- **Admin Control**:
  - Username: `admin`
  - Password: `admin`
  - Full movie catalogue management (Add / Remove movies).
  - Real-time business analytics KPI cards (Total Revenue, Total Bookings, Active Movies).
  - Complete customer booking audit logs with timestamps.
- **User / Customer Control**:
  - Default User: `user` / `user123`
  - In-app **Registration** dialog for new customer accounts.
  - Browse movies with real-time search and genre/language filters.
  - "My Bookings" tab with instant e-Ticket viewer.

### 2. 🎬 Tamil Nadu Now Playing Movies & Theatres
- Movies pre-loaded with Tamil Nadu current hits:
  - **GOAT - The Greatest of All Time** (Thalapathy Vijay / Venkat Prabhu)
  - **Vettaiyan** (Superstar Rajinikanth / T.J. Gnanavel)
  - **Amaran** (Sivakarthikeyan / Sai Pallavi)
  - **Kanguva** (Suriya / Siva)
  - **Meiyazhagan** (Karthi / Arvind Swami)
  - **Leo (Special Show Re-Release)** (Thalapathy Vijay / Lokesh Kanagaraj)
- Renowned Tamil Nadu Theatres:
  - *SPI Sathyam Cinemas (Royapettah, Chennai)*
  - *Rohini Silver Screens (Koyambedu, Chennai)*
  - *Vettri Theatres (Chromepet, Chennai)*
  - *PVR Inox Luxe (Velachery, Chennai)*
  - *Broadway Cinemas (KMCH, Coimbatore)*
  - *KG Cinemas (Race Course, Coimbatore)*
  - *Vetri Cinemas & Gopuram Cinemas (Madurai)*

### 3. 💺 Interactive Seat Check & Booking
- Curved cinematic screen visualizer.
- Realistic seat layout grid (`A1` to `F10`) with aisle walkways.
- Distinct color-coded statuses:
  - 🟦 **Available**
  - 🟩 **Selected**
  - ⬛ **Sold Out / Already Booked** (with real-time occupancy)
- Live price tally and seat selection badge counter.
- Duplicate booking prevention and confirmed e-ticket generation.

---

## 🚀 How to Run

### Option 1: Double-click launcher
Double click [`run.bat`](file:///d:/Jerusha/aiasst/run.bat) in the project root directory.

### Option 2: Command Line (PowerShell / CMD)
```bash
# 1. Compile
javac -d bin -sourcepath src src/com/movieticket/Main.java

# 2. Run
java -cp bin com.movieticket.Main
```
