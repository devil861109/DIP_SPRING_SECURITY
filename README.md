# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

This repository is for an example application built in **Spring Boot 3.5.9**

The application is a simple Spring Boot 3 project designed to help students learn how
to use the Spring Framework with Spring Security Framework. 
Step by step instructions and detailed explanations can be found in moodle.

As you work through the course, please feel free to fork this repository to your own GitHub repo. Most links contain links
to source code changes. If you encounter a problem you can compare your code to the lesson code.

## Table of Contents
- [Project Overview](#project-overview)
- [Technologies Used](#technologies-used)
- [Getting Your Development Environment Setup](#getting-your-development-environment-setup)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Testing the Application](#testing-the-application)
- [Project Structure](#project-structure)
- [Available Endpoints](#available-endpoints)
- [Configuration](#configuration)
- [Learning Resources](#learning-resources)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## Project Overview
This is a Spring Boot 3 application that demonstrates the implementation of Spring Security. The project includes:
- Basic Spring Security configuration
- RESTful API endpoints
- Authentication and authorization mechanisms
- Best practices for securing Spring Boot applications

## Technologies Used
- **Spring Boot**: 3.5.9
- **Spring Security**: Latest compatible version
- **Spring Web**: For RESTful web services
- **Spring DevTools**: For development convenience
- **Java**: 17
- **Maven**: 3.9.0+
- **JUnit**: For testing

## Getting Your Development Environment Setup
### Recommended Versions
| Recommended             | Reference                                                                                                                                                     | Notes                                                                                                  |
|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| Oracle Java 17 JDK      | [Download](https://www.oracle.com/java/technologies/downloads/#java17)                                                                                        | Java 17 or higher is required for Spring Boot 3                                                        |
| IntelliJ 2022 or Higher | [Download](https://www.jetbrains.com/idea/download/)                                                                                                          | Ultimate Edition recommended. Anyway, this runs in Community Edition                                   |
| Maven 3.9.0 or higher   | [Download](https://maven.apache.org/download.cgi)                                                                                                             | [Installation Instructions](https://maven.apache.org/install.html)                                     |
| Git 2.44 or higher      | [Download](https://git-scm.com/downloads)                                                                                                                     |                                                                                                        | 
| Git GUI Clients         | [Downloads](https://git-scm.com/downloads/guis)                                                                                                               | Not required. But can be helpful if new to Git. SourceTree is a good option for Mac and Windows users. |

### Verify Installation
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Git version
git --version
```

## Building the Project

### Clone the Repository
```bash
git clone <repository-url>
cd DIP_SPRING_SECURITY
```

### Build with Maven
```bash
# Clean and build the project
mvn clean install

# Build without running tests
mvn clean install -DskipTests
```

## Running the Application

### Using Maven
```bash
mvn spring-boot:run
```

### Using Java
```bash
# First build the project
mvn clean package

# Then run the JAR file
java -jar target/DIP_SPRING_SECURITY-0.0.1-SNAPSHOT.jar
```

### Using IDE
1. Open the project in IntelliJ IDEA
2. Navigate to `src/main/java/edu/unam/springsecurity/SpringSecurityApplication.java`
3. Right-click and select "Run 'SpringSecurityApplication'"

The application will start on **port 8090** by default.

## Testing the Application

### Run All Tests
```bash
mvn test
```

### Access the Application
Once running, you can access the application at:
- Base URL: `http://localhost:8090`
- Welcome Endpoint: `http://localhost:8090/auth/welcome`

Default Spring Security credentials (if configured):
- **Username**: user
- **Password**: Check console output for auto-generated password, or use custom credentials from application.properties

## Project Structure
```
DIP_SPRING_SECURITY/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/unam/springsecurity/
│   │   │       ├── SpringSecurityApplication.java    # Main application class
│   │   │       └── controller/
│   │   │           └── HelloWorldController.java     # REST controller
│   │   └── resources/
│   │       └── application.properties                # Application configuration
│   └── test/
│       └── java/
│           └── edu/unam/springsecurity/
│               └── SpringSecurityApplicationTests.java
├── pom.xml                                           # Maven configuration
├── README.md                                         # This file
├── HELP.md                                           # Additional help
└── LICENSE                                           # License information
```

## Available Endpoints

| Method | Endpoint         | Description                    | Authentication Required |
|--------|------------------|--------------------------------|-------------------------|
| GET    | /auth/welcome    | Returns welcome message        | Yes (by default)        |

## Configuration

### Application Properties
The application can be configured through `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8090

# Security Configuration (commented out by default)
#spring.security.user.name=jonathan
#spring.security.user.password=1234
```

To customize:
1. **Change Port**: Modify `server.port`
2. **Set Custom Credentials**: Uncomment and modify security properties
3. **Add Database**: Add datasource properties for production use

## Learning Resources

### Spring Security Documentation
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Boot Security](https://spring.io/guides/gs/securing-web/)
- [Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture)

### Tutorials
- [Spring Security Tutorial](https://www.baeldung.com/security-spring)
- [Authentication and Authorization](https://www.baeldung.com/spring-security-authentication-and-registration)

## Troubleshooting

### Common Issues

**Issue**: Application fails to start on port 8090
- **Solution**: Port might be in use. Change `server.port` in `application.properties` or stop the process using port 8090

**Issue**: Maven build fails
- **Solution**: Ensure Java 17 or higher is installed and JAVA_HOME is set correctly

**Issue**: Cannot access endpoints
- **Solution**: Check if Spring Security is requiring authentication. Check console for default password or configure custom credentials

**Issue**: 401 Unauthorized errors
- **Solution**: Provide valid credentials. Spring Security protects all endpoints by default

### Debugging
Enable debug logging in `application.properties`:
```properties
logging.level.org.springframework.security=DEBUG
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
See the [LICENSE](LICENSE) file for details.

## Contact
For questions about the course, please refer to the Moodle platform or contact your instructor.

---
**Note**: This is an educational project for the UNAM Diplomado program. The implementation focuses on learning Spring Security concepts and best practices.

