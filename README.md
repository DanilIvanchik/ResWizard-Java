#   ResWizard - Resume Hosting and Customizable Download Page
![Development Status](https://img.shields.io/badge/Development-In%20Progress-brightgreen)
![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen)
![Test Coverage](https://img.shields.io/badge/Test%20Coverage-62-brightgreen)

ResWizard is a Java-based web service designed to streamline the resume-sharing process. Its primary objective is to generate an intuitive and visually appealing web page that can be effortlessly shared through a custom link, allowing individuals to present their resumes and provide concise personal information to potential employers. With ResWizard, users can effortlessly distribute their resumes, while visitors to their unique web page can conveniently download the resume in their preferred language.

## Содержание
- [Technologies](##Technologies)
- [Usage](#Usage)
- [Configuration](#Configuration)
- [Development](#Development)
- [Testing](#Testing)
- [Contributing](#contributing)
- [Deploy and CI/CD](#Deploy-and-CI/CD)
- [Contributing to ResWizard](#Contributing-to-ResWizard)
- [To do](##to-do)
- [The project team](#The-project-team)

## Technologies

- **Java**: Version 17
- **Spring Boot**: Version 3.1.3
- **Thymeleaf**: Version 3.1.2.RELEASE
- **Spring Boot Configuration Processor**: Optional
- **Commons IO**: Version 2.13.0
- **Spring Boot Starter Mail**: Version 3.1.4
- **Spring Boot Starter Undertow**
- **Thymeleaf Extras SpringSecurity6**
- **ModelMapper**: Version 3.1.1
- **Spring Boot Starter Data JPA**
- **Spring Boot Starter Security**
- **Spring Boot Starter Validation**
- **Spring Boot Starter Web**
- **Spring Boot Devtools**: Runtime, Optional
- **PostgreSQL**: Runtime
- **Project Lombok**: Optional
- **Spring Boot Starter Test**: Test
- **Spring Security Test**: Test
- **Spring Boot Maven Plugin**

## Usage

### Installation

To use our project, you can follow these steps to download it from GitHub and set it up in your Java environment.

1. **Clone the Repository:**

   Use `git` to clone the project repository from GitHub:

   ```shell
   git clone https://github.com/DanilIvanchik/ResWizard.git 

2. **Navigate to the Project Directory:**

   Change your working directory to the project folder:

   ```shell
   cd ResWizard

3. **Build the Project:**

   Use Maven to build the project. Make sure you have Maven installed.

   ```shell
   mvn clean install

3. **Run the Application:**

   Run the application using the Spring Boot Maven plugin:

   ```shell
   mvn spring-boot:run

4. **Access the Application: **

   Once the application is running, open your web browser and go to http://localhost:8080/hello to access the project.

## Configuration

Before running the application, you need to configure the `application.properties` file. Please fill in the following properties with your specific values:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your-database-name
spring.datasource.username=your-database-username
spring.datasource.password=your-database-password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.show_sql=false

spring.mvc.hiddenmethod.filter.enabled=true
upload.file.path=/your/upload/file/path
upload.avatar.path=/your/upload/avatar/path
spring.servlet.multipart.max-file-size=1MB
spring.servlet.multipart.max-request-size=1MB

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
#Use app password . Find more information at the link: https://www.youtube.com/watch?v=nuD6qNAurVM
spring.mail.password=your-email-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Uncomment the following line to enable debugging if needed
# debug=true
```


## Development

### Requirements:
To install and run the project you will need Java v17+ and Maven v3.8.1+. Make sure they are installed and configured on your system.

### Installing Dependencies:
This project utilizes various dependencies managed by Apache Maven. To install these dependencies, execute the following command within the project directory:

```bash
$ mvn install
```

###Creating a Production Build:
To build a production-ready version of your application, package it into a JAR file. Run the following Maven command:

```bash
$ mvn package
```


## Testing

Our project is covered with Jest unit tests. To run them, run the command:
```bash
npm run test
```
## Deploy and CI/CD

### Deploying an Application

1. **Choose a Hosting Platform:** Start by selecting a hosting platform for your application. Popular options include GitHub Pages, Heroku, AWS, Google Cloud, and more.

2. **Prepare the Environment:** Prepare the environment for your application. This involves setting up servers, configuring databases, and any other required components.

3. **Configure the Application:** Adjust your application to run on the chosen hosting platform. This may involve installing dependencies, configuring databases, and other necessary settings.

4. **Deploy Your Code:** Upload your application code to the selected hosting platform. You can do this using Git, FTP, or other relevant methods based on the platform's requirements.

5. **Testing:** Ensure that your application is successfully deployed and running as expected. Conduct testing to verify that all functions work correctly.

## Contributing to ResWizard

Thank you for your interest in contributing to our project! We welcome contributions from the community. This document outlines the process for submitting bug reports, suggesting enhancements, and making code contributions.

### Bug Reports

If you encounter a bug or issue with our project, please follow these steps to report it:

1. Check the [Issues](https://github.com/yourusername/yourprojectname/issues) section to see if the issue has already been reported.

2. If the issue is not already reported, create a new issue and provide the following information:
   - A clear and concise title describing the issue.
   - A detailed description of the problem, including how to reproduce it.
   - Information about your environment (e.g., operating system, browser, version of the project).

### Feature Requests

We welcome suggestions for new features or enhancements. To suggest a new feature:

1. Check the [Issues](https://github.com/DanilIvanchik/ResWizard/issues) section to see if it's already been requested.

2. If not, create a new issue with a clear and descriptive title. Explain the proposed feature and why it would be valuable.

### Making Code Contributions

We encourage you to contribute to our project by submitting code changes. Here's how you can do that:

1. Fork the project repository to your GitHub account.

2. Create a new branch for your changes:
   ```bash
   git checkout -b feature/your-feature-name

## Why was this project developed?
This project was designed to simplify the process of distributing resumes by offering a user-friendly platform. ResWizard allows users to share a single link, eliminating the need to send resumes to employers in different languages. Employers can then select their preferred language and access the relevant CV, first receiving a summary of the candidate from the resulting page.

## To do
- [x] Implement registration and authentication.
- [x] Implement the basic logic of the application, namely the creation of a shareable resume repository page with the possibility of customization (happy flow).
- [x] Implementation of the logic for activating an account via email.
- [x] Implementation of the logic for resetting and reassigning a password via email.
- [x] Exception handling.
- [ ] Test coverage.

## The project team

- [Ivanchyk Daniil] — Back-end & Front-end developer.

