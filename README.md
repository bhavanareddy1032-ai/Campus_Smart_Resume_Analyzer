# Campus Smart Resume Analyzer

A simple web application for the OOPS Through Java Lab.

## Requirements
- JDK 17 or newer
- VS Code / IntelliJ / Eclipse
- Any modern browser

No external Java libraries are required.

## How to run

Open a terminal in the project folder.

### Windows
```text
javac -d out src\campusresume\*.java
java -cp out campusresume.ApiServer
```

Then open:
http://localhost:8080

### Linux/macOS
```text
javac -d out src/campusresume/*.java
java -cp out campusresume.ApiServer
```

## Features
- Student details
- Target job selection
- Resume text input
- .txt file loading
- Skill matching
- Missing skill detection
- Resume score
- Improvement suggestions
- Analysis history

## OOP concepts demonstrated
- Classes and objects
- Constructors
- Encapsulation
- Abstraction
- Inheritance
- Method overriding
- Runtime polymorphism
- ArrayList collections
- Exception handling
- Multithreading
- File handling

## Project flow
User -> HTML/CSS/JavaScript -> Java HTTP Server -> OOP Analyzer -> Result -> History

## Important note
This is a student mini-project prototype. It uses keyword matching rather than an external AI model, which keeps the project easy to run and explain in a lab.
