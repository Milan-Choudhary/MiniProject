# 🤖 Mini AI Bot

A conversational AI chatbot powered by **Google Gemini 2.5 Flash**, built with **Spring Boot** and **MongoDB**. It features a sleek, modern glassmorphism UI with real-time chat, session persistence, and smooth animations.

---

## ✨ Features

- 🧠 **Gemini 2.5 Flash** — Powered by Google's latest generative AI model
- 💬 **Session-based conversations** — Each browser tab gets a unique session; history is stored in MongoDB
- 🎨 **Modern glassmorphism UI** — Dark theme with gradient accents, typing indicators, and micro-animations
- ⚡ **Quick suggestion chips** — Jump-start conversations with one click
- 📱 **Fully responsive** — Works seamlessly on desktop and mobile
- 🗑️ **Clear chat** — Reset the conversation at any time

---

## 🛠️ Tech Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Backend    | Java 17, Spring Boot 4.0.2          |
| Database   | MongoDB (Spring Data MongoDB)        |
| AI Model   | Google Gemini 2.5 Flash (REST API)  |
| Frontend   | Vanilla HTML, CSS, JavaScript       |
| Build Tool | Maven (Maven Wrapper included)      |
| Utilities  | Lombok                              |

---

## 📁 Project Structure

```
MiniProject/
├── src/
│   ├── main/
│   │   ├── java/com/example/mini_ai_bot/
│   │   │   ├── MiniAiBotApplication.java     # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── ChatController.java       # REST API: POST /api/chat/ask
│   │   │   ├── model/
│   │   │   │   └── AIModel.java              # MongoDB document model
│   │   │   ├── repository/
│   │   │   │   └── chatbotrepo.java          # MongoDB repository
│   │   │   └── service/
│   │   │       └── ChatService.java          # Gemini API integration & session logic
│   │   └── resources/
│   │       └── static/
│   │           └── index.html                # Frontend chat UI
├── pom.xml
└── mvnw / mvnw.cmd                           # Maven wrapper scripts
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** — [Download JDK](https://adoptium.net/)
- **MongoDB** — Running locally on the default port `27017` (or provide a connection URI)
- **Google Gemini API Key** — [Get one here](https://aistudio.google.com/app/apikey)

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/MiniProject.git
cd MiniProject
```

### 2. Configure the Application

Create (or edit) `src/main/resources/application.properties`:

```properties
# MongoDB connection
spring.data.mongodb.uri=mongodb://localhost:27017/mini_ai_bot

# Google Gemini API Key
gemini.api.key=YOUR_GEMINI_API_KEY_HERE

# Server port (default: 8080)
server.port=8080
```

> ⚠️ **Never commit your API key to version control.** Use environment variables or a secrets manager in production.

### 3. Run the Application

**On Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**On macOS/Linux:**
```bash
./mvnw spring-boot:run
```

### 4. Open the Chat UI

Navigate to [http://localhost:8080](http://localhost:8080) in your browser.

---

## 🔌 API Reference

### `POST /api/chat/ask`

Send a message to the AI bot.

**Request Body:**
```json
{
  "sessionId": "session-abc123",
  "message": "What is the speed of light?"
}
```

**Response:** Plain text string containing the AI's reply.

**Example using cURL:**
```bash
curl -X POST http://localhost:8080/api/chat/ask \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-session","message":"Hello!"}'
```

---

## 🗄️ Database Schema

Each chat session is stored as a document in the `data` collection in MongoDB:

```json
{
  "_id": "session-abc123",
  "name": null,
  "title": null,
  "messages": [
    { "role": "user",  "content": "Hello!" },
    { "role": "model", "content": "Hi there! How can I help you today?" }
  ]
}
```

---

## 🖼️ UI Overview

| Component          | Description                                      |
|--------------------|--------------------------------------------------|
| **Header**         | Bot name, live status dot, clear-chat button     |
| **Welcome screen** | Shown before the first message is sent           |
| **Quick chips**    | Pre-built suggestion buttons                     |
| **Chat bubbles**   | Gradient user bubbles, glass-style bot bubbles   |
| **Typing indicator** | Animated three-dot bounce while AI responds    |
| **Input area**     | Auto-resizing textarea with character counter    |

---

## ⚙️ Configuration Reference

| Property                    | Description                    | Default                        |
|-----------------------------|--------------------------------|--------------------------------|
| `gemini.api.key`            | Your Google Gemini API key     | *(required)*                   |
| `spring.data.mongodb.uri`   | MongoDB connection URI         | `mongodb://localhost:27017`    |
| `server.port`               | HTTP server port               | `8080`                         |

---

## 🔒 Security Notes

- The Gemini API key is read from `application.properties` via `@Value`. For production, inject it as an **environment variable** instead:
  ```properties
  gemini.api.key=${GEMINI_API_KEY}
  ```
- CORS is currently set to `*` (all origins). Restrict this in production to your frontend's domain.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

This project is for educational/mini-project purposes. Feel free to use and modify it.

---

<p align="center">Made with ❤️ using Spring Boot & Google Gemini</p>
