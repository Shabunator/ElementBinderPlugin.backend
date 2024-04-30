# Используем базовый образ с Java
FROM openjdk:21

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY build/libs/element.binder.plugin.backend.jar .

# Определяем команду, которая будет выполняться при запуске контейнера
CMD ["java", "-jar", "element.binder.plugin.backend.jar"]