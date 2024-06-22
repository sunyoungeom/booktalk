CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profileImgPath VARCHAR(255),
    nickname VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    signUpType VARCHAR(50),
    signUpDate datetime default now(),
    userRole VARCHAR(50) DEFAULT 'USER'
);