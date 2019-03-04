# API-reference для приложения-календаря

TODO: request examples

Работа с API будет проходить с помощью HTTP-запросов <br />
Используемые методы: GET, POST, PUT, DELETE <br />
Content-Type: application/json и application/x-www-form-urlencoded <br />
Для генерации документации к API используется [Swagger](https://swagger.io/)

----------------------------------------------------------------------------------------------------------------------

### Аутентификация
- OAuth 2.0, для предоставления доступа к API необходимо авторизовать приложение и получить сервисный ключ. <br />
Подробнее про OAuth2: [docs](https://oauth.net/2/), [habr](https://habr.com/ru/company/mailru/blog/115163/)
- Альтернатива - использовать вечный API-token на приложение (клиент)
- Для авторизации пользователей в клиенте можно использовать OAuth Google, [habr](https://habr.com/ru/post/325518/), [docs](https://developers.google.com/identity/protocols/OAuth2), [example](https://developers.google.com/identity/sign-in/android/backend-auth). <br />
Что будет для iOS - дискас.

**User** - пользователь

| Name  | Type                    | Description                                                               |
|-------|-------------------------|---------------------------------------------------------------------------|
| id    | uint, uuid, required    | Уникальный идентификатор пользователя                                     |
| regid | string, optional        | RegId из Google Cloud Messaging, используется для push-уведомлений        |
| name  | string, optional        | Имя пользователя                                                          |
| phone | string, phone, optional | Номер телефона пользователя, используется при добавлении участников задач |
| email | string, email, optional | Почта пользователя, используется при добавлении участников задач          |

### Напоминания
- Используют Google Cloud Messaging [GCM]. Для общего понимания можно почитать:
* https://javapapers.com/android/google-cloud-messaging-gcm-for-android-and-push-notifications
* https://code.tutsplus.com/ru/tutorials/how-to-get-started-with-push-notifications-on-android--cms-25870

API GCM на данный момент могло обновиться, поэтому так же стоит посетить:
* https://developers.google.com/cloud-messaging/concept-options?hl=ru

### Календарь и список задач

Для лучшего понимания архитектуры и внесения обоснованных изменений рекомендуется ознакомиться со
[спецификацией iCalendar](https://www.kanzaki.com/docs/ical/).

Для периодических событий используется cron-формат (арифметические операции не поддерживаются).


**Task** - задача

| Name        | Type                | Description                              |
|-------------|---------------------|------------------------------------------|
| id          | uint, required      | Уникальный ID задачи                     |
| name        | string, optional    | Название задачи                          |
| details     | string, optional    | Описание задачи                          |
| status      | int, required       | Текущий статус задачи, enum              |
| event_id    | uint, optional      | ID события, за которым закреплена задача |
| parent_id   | uint, optional      | ID родительской задачи                   |
| deadline_at | timestamp, required | Крайний срок выполнения задачи           |
| created_at  | timestamp, optional | Дата-время создания задачи               |
| updated_at  | timestamp, optional | Дата-время обновления задачи             |


**Event** - событие

| Name       | Type                | Description                   |
|------------|---------------------|-------------------------------|
| id         | uint, required      | Уникальный ID события         |
| owner_id   | uint, required      | ID создателя события          |
| name       | string, optional    | Название события              |
| details    | string, optional    | Описание события              |
| status     | int, required       | Текущий статус события, enum  |
| location   | string, optional    | Место события                 |
| created_at | timestamp, optional | Дата-время создания события   |
| updated_at | timestamp, optional | Дата-время обновления события |


**EventPattern** - описывает RRULE, RDATE, EXRULE и EXDATE правила для события

| Name        | Type                | Description                      |
|-------------|---------------------|----------------------------------|
| id          | uint, required      | Уникальный ID правила            |
| event_id    | uint, required      | ID события                       |
| type        | char, required      | Тип правила: RRULE[0], EXRULE[1] |
| year        | int, optional       | Год, cron-формат                 |
| month       | int, optional       | Месяц, cron-формат               |
| day         | int, optional       | День, cron-формат                |
| hour        | int, optional       | Час, cron-формат                 |
| minute      | int, optional       | Минута, cron-формат              |
| started_at  | timestamp, optional | Час, cron-формат                 |
| finished_at | timestamp, optional | Минута, cron-формат              |
| created_at  | timestamp, optional | Дата-время создания правила      |
| updated_at  | timestamp, optional | Дата-время обновления правила    |


**Tag** - теги событий (задач?)

**EventToTag** - m2m связь событий и тегов

**EventToUser** - m2m связь событий и их участников

**TaskToUser** - m2m связь для ответственных за задачу

**TagToUser** - m2m связь тегов и пользователей, подписка на теги


### Взаимодействие (с другими пользователями)
- WIP

### API-методы

`POST /api/auth`
> Авторизует пользователя с помощью полученного от Google Sign-In токена (альтернативные сервисы на рассмотрении).
> Ключ будет проверен на валидность.
> В случае успеха вернет OAuth-ключ для доступа к API.

**Параметры**

| Field   | Type             | Description                                 |
|---------|------------------|---------------------------------------------|
| token   | string, required | ID-токен для аутентификации                 |
| service | string, optional | Сервис аутентификации. По умолчанию: google |

**HTTP 200**
```
{
   "access_token": "SlAV32hkKG",
   "token_type": "bearer",
   "expires_in": 86400,
   "refresh_token": "8xLOxBtZp8"
}
```

----------------------------------------------------------------------------------------------------------------------


`GET|POST|PUT|DELETE /api/events`
> ...

**Параметры**

| Field   | Type             | Description                                 |
|---------|------------------|---------------------------------------------|
| ...     | string, required | ...                                         |

**HTTP 200**
```
{
   "...": "..."
}
```

----------------------------------------------------------------------------------------------------------------------


`GET|POST|PUT|DELETE /api/events/pattern`
> ...

**Параметры**

| Field   | Type             | Description                                 |
|---------|------------------|---------------------------------------------|
| ...     | string, required | ...                                         |

**HTTP 200**
```
{
   "...": "..."
}
```

----------------------------------------------------------------------------------------------------------------------


`GET|POST|PUT|DELETE /api/events/check`
> ...

**Параметры**

| Field   | Type             | Description                                 |
|---------|------------------|---------------------------------------------|
| ...     | string, required | ...                                         |

**HTTP 200**
```
{
   "...": "..."
}
```

----------------------------------------------------------------------------------------------------------------------


`GET|POST|PUT|DELETE /api/tasks`
> ...

**Параметры**

| Field   | Type             | Description                                 |
|---------|------------------|---------------------------------------------|
| ...     | string, required | ...                                         |

**HTTP 200**
```
{
   "...": "..."
}
```

----------------------------------------------------------------------------------------------------------------------


`POST /api/tag/subscribe`
> ...

**Параметры**

| Field   | Type             | Description                                 |
|---------|------------------|---------------------------------------------|
| ...     | string, required | ...                                         |

**HTTP 200**
```
{
   "...": "..."
}
```

----------------------------------------------------------------------------------------------------------------------


`POST /api/tag/unsubscribe`
> ...

**Параметры**

| Field   | Type             | Description                                 |
|---------|------------------|---------------------------------------------|
| ...     | string, required | ...                                         |

**HTTP 200**
```
{
   "...": "..."
}
```