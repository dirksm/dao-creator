# DAO Creator
DAO Creator is a Spring Boot-based application designed to simplify the generation of DAO-related code. This project leverages configuration properties defined in `application.yml` files to generate code tailored for specific databases. Each database has its own `application.yml` file, and the appropriate profile is set to ensure accurate code generation.

## Table of Contents

- [Features](#features)
- [Folder Structure](#folder-structure)
- [Key Directories Generated](#key-directories-generated)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Code Generation**: Automatically generate DAO-related code based on database-specific configurations.
- **Profile-Based Configuration**: Use Spring profiles to load database-specific `application.yml` files.
- **Extensible Design**: Modular architecture for adding support for additional databases or features.

## Folder Structure

The main codebase is located under the `src/main/java` directory. Below is an overview of the folder structure:

```
src/main/java
├── com
│   └── maddog
│       ├── dao
│       │   └── creator
│       │   │       ├── config
│       │   │       │   ├── DatabaseConfiguration
│       │   │       │   └── GeneratorConfig
│       │   │       ├── generator
│       │   │       │   ├── AbstractGenerator
│       │   │       │   ├── CommandLineAppStartupRunner
│       │   │       │   ├── DaoGenerator
│       │   │       │   ├── MainGenerator
│       │   │       │   └── ModelGenerator
│       │   │       ├── model
│       │   │       │   ├── DatabaseValueModel
│       │   │       │   └── TableValueModel
src/main/resources
├── application.yml
├── application-mssql.yml
├── application-oracle.yml
├── logback-spring.xml
```
## Key Directories Generated
The application creates the following structure for the DAO entities and Models needed for database operations.  The package structure is specifed in the application.yml.  It is a combination of the following:
* application.generator.basepackage
* application.generator.classPrefix

This is how the generated folder structure looks (based on the provided attributes above):
```
├── com
│   └── maddog
│       ├── dao
│       │   ├── creator
│       │   │   ├── bills
│       │   │   │   ├── dao
│       │   │   │   │   ├── creator
│       │   │   │   │   │   └── TableNameCreator
│       │   │   │   │   ├── extractor
│       │   │   │   │   │   └── TableNameExtractor
│       │   │   │   │   ├── impl
│       │   │   │   │   │   └── TableNameDaoImpl
│       │   │   │   │   ├── updater
│       │   │   │   │   │   └── TableNameUpdater
│       │   │   │   │   └── TableNameDao
│       │   │   │   └── model
│       │   │   │       └── TableName
```

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/dirksm/dao-creator.git
    cd dao-creator
    ```

2. Create a database specific application.yml file for your typical database or use one of the ones provided. Make sure you create values for the environment variables present.

        Current variables:
        - mssqlUser: User account for SQL Server (e.g. SA)
        - mssqlPassword: User password for SQL Server
        - mysqlUser: User account for MySQL Server
        - mysqlPassword: User password for MySQL Server
        - oracleUser: User account for Oracle Server
        - oraclePassword: User password for Oracle

        Each account corresponds to one of the application.yml files and may change based on your database preference.

        

3. Build the project using Gradle:
    ```bash
    ./gradlew build
    ```

4. Run the application with the desired profile:
    ```bash
    ./gradlew bootRun --args='--spring.profiles.active=<profile-name>'
    ```

Replace `<profile-name>` with the appropriate profile for the target database (e.g., `oracle`, `mssql`, etc.).
    ```

## Usage

When the bootRun job terminates, the generated DAO source code will be generated in the root directory of this project in the folder structure shown above.

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch:
    ```bash
    git checkout -b feature-name
    ```
3. Commit your changes:
    ```bash
    git commit -m "Add feature-name"
    ```
4. Push to your branch:
    ```bash
    git push origin feature-name
    ```
5. Open a pull request.

## License

This project is licensed under the [MIT License](LICENSE.md).
