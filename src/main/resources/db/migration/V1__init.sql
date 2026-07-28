CREATE TABLE `users` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `created_at` DATETIME(6) NOT NULL,
                         `updated_at` DATETIME(6) DEFAULT NULL,
                         `birth_date` DATE DEFAULT NULL,
                         `country_code` VARCHAR(2) DEFAULT NULL,
                         `email` VARCHAR(255) NOT NULL,
                         `name` VARCHAR(255) NOT NULL,
                         `password_hash` VARCHAR(255) DEFAULT NULL,
                         `phone_number` VARCHAR(20) DEFAULT NULL,
                         `profile_image_url` VARCHAR(255) DEFAULT NULL,
                         `provider` ENUM('GOOGLE','LOCAL') NOT NULL,
                         `provider_id` VARCHAR(255) DEFAULT NULL,
                         `role` ENUM('ADMIN','USER') NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
