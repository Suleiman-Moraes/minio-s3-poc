# MinIO S3 PoC

## 🚀 About the Project

This project is a **Proof of Concept (PoC)** for integrating **MinIO** with **AWS S3 SDK** using **Spring Boot**. It provides a complete CRUD for file manipulation, allowing uploading, downloading, listing and deleting files directly in an S3 bucket.

---

## 📌 Features
- 📂 **File Upload**
- 🔍 **File List**
- 📥 **File Download**
- 🗑️ **File Removal**
- 📄 **Swagger UI for API Documentation**

---

## 🏗️ Technologies Used

### **Backend**
- ☕ **Java 21**
- 🖥️ **Spring Boot 3.4.4**
- ☁️ **AWS S3 SDK 2.31.7**
- 🔄 **Docker Compose**
- 📚 **Swagger (OpenAPI)**

---

## 📦 Project Setup

### 🔧 **Prerequisites**
- [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/)
- Java 21 (JDK 21)
- Maven

### 🛠️ **Setup Steps**

1️⃣ **Clone the repository**
```sh
git clone https://github.com/suleiman-moraes/minio-s3-poc.git
cd minio-s3-poc
```

2️⃣ **Run the project with Docker Compose**
```sh
docker-compose -f docker-compose-full.yaml up -d
```

3️⃣ **Compile and run the project**
```sh
mvn clean install
mvn spring-boot:run
```

4️⃣ **Access Swagger to test endpoints**
```
http://localhost:8080/swagger-ui.html
```

---

## 📌 **API Endpoints**

### 📂 **File Upload**
```http
POST /files
```
**Request:** `multipart/form-data`
```json
{
"file": "(file binary)"
}
```
📥 **Response:** `200 OK`
```json
"File uploaded successfully!"
```

---
### 📥 **File Download**
```http
GET /files/{key}
```
🔑 **Parameter:** `key` → Name of the file stored in S3. 📥 **Response:** `application/octet-stream`

---
### 🔍 **List Files**
```http
GET /files
```
📥 **Response:** `200 OK`
```json
["file1.jpg", "file2.png"]
```

---
### 🗑️ **Delete File**
```http
DELETE /files/{key}
```
🔑 **Parameter:** `key` → Name of the file to be removed.
📥 **Response:** `200 OK`
```json
"File removed successfully!"
```

---

## 📜 **License**

This project is distributed under the **MIT** license. Feel free to use and modify!

---

## ✨ **Credits**

👨‍💻 **Author:** [Suleiman Moraes](https://github.com/suleiman-moraes)

🚀 **Made with Spring Boot & MinIO** ❤️