# Advanced Programming – Assignment 6

## HTTP Server for Computational Graphs

### Author
Saar Golan

---

# Project Description

This project implements a lightweight HTTP server in Java for creating and interacting with computational graphs through a web interface.

Users can upload graph configurations, publish values to topics, visualize the generated graph, and observe how values propagate through the computation in real time.

The project integrates all components developed throughout Assignments 1–6, including the publish/subscribe mechanism, dynamic configuration loading, graph generation, HTTP communication, and web visualization.

---

# Features

- Upload computational graph configuration files.
- Dynamic graph creation using Java Reflection.
- Interactive SVG graph visualization.
- Draggable graph nodes with automatically updated connections.
- Automatic generation of the mathematical equation represented by the graph.
- Publish values to graph topics.
- Live table displaying the latest value of every topic.
- Configuration validation before deployment.
- Friendly error page for invalid configuration files.
- Multi-threaded execution using ParallelAgent.

---

# Project Structure

```text
.
├── src
│   ├── configs
│   │   ├── Config.java
│   │   ├── ConfigValidator.java
│   │   ├── GenericConfig.java
│   │   ├── Graph.java
│   │   └── Node.java
│   │
│   ├── graph
│   │   ├── Agent.java
│   │   ├── IncAgent.java
│   │   ├── Message.java
│   │   ├── MinAgent.java
│   │   ├── MulAgent.java
│   │   ├── ParallelAgent.java
│   │   ├── PlusAgent.java
│   │   ├── Topic.java
│   │   └── TopicManagerSingleton.java
│   │
│   ├── server
│   │   ├── HTTPServer.java
│   │   ├── MyHTTPServer.java
│   │   └── RequestParser.java
│   │
│   ├── servlets
│   │   ├── ConfLoader.java
│   │   ├── HtmlLoader.java
│   │   ├── Servlet.java
│   │   └── TopicDisplayer.java
│   │
│   ├── views
│   │   └── HtmlGraphWriter.java
│   │
│   └── Main.java
│
└── README.md
```

---

# Architecture

The project follows the MVC (Model–View–Controller) architecture.

### Model

Responsible for the application logic.

Includes:

- Topics
- Agents
- Messages
- Graph
- GenericConfig
- ParallelAgent

---

### Controller

Responsible for handling HTTP requests.

Includes:

- MyHTTPServer
- HtmlLoader
- ConfLoader
- TopicDisplayer

---

### View

Responsible for presenting the system.

Includes:

- HTML pages
- HtmlGraphWriter
- Interactive SVG graph

---

# Supported Agents

The project currently includes:

- PlusAgent
- MinAgent *(performs subtraction)*
- MulAgent
- IncAgent

Additional agents can be added simply by implementing the `Agent` interface and referencing the class in the configuration file.

---

# Running the Project

1. Run `Main.java`.
2. Open:

```
http://localhost:8080/app/index.html
```

3. Upload a configuration file.
4. Publish values through the web interface.
5. Observe:

- Interactive computational graph
- Generated mathematical equation
- Updated topic values

---

# Example Configuration

```
graph.PlusAgent
A,B
C

graph.MinAgent
A,B
D

graph.MulAgent
C,D
E
```

Example input:

```
A = 5
B = 3
```

Computed values:

```
C = 8
D = 2
E = 16
```

---

# Configuration Validation

Before loading a configuration, the application validates its structure.

If an invalid configuration is uploaded:

- The graph is not deployed.
- A friendly error page is displayed.
- A valid configuration example is provided to help the user correct the file.

---

# Technologies

- Java
- HTTP
- HTML
- CSS
- SVG
- JavaScript
- Java Reflection
- Multi-threading
- Publish / Subscribe
- MVC Architecture

---

# Design Highlights

- Reflection-based dynamic agent creation.
- Thread-safe publish/subscribe communication.
- Multi-threaded message processing.
- Automatic graph generation from the loaded configuration.
- Interactive SVG visualization.
- Separation of concerns using MVC.
- Modular and extensible architecture.

---

# Future Extensions

The project architecture allows future additions with minimal code changes, such as:

- New computational agents.
- Additional graph operations.
- More advanced graph layouts.
- Persistent configuration storage.
- WebSocket-based real-time updates.