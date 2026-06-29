# Advanced Programming – Assignment 6
## HTTP Server for Computational Graphs

### Author
Saar Golan

---

# Project Description

This project implements a lightweight HTTP server in Java that allows users to deploy and interact with computational graphs through a web interface.

The server supports:

- Uploading graph configuration files.
- Dynamically creating computational graphs.
- Publishing values to graph topics.
- Displaying the graph visually.
- Displaying the latest value of every topic.

The project is built on top of the infrastructure developed in previous assignments, including the publish/subscribe system, graph generation, and HTTP request parsing.

---

# Project Structure

├── src/
│   ├── configs/
│   │   ├── Config.java
│   │   ├── GenericConfig.java
│   │   ├── Graph.java
│   │   └── Node.java
│   │
│   ├── graph/
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
│   ├── server/
│   │   ├── HTTPServer.java
│   │   ├── MyHTTPServer.java
│   │   └── RequestParser.java
│   │
│   ├── servlets/
│   │   ├── ConfLoader.java
│   │   ├── HtmlLoader.java
│   │   ├── Servlet.java
│   │   └── TopicDisplayer.java
│   │
│   ├── views/
│   │   └── HtmlGraphWriter.java
│   │
│   └── Main.java
│
└── README.md

---

# Design

The project is divided into several independent components.

## HTTP Server

`MyHTTPServer` listens for incoming HTTP connections.

For every request it:

1. Parses the HTTP request.
2. Finds the matching servlet.
3. Invokes the servlet.
4. Returns an HTTP response.

---

## Servlets

### HtmlLoader

Serves static HTML files from the `html_files` directory.

---

### ConfLoader

Responsible for:

- Receiving uploaded configuration files.
- Saving the configuration.
- Creating the computational graph.
- Returning an HTML visualization of the graph.

---

### TopicDisplayer

Responsible for:

- Receiving topic/value pairs.
- Publishing messages to the requested topic.
- Displaying a table containing the latest value of every topic.

---

## Computational Graph

The graph is generated from the TopicManager after loading a configuration.

Topics are displayed as rectangles.

Agents are displayed as circles.

Directed edges represent publish/subscribe relationships.

---

# Supported Agents

The project currently includes:

- PlusAgent
- MinusAgent
- MulAgent
- IncAgent

Additional agents can be added by implementing the `Agent` interface.

---

# Running the Project

1. Run `Main.java`.
2. Open:

```
http://localhost:8080/app/index.html
```

3. Upload a configuration file.
4. Publish values using the web interface.
5. Observe:

- Graph visualization
- Updated topic values

---

# Example Configuration

```
graph.PlusAgent
A,B
C

graph.MinusAgent
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

Result:

```
C = 8
D = 2
E = 16
```

---

# Technologies

- Java
- HTTP
- HTML
- SVG
- Java Reflection
- Publish / Subscribe architecture

---

# Notes

The implementation reuses components developed throughout previous assignments:

- Publish/Subscribe system
- Reflection-based configuration loader
- Computational graph
- HTTP request parser
- Threaded HTTP server

These components were integrated to create an interactive web application for visualizing and interacting with computational graphs.