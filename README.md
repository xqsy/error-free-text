# ErrorFreeText

Сервис для автоматической проверки и исправления орфографических ошибок с помощью [Яндекс Спеллера](https://yandex.ru/dev/speller/).

Обработка отделена от HTTP-запроса: клиент создаёт задачу через REST API, а встроенный планировщик позднее обрабатывает её в фоновом режиме.

## Стек

- Java 21 и Spring Boot 3.5
- Gradle 8.14.5
- PostgreSQL, Spring Data JPA и Hibernate
- Docker и Docker Compose
- API: Яндекс Спеллер (`POST checkTexts`)
- JUnit 5 и Mockito

## Запуск

### Docker Compose

```shell
docker compose up --build
```

После запуска API доступен по адресу `http://localhost:8080`.

### Локальная разработка

```shell
docker compose up -d postgres
./gradlew bootRun
```

В Windows вместо `./gradlew` можно использовать `gradlew.bat`.

## API

### Создание задачи

```http
POST /tasks
Content-Type: application/json

{
  "text": "Превет, мир!", "language": "ru"
}
```

Успешный ответ имеет статус `201 Created`:

```json
{"id": "44bd78dc-d08c-41c6-b87d-fb82046bd470"}
```

Поддерживаются языки `ru` и `en`. После удаления окружающих пробелов для целей валидации текст должен содержать не менее трёх Unicode-символов и хотя бы одну букву.

### Получение задачи

```http
GET /tasks/44bd78dc-d08c-41c6-b87d-fb82046bd470
```

Пример ответа для завершённой задачи:

```json
{
  "status": "COMPLETED",
  "correctedText": "Привет, мир!"
}
```

Жизненный цикл: `NEW → PROCESSING → COMPLETED / FAILED`.

## Конфигурация

Основные параметры находятся в `src/main/resources/application.yml`:

| Параметр | Значение по умолчанию |
|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/errorfreetext` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `postgres` |
| `yandex.speller.endpoint` | `https://speller.yandex.net/services/spellservice.json/checkTexts` |
| `yandex.speller.connect-timeout` | `3s` |
| `yandex.speller.read-timeout` | `10s` |
| `task.processing.polling-interval` | `1s` |

Параметры можно переопределить переменными окружения `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `YANDEX_SPELLER_ENDPOINT`, `YANDEX_SPELLER_CONNECT_TIMEOUT`, `YANDEX_SPELLER_READ_TIMEOUT` и `TASK_PROCESSING_POLLING_INTERVAL`. Порт Docker-контейнера задаётся через `APP_PORT`.

---

## Ограничения

- Ограничения Яндекс Спеллера: до 10 000 запросов и до 10 миллионов символов в сутки.
