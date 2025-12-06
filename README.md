# CareKeeper Aquarium 🐠

A multi-user, client-server aquarium simulation game where users can manage their virtual fish, maintain tank cleanliness, and learn interesting fish facts through an interactive terminal interface.

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Game Mechanics](#game-mechanics)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Testing](#testing)
- [Contributing](#contributing)

## ✨ Features

### Core Gameplay
- **Multi-User Support**: Multiple users can connect simultaneously and manage their own fish
- **Virtual Fish Management**: Add, view, remove, and feed your fish
- **Real-Time Updates**: Aquarium state updates automatically displayed to all connected clients
- **Tank Maintenance**: Monitor and clean tank cleanliness
- **Fish Lifecycle**: Fish grow, get hungry, and can die if not properly cared for
- **Point System**: Earn points for maintaining your fish

### Interactive Features
- **Random Fish Facts**: Get interesting and varied fish facts from an AI-powered API
- **Live Status Display**: Real-time aquarium status header showing tank cleanliness, users online, and your fish
- **Intuitive Menu System**: Arrow-key navigation menus powered by JLine
- **Text Wrapping**: Automatic text wrapping for better readability

### Technical Features
- **Observer Pattern**: Real-time state synchronization across all clients
- **Thread Pool Management**: Efficient handling of multiple client connections
- **Scheduled Tasks**: Automatic tank updates every minute
- **Graceful Shutdown**: Proper resource cleanup and user session management

## 🏗️ Architecture

The application follows a **client-server architecture** with clear separation of concerns:

```
┌─────────────┐         ┌─────────────┐         ┌──────────────┐
│   Client    │◄───────►│   Server    │◄───────►│ AquariumState│
│  (Console)  │  Socket │ (Handlers)  │ Observer│  (Singleton) │
└─────────────┘         └─────────────┘         └──────────────┘
```

### Key Components

- **Client Layer**: Terminal UI with JLine for interactive menus and real-time updates
- **Server Layer**: Multi-threaded server handling concurrent client connections
- **Business Layer**: Game logic, fish factory, and thread pool management
- **Model Layer**: Domain entities (Fish, UserProfile, AquariumState)
- **Integration Layer**: External API integration for fish facts

### Design Patterns

- **Singleton Pattern**: AquariumState ensures single shared state
- **Observer Pattern**: StateObserver notifies all clients of state changes
- **Factory Pattern**: FishFactory creates randomized fish instances
- **Command Pattern**: Enum-based command system for client-server communication

## 🔧 Prerequisites

- **Java 21** or higher
- **Maven 3.6+** for building
- **Windows OS** (batch scripts provided; adaptable for Linux/Mac)
- Internet connection (for fish facts API)

## 📦 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/JKaulback/care-keeper-aquarium.git
   cd carekeeperaquarium
   ```

2. **Build the project**
   ```bash
   build.bat
   ```
   Or using Maven directly:
   ```bash
   mvn clean package
   ```

3. **Verify the build**
   - The JAR file will be created at: `target/carekeeperaquarium-1.0-SNAPSHOT.jar`

## 🚀 Usage

### Starting the Server

Run the server first (only one server instance needed):

```bash
server.bat
```

Or manually:
```bash
java -jar target/carekeeperaquarium-1.0-SNAPSHOT.jar
```

The server will start on **port 8080** and wait for client connections.

### Starting the Client

Open a new terminal and run:

```bash
client.bat
```

Or manually:
```bash
java -jar target/carekeeperaquarium-1.0-SNAPSHOT.jar client
```

Multiple clients can connect simultaneously!

### Quick Start Script

Run both server and client at once:
```bash
.\server; .\client
```

## 🎮 Game Mechanics

### Login
- Enter a unique username (alphanumeric, spaces, hyphens, underscores allowed)
- Username must not already be in use

### Menu Options

1. **Add Fish** - Randomly generate a new fish to your collection
2. **View Your Fish** - See all your fish with their health status
3. **Feed Your Fish** - Feed all your living fish to restore health
4. **Remove Fish** - Select and remove a fish from your collection
5. **Clean Tank** - Restore tank cleanliness to maximum
6. **View Tank** - See overall aquarium statistics for all users
7. **Get Fish Fact** - Receive a random, interesting fish fact
8. **Quit** - Disconnect from the server

### Fish Characteristics

Each fish has:
- **Species**: 12 different types (Clownfish, Betta, Angel Fish, etc.)
- **Health**: Current health / Maximum health
- **Size**: Small, Medium, or Large
- **Age**: Tracked in days
- **Hunger Level**: Affects health over time

### Automatic Events (Every Minute)

- Tank cleanliness decreases based on fish count and size
- Fish get hungrier
- Fish grow larger
- Users earn points
- All clients receive status updates

## 📁 Project Structure

```
carekeeperaquarium/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── carekeeperaquarium/
│   │               ├── Main.java                    # Application entry point
│   │               ├── business/                    # Business logic layer
│   │               │   ├── AquariumManager.java     # Main game logic coordinator
│   │               │   ├── FishFactory.java         # Fish creation factory
│   │               │   └── ThreadPoolManager.java   # Concurrent execution
│   │               ├── client/                      # Client-side components
│   │               │   ├── AquariumClient.java      # Client networking
│   │               │   ├── ConsoleUI.java           # Terminal UI wrapper
│   │               │   ├── Menu.java                # Menu data structure
│   │               │   └── MenuHandler.java         # Menu logic
│   │               ├── common/                      # Shared components
│   │               │   └── Command.java             # Command protocol
│   │               ├── integration/                 # External integrations
│   │               │   └── FishFactAPI.java         # Fish facts API client
│   │               ├── model/                       # Domain models
│   │               │   ├── AquariumState.java       # Global state (Singleton)
│   │               │   ├── Fish.java                # Fish entity
│   │               │   └── UserProfile.java         # User entity
│   │               └── server/                      # Server-side components
│   │                   ├── AquariumServer.java      # Server socket manager
│   │                   ├── ClientHandler.java       # Per-client handler
│   │                   └── StateObserver.java       # Observer pattern impl
│   └── test/
│       └── java/
│           └── com/
│               └── carekeeperaquarium/
│                   ├── business/                    # Business layer tests
│                   └── model/                       # Model layer tests
├── pom.xml                                          # Maven configuration
├── build.bat                                        # Build script
├── server.bat                                       # Server launcher
├── client.bat                                       # Client launcher
└── README.md                                        # This file
```

## 🛠️ Technologies Used

### Core Technologies
- **Java 21**: Primary programming language
- **Maven**: Build automation and dependency management

### Libraries & Frameworks
- **JLine 3.21.0**: Rich terminal UI with ANSI support, arrow-key navigation
- **Gson 2.10.1**: JSON parsing for API responses
- **JNA 5.13.0**: Native library access for terminal control
- **JUnit Jupiter 5.9.3**: Unit testing framework

### APIs
- **JsonGPT API**: AI-powered fish fact generation

### Development Tools
- **VS Code**: Primary IDE
- **Git**: Version control
- **Windows Batch Scripts**: Build and run automation

## 🧪 Testing

Run the test suite:

```bash
mvn test
```

### Test Coverage

Tests are included for:
- `AquariumManager` business logic
- `ThreadPoolManager` concurrency
- `AquariumState` state management
- `Fish` entity behavior
- `UserProfile` user operations

Test reports are generated in: `target/surefire-reports/`

## 🏗️ Building from Source

### Full Build
```bash
mvn clean package
```

### Skip Tests
```bash
mvn clean package -DskipTests
```

### Run Tests Only
```bash
mvn test
```

### Clean Build Artifacts
```bash
mvn clean
```

## 🔌 API Integration

The application integrates with the **JsonGPT API** for fish facts:

- **Endpoint**: `https://api.jsongpt.com/json`
- **Prompt Engineering**: Uses 90+ unique prompt combinations for variety
- **Error Handling**: Graceful fallbacks for API failures
- **Timeout**: 10-second connection timeout

## 🎯 Future Enhancements

Potential features for future versions:
- Fish breeding system
- Rare/legendary fish species
- User achievements and leaderboards
- Save/load game state to database
- Web-based client interface
- Fish trading between users
- Customizable aquarium themes

## 📝 Known Issues

- API occasionally returns error codes (handled gracefully)
- Terminal ANSI codes may not work on all terminal emulators
- Windows-specific batch scripts (Linux/Mac users need shell scripts)

## 👥 Contributing

This is an educational project for PROG2200 at NSCC. 

## 📄 License

This project is developed as part of an academic assignment.

## 👨‍💻 Author

**JKaulback**
- GitHub: [@JKaulback](https://github.com/JKaulback)
- Project: [care-keeper-aquarium](https://github.com/JKaulback/care-keeper-aquarium)

## 🙏 Acknowledgments

- NSCC PROG2200 instructors and course materials
- JsonGPT API for fish fact generation
- JLine library for terminal UI capabilities
- Open source community for inspiration and resources

---

**Happy Fish Keeping! 🐠🐟🐡**
