-- Создание таблицы для пользователей
CREATE TABLE "users" (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    username VARCHAR(255) UNIQUE NOT NULL, -- Уникальное поле для имени пользователя
    email VARCHAR(255) UNIQUE NOT NULL,    -- Уникальное поле для email
    password VARCHAR(255) NOT NULL,        -- Пароль пользователя
    first_name VARCHAR(255),               -- Имя пользователя
    last_name VARCHAR(255),                -- Фамилия пользователя
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата регистрации
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Дата последнего обновления
);

-- Создание таблицы для задач
CREATE TABLE "Task" (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    title VARCHAR(255) NOT NULL,          -- Название задачи
    description TEXT,                     -- Описание задачи
    ready BOOLEAN DEFAULT FALSE,          -- Статус выполнения задачи (по умолчанию false)
    user_id BIGINT,                       -- Внешний ключ на таблицу пользователей
    FOREIGN KEY (user_id) REFERENCES "User" (id) -- Связь с пользователем
);

-- Добавление индексов для улучшения производительности
CREATE INDEX idx_user_email ON "User" (email);
CREATE INDEX idx_user_username ON "User" (username);

