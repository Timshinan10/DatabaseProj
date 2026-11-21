# DatabaseProject

CSCI 4055 group project. This Java Swing application loads departments and projects from a MySQL database using the COMPANY schema, then searches for employees based on the departments and projects the user selects. Users can also select records to exclude.

The project started from Dr. Smith's template: https://github.com/proflon/CSCI4055-2025

**Group:** Nobel Timshina, Unika Bista, Ranjan Lamichhane

## Tools Used

- Java JDK
- Java Swing
- JDBC
- MySQL
- MySQL Connector/J 9.1.0
- XAMPP for Windows setup
- Homebrew and MySQL for macOS setup

## Database Requirements

- MySQL must run on `localhost:3306`.
- The database name must be `company`.
- The application uses the MySQL user `root`.
- The default connection assumes a blank password.
- The connection settings are in `DatabaseConnection.java`.

If MySQL uses a password, change this line in `DatabaseConnection.java`:

```java
private static final String DB_PASS = "your_mysql_password";
```

## Setup on Windows

### 1. Install the required software

Install:

- Java JDK 8 or newer
- XAMPP with Apache and MySQL
- MySQL Workbench, optionally

Open Command Prompt or PowerShell and verify Java:

```bat
java -version
javac -version
```

If `javac` is not recognized, install a JDK and add its `bin` directory to the `Path` environment variable.

### 2. Start MySQL

Open the XAMPP Control Panel and start **MySQL**. It should use port `3306`.

Open MySQL using XAMPP's full executable path:

```bat
"C:\xampp\mysql\bin\mysql.exe" -u root
```

If MySQL is in `Path`, this shorter command also works:

```bat
mysql -u root
```

### 3. Create and initialize the COMPANY database

In MySQL:

```sql
CREATE DATABASE company;
USE company;
SOURCE C:/path/to/your/project/buildDB.sql;
exit;
```

Replace `C:/path/to/your/project/buildDB.sql` with the actual absolute path to your configuration file.

The SQL file can also be run directly from Command Prompt:

```bat
"C:\xampp\mysql\bin\mysql.exe" -u root -e "CREATE DATABASE IF NOT EXISTS company"
"C:\xampp\mysql\bin\mysql.exe" -u root company < "C:\(\path\to\your/\)project\buildDB.sql"
```

Verify the database tables are loaded:

```bat
"C:\xampp\mysql\bin\mysql.exe" -u root -e "USE company; SHOW TABLES;"
```

### 4. Compile and run

Open Command Prompt in the project root folder:

```bat
javac -cp "mysql-connector-j-9.1.0.jar;." *.java
java -cp "mysql-connector-j-9.1.0.jar;." Main
```

The same commands work in PowerShell.

## Setup on macOS

### 1. Install Java and MySQL

Install Homebrew if it is not already available:

```bash
/bin/bash -c "\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

Install Java and MySQL dependencies:

```bash
brew install openjdk mysql
```

Configure Java environment for the active shell:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="\(JAVA_HOME/bin:\)PATH"
```

Verify Java compiler status:

```bash
java -version
javac -version
```

To make your Java path configurations permanent, append these lines to your `~/.zshrc` file:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="\(JAVA_HOME/bin:\)PATH"
```

Reload the shell configuration parameters:

```bash
source ~/.zshrc
```

### 2. Start MySQL

```bash
brew services start mysql
mysqladmin ping
```

Open a local terminal session:

```bash
mysql -u root
```

If your local MySQL environment requires a password, update `DB_PASS` in `DatabaseConnection.java`.

### 3. Create and initialize the COMPANY database

Create the database target:

```bash
mysql -u root -e "CREATE DATABASE IF NOT EXISTS company"
```

Run the SQL script utilizing its local directory path:

```bash
mysql -u root company < "/path/to/your/project/buildDB.sql"
```

Verify the database tables are loaded:

```bash
mysql -u root -e "USE company; SHOW TABLES;"
```

### 4. Compile and run

Open Terminal inside your local repository workspace:

```bash
cd "/path/to/your/project"
javac -cp "mysql-connector-j-9.1.0.jar:." *.java
java -cp "mysql-connector-j-9.1.0.jar:." Main
```

## Using the Application

1. Enter `company` in the Database field.
2. Click **Fill**.
3. Select one or more departments or projects from the scrollable panels.
4. Select **Not** beside departments or projects to exclude those criteria parameters.
5. Click **Search** to extract your match records.
