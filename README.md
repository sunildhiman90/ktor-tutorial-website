THis is an example of using ktor with exposed using mysql as data source

1. Create database `blogdb` from mysql command prompt using following command: `CREATE DATABASE blogdb;`
2. Create user `blogdb_user` with some password using following command: `CREATE USER  blogdb_user'@'localhost' identified by 'yourpassword';`
3. Grant all privileges on `blogdb` to user `blogdb_user` using following command: `GRANT ALL ON blogdb.* TO 'blogdb_user'@'localhost';`
4. Finally change your username and password in `application.conf` file