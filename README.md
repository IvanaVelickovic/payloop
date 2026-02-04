# PayLoop 💸

PayLoop is a Kotlin-based Android app for managing subscriptions. Track trials, set smart reminders, and analyze spending. Features a unique "work-hour" cost calculator and advanced shared-expense tools with QR payment generation. Built with Jetpack Compose.

### Key Features
* **Smart Reminders:** Custom notifications before charge dates.
* **Work-Hour Cost:** Calculates the "real" cost of a subscription based on your hourly wage.
* **Shared Expenses:** Advanced tools for splitting bills with QR payment generation.

---

## 🌳 Branch-naming Convention

We follow a strict naming convention to keep the repository organized.
Format: `<initials>/<type>/<short-description>`

### 1. Initials
Use the initials of the person working on the branch.

### 2. Types
* `feature`: A new feature (e.g., add-subscription-flow)
* `fix`: A bug fix
* `ui`: Visual changes or layout adjustments
* `refactor`: Code restructuring without behavior changes
* `docs`: Documentation only changes
* `chore`: Maintenance tasks (dependencies, build setup)

### Examples
* `iv/ui/dashboard-layout`
* `md/feature/room-database-setup`
* `iv/fix/reminder-crash`

---

## 📂 Project Structure (current)

This project follows a **Package by Feature** architecture for the UI layer, with a centralized Data layer.

### Why this structure?
It keeps related UI code (Screens + ViewModels) together, making features easy to isolate and maintain, while sharing the core Data logic.

```text
com.app.payloop
├── data                         # 📦 Centralized Data Layer
│   ├── local                    # Room Database & DAOs
│   ├── model                    # Data classes (Entities)
│   └── repository               # Single source of truth
│
├── ui                           # 🎨 UI Layer (Package by Feature)
│   ├── add_subscription         # Add flow
│   ├── components               # Shared Composables
│   │
│   ├── editsubscription         # Edit existing sub logic
│   │
│   ├── mainscreen               # Dashboard / Home
│   │
│   ├── settings                 # App preferences
│   │
│   ├── subscriptionview         
│   │
│   └── theme                    # Design System
│
└── MainActivity.kt              # App Entry Point