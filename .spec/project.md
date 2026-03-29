# 📌 Project Specification — MCP Client (Spring Boot MVC Chat Application)

## 🎯 Objective

Build a **Java Spring Boot MVC application** that acts as a **client for an existing MCP server**.
The application provides a **chat interface (similar to ChatGPT)** allowing a user to interact with an LLM, which can leverage tools exposed by the MCP server (e.g., e-commerce analytics).

---

## 🧱 Technical Stack

* **Backend:** Java, Spring Boot
* **Architecture:** MVC (Controller → Service → Repository)
* **Frontend:** Thymeleaf (server-side rendering)
* **Database:** Embedded SQLite
* **Communication:** MCP Server (already implemented)
* **Session Management:** HTTP Session (no JWT, no Bearer tokens)

---

## 👤 Authentication & Users

* The application supports **exactly 2 users**:

    * `admin` (role: ADMIN)
    * `user` (role: USER)

* Authentication is **session-based**:

    * Login via form (`/login`)
    * Session stores authenticated user
    * Protected routes require authentication

* No complex security (no OAuth, no JWT)

---

## 💬 Core Feature: Chat Interface

### General Behavior

* The application provides a **1-to-1 chat** between:

    * the authenticated user
    * the LLM (via MCP server)

* The UI should resemble a **ChatGPT-style interface**:

    * Sidebar with conversation history
    * Main chat area with messages
    * Input field for sending messages

---

## 🗂️ Data Model

### Conversation

* `id`
* `userId`
* `title`
* `createdAt`

### Message

* `id`
* `conversationId`
* `role` (USER | ASSISTANT)
* `content`
* `createdAt`

### Relationships

* One **Conversation** has many **Messages**
* Each conversation belongs to a **User**

---

## 🧠 Chat Flow

1. User selects or creates a conversation
2. User sends a message
3. Backend:

    * Saves user message in DB
    * Retrieves full conversation history
    * Sends it to MCP server
4. MCP server:

    * Processes request (LLM + tools)
    * Returns assistant response
5. Backend:

    * Saves assistant response
    * Returns it to UI

---

## 🔌 MCP Integration

* The MCP server is already implemented and exposes tools

* The client must:

    * Send conversation history in the expected format
    * Receive and display LLM responses

* A dedicated component should handle this:

    * `MCPClient` (service layer abstraction)

---

## 🖥️ User Interface

### Pages

#### `/login`

* Username/password form

#### `/chat`

* Main chat interface

---

### Chat Layout

#### Sidebar

* List of conversations (by user)
* Button: "New Conversation"

#### Main Panel

* Message history (chronological)
* Input field to send messages

---

## 💾 Persistence

* Use **SQLite embedded database**

* Store:

    * Users
    * Conversations
    * Messages

* ORM: Spring Data JPA

---

## ⚡ UX Requirements

* Initial version can use **full page reloads**
* Optional improvements:

    * AJAX for sending messages
    * Streaming responses (SSE)

---

## 👑 Admin Features (Business Use Case)

The main purpose of the application is to allow an **e-commerce administrator** to query business data via the LLM.

### Examples:

* “What are today’s sales?”
* “Top selling products?”
* “Revenue this month?”

### Behavior:

* The LLM uses MCP tools to answer
* The client only displays results

---

## 🚫 Non-Goals

* No REST API-first architecture
* No JWT / OAuth / external auth provider
* No multi-user scalability
* No microservices

---

## 🏗️ Suggested Package Structure

```
controller/
service/
repository/
model/
dto/
config/
mcp/
```

---

## ✅ Expected Outcome

A fully functional **chat-based MVC web application** where:

* Users can log in
* Users can manage conversations
* Messages are persisted
* The LLM responds via MCP server
* Admin can query business data through natural language

---

## 💡 Notes

* Keep the implementation **simple and pragmatic**
* Focus on:

    * clean architecture
    * working chat flow
    * MCP integration
* UI can be minimal but usable
