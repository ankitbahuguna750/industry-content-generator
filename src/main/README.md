# AI LinkedIn Content Generator

An AI-powered web application that generates LinkedIn post ideas based on an industry and writing tone. The application uses industry news as context and Google's Gemini API to generate content.

Built with **Java 21, Spring Boot, MySQL, and HTML/CSS/JavaScript**, this project also supports user authentication, email notifications, scheduled content generation, and LinkedIn OAuth integration.

## Features

- **AI Content Generation:** Generate LinkedIn content based on a selected industry.
- **Multiple Writing Tones:** Choose Professional, Friendly, or Educational.
- **Industry News:** Use industry news as context for generated posts.
- **User Authentication:** Register and log in using an email and password.
- **Secure Password Storage:** Store user passwords using BCrypt hashing.
- **LinkedIn OAuth:** Connect a LinkedIn account through the OAuth flow.
- **Email Notifications:** Send generated posts using Gmail SMTP.
- **Daily Scheduling:** Generate and email content daily at 9:00 AM India time. The current scheduler is configured for the technology industry and a recipient email.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend programming |
| Spring Boot | Application framework |
| Spring Web | REST APIs |
| Spring Data JPA / Hibernate | Database operations |
| MySQL | Database |
| Google Gemini API | AI content generation |
| Gmail SMTP | Email delivery |
| HTML, CSS, JavaScript | Frontend |
| Maven | Build and dependency management |

## Prerequisites

Before running the application, prepare:

- JDK 21
- MySQL Server
- Google Gemini API key
- Gmail SMTP credentials (an app password may be required)
- LinkedIn Developer Application credentials, if using LinkedIn integration
- Git (optional, for version control)

## Getting Started


### 1. Create the Database

Open MySQL Workbench or your MySQL client and execute:

```sql
CREATE DATABASE linkedin_ai;
```

The application is configured to connect to MySQL on `localhost:3306` with the username `root`.

Set the `DB_PASSWORD` environment variable to the password of your configured database user.

### 2. Configure Environment Variables

Set the following environment variables before running the application.

| Variable | Purpose |
|---|---|
| `GEMINI_API_KEY` | Google Gemini API key |
| `MAIL_USERNAME` | Gmail address used to send emails |
| `MAIL_PASSWORD` | Gmail SMTP password or app password |
| `DB_PASSWORD` | MySQL database password |
| `LINKEDIN_CLIENT_ID` | LinkedIn application client ID |
| `LINKEDIN_CLIENT_SECRET` | LinkedIn application client secret |

**Security note:** Never commit real API keys, passwords, or client secrets to GitHub. Keep secrets outside source control and use environment variables or another secure configuration method.

### 3. Configure LinkedIn OAuth (Optional)

The configured callback URL is:

```text
http://localhost:8081/oauth/linkedin/callback
```

If you enable LinkedIn integration, make sure this URL matches the redirect URL configured in your LinkedIn Developer Application.

Valid LinkedIn credentials and the required permissions are necessary for OAuth and any supported publishing operations.

### 4. Run the Application

Open a terminal in the project directory.

**Windows Command Prompt:**

```bat
mvnw.cmd spring-boot:run
```

**macOS / Linux:**

```bash
./mvnw spring-boot:run
```

Alternatively, build the application JAR:

```bash
mvnw.cmd clean package
```

Then run it:

```bash
java -jar target/industry-content-generator-0.0.1-SNAPSHOT.jar
```

The application is configured to run on port `8081`.

> If the Maven Wrapper is not present in your repository, install Maven and use `mvn spring-boot:run` or `mvn clean package` instead.

## Access the Application

Once the application starts, open these URLs in your browser:

| Page | URL |
|---|---|
| Home | http://localhost:8081/ |
| Login | http://localhost:8081/login.html |
| Registration | http://localhost:8081/register.html |

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/content` | Generate content for the supplied industry and tone |
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Log in |
| POST | `/api/auth/logout` | Log out |
| GET | `/api/auth/current-user` | Get the current session user |
| GET | `/api/email/test?email=you@example.com` | Send a test email |
| GET | `/api/email/posts?email=you@example.com&industry=technology` | Generate content and email it |

### Example: Generate LinkedIn Content

Request:

```bash
curl -X POST http://localhost:8081/api/content ^
  -H "Content-Type: application/json" ^
  -d "{\"industry\":\"Technology\",\"tone\":\"Professional\"}"
```

This example uses Windows Command Prompt syntax. On macOS or Linux, replace the `^` line continuations with `\`.

The request asks the application to generate professional LinkedIn content for the Technology industry. The exact response depends on the implementation and the external AI service.

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/example/industry_content_generator/
│   │       ├── controller/   # Authentication, content, and LinkedIn endpoints
│   │       ├── dto/          # Request DTOs
│   │       ├── entity/       # JPA entities
│   │       ├── repository/   # Database repositories
│   │       └── service/      # Gemini, news, email, LinkedIn, and scheduler logic
│   └── resources/
│       ├── static/           # HTML frontend pages
│       └── application.properties
└── test/                     # Application tests
```

## Scheduled Content Generation

The daily scheduler is configured in `DailyContentScheduler.java` to run at **9:00 AM in the `Asia/Kolkata` time zone**.

Before using the scheduler, review the configured industry and recipient email in that class. Ensure the email settings and required API credentials are valid.

## Important Notes

- AI-generated content should be reviewed and edited before publishing.
- External API availability, rate limits, and provider quotas can affect content generation.
- LinkedIn OAuth and publishing depend on valid application credentials and the appropriate permissions.
- The project currently uses `spring.jpa.hibernate.ddl-auto=update`. Consider a database migration tool such as Flyway or Liquibase before production deployment.
- Do not expose API keys, database passwords, email credentials, or LinkedIn client secrets in source control.
- The project is intended as a learning and portfolio project; additional security, testing, and deployment work may be needed before production use.

## Future Improvements

- Add industry and topic preferences.
- Improve error handling and input validation.
- Add tests for controllers and services.
- Allow users to manage their scheduled content preferences.
- Add deployment and monitoring support.

## License

No license file is currently included. Add a license if you intend to specify how others may use, modify, or distribute this project.
