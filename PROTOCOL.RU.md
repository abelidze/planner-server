# API-reference для приложения-календаря

TODO: request examples<br />
TODO: work with timezones

Работа с API будет проходить с помощью HTTP-запросов <br />
Используемые методы: GET, POST, PATCH, DELETE <br />
Content-Type: application/json и application/x-www-form-urlencoded <br />
Для генерации документации к API используется [Swagger](http://planner.skillmasters.ga/docs)

----------------------------------------------------------------------------------------------------------------------

### Аутентификация
- Актуальный способ авторизации - Firebase Authentication. <br />
[Установка (Step 3+)](https://firebase.google.com/docs/android/setup) и [настройка UI](https://firebase.google.com/docs/auth/android/firebaseui).
- OAuth 2.0, для предоставления доступа к API необходимо авторизовать приложение и получить сервисный ключ. <br />
Подробнее про OAuth2: [docs](https://oauth.net/2/), [habr](https://habr.com/ru/company/mailru/blog/115163/)
- Альтернатива - использовать вечный API-токен на приложение (клиент)
- Для авторизации пользователей в клиенте можно использовать OAuth Google, [habr](https://habr.com/ru/post/325518/), [docs](https://developers.google.com/identity/protocols/OAuth2), [example](https://developers.google.com/identity/sign-in/android/backend-auth). <br />
Для iOS альтернативных способов авторизации на данный момент не предусмотрено.
- Получив API-токен, его необходимо добавлять в header каждого запроса к API: <br />
`Authorization: Bearer <access_token>`


**User** - пользователь

| Name  | Type                    | Description                                                               |
|-------|-------------------------|---------------------------------------------------------------------------|
| id    | uint, uuid, required    | Уникальный идентификатор пользователя                                     |
| regid | string, optional        | RegId из Google Cloud Messaging, используется для push-уведомлений        |
| name  | string, optional        | Имя пользователя                                                          |
| phone | string, phone, optional | Номер телефона пользователя, используется при добавлении участников задач |
| email | string, email, optional | Почта пользователя, используется при добавлении участников задач          |


### Напоминания

Используют Google Cloud Messaging [GCM]. Для общего понимания можно почитать:

* https://javapapers.com/android/google-cloud-messaging-gcm-for-android-and-push-notifications
* https://code.tutsplus.com/ru/tutorials/how-to-get-started-with-push-notifications-on-android--cms-25870

API GCM на данный момент могло обновиться, поэтому так же стоит посетить:

* https://developers.google.com/cloud-messaging/concept-options?hl=ru


### Календарь и список задач

Для лучшего понимания архитектуры и внесения обоснованных изменений рекомендуется ознакомиться со
[спецификацией iCalendar](https://www.kanzaki.com/docs/ical/).

Для периодических событий используются RRULE и EXRULE правила стандарта iCalendar
([RFC 2445](https://tools.ietf.org/html/rfc2445) / [RFC 5545](https://tools.ietf.org/html/rfc5545)).

![iCal specification](ICalendarSpecification.png "iCalendar")


**Task** - задача

| Name        | Type                | Description                              |
|-------------|---------------------|------------------------------------------|
| id          | uint, required      | Уникальный ID задачи                     |
| name        | string, optional    | Название задачи                          |
| details     | string, optional    | Описание задачи                          |
| status      | char, required      | Текущий статус задачи, enum              |
| event_id    | uint, optional      | ID события, за которым закреплена задача |
| parent_id   | uint, optional      | ID родительской задачи                   |
| deadline_at | timestamp, required | Крайний срок выполнения задачи           |
| created_at  | timestamp, optional | Временная отметка создания задачи        |
| updated_at  | timestamp, optional | Временная отметка обновления задачи      |


**Event** - событие

| Name       | Type                | Description                                                                                       |
|------------|---------------------|---------------------------------------------------------------------------------------------------|
| id         | uint, required      | Уникальный ID события                                                                             |
| owner_id   | uint, required      | ID создателя события                                                                              |
| name       | string, optional    | Название события                                                                                  |
| details    | string, optional    | Описание события                                                                                  |
| status     | char, required      | Текущий статус события, enum                                                                      |
| location   | string, optional    | Место события                                                                                     |
| started_at | timestamp, optional | Временная отметка начала события. Присутствует только при запросе события на временном промежутке |
| ended_at   | timestamp, optional | Временная отметка конца события. Присутствует только при запросе события на временном промежутке  |
| created_at | timestamp, optional | Временная отметка создания события                                                                |
| updated_at | timestamp, optional | Временная отметка обновления события                                                              |


**EventPattern** - описывает RRULE, RDATE, EXRULE и EXDATE правила для события

| Name        | Type                | Description                                                                                       |
|-------------|---------------------|---------------------------------------------------------------------------------------------------|
| id          | uint, required      | Уникальный ID правила                                                                             |
| event_id    | uint, required      | ID события                                                                                        |
| type        | char, required      | Deprecated. Тип правила: RRULE[0], EXRULE[1]                                                      |
| rrule       | string, optional    | RRULE-строка, описывающая правило повторения события                                              |
| exrule      | string, optional    | Не реализовано. EXRULE-строка, описывающая исключения для повторяющихся событий                   |
| duration    | timestamp, optional | Продолжительность события в мс. В будущем - если NULL, то равна времени, оставшемуся до конца дня |
| started_at  | timestamp, optional | Временная отметка начала события / правила                                                        |
| ended_at    | timestamp, optional | Временная отметка конца события / правила                                                         |
| created_at  | timestamp, optional | Временная отметка создания правила                                                                |
| updated_at  | timestamp, optional | Временная отметка обновления правила                                                              |


**Tag** - теги событий (задач?)

| Name       | Type                | Description                              |
|------------|---------------------|------------------------------------------|
| id         | uint, required      | Уникальный ID тега                       |
| name       | string, required    | Название тега                            |
| slug       | string, required    | Уникальное ключевое слово тега, латиница |
| created_at | timestamp, optional | Временная отметка создания тега          |
| updated_at | timestamp, optional | Временная отметка обновления тега        |


**EventToTag** - m2m связь событий и тегов

**EventToUser** - m2m связь событий и их участников

**TaskToUser** - m2m связь для ответственных за задачу

**TagToUser** - m2m связь тегов и пользователей, подписка на теги


### Взаимодействие (с другими пользователями)
- WIP


### API-методы

`DEPRECATED`
`POST /api/v1/auth`
> Авторизует пользователя с помощью полученного от Google Sign-In токена (альтернативные сервисы на рассмотрении).
> Ключ будет проверен на валидность.
> В случае успеха вернет OAuth-ключ для доступа к API.
> Полученный `access_token` необходимо передавать в header каждого API-запроса в виде:<br />
> `Authorization: Bearer <access_token>`

> P.S. Актуальный способ авторизации - Firebase Authentication.
[Установка (Step 3+)](https://firebase.google.com/docs/android/setup) и [настройка UI](https://firebase.google.com/docs/auth/android/firebaseui).

**Параметры**

| Field   | Type             | Description                                                                 |
|---------|------------------|-----------------------------------------------------------------------------|
| token   | string, required | Токен для аутентификации                                                    |
| scope   | int, required    | Запрашиваемый уровень доступа. Для доступа к базовому API значение равно 0. |
| service | string, optional | Сервис аутентификации. По умолчанию: google                                 |

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

`DEPRECATED`
`POST /api/v1/auth/renew`
> Обновляет `access_token` с помощью заданного `refresh_token`.
> В случае успеха вернет новый OAuth-ключ для доступа к API.
> Передавать `access_token` в заголовке запроса не нужно.

**Параметры**

| Field         | Type             | Description                    |
|---------------|------------------|--------------------------------|
| access_token  | string, required | Старый `access_token`          |
| refresh_token | string, required | `refresh_token` для обновления |

**HTTP 200**
```
{
   "access_token": "Aw4V3T5blD",
   "token_type": "bearer",
   "expires_in": 86400,
   "refresh_token": "O2h4ZpeBt1"
}
```

----------------------------------------------------------------------------------------------------------------------


`GET /api/v1/events`
> Возвращает коллекцию событий, в которых принимает участие текущий пользователь, поддерживает пагинацию.

**Параметры**

| Field        | Type                | Description                                                                   |
|--------------|---------------------|-------------------------------------------------------------------------------|
| id           | uint[], optional    | Массив из ID событий, которые необходимо рассмотреть                          |
| tags         | string[], optional  | WIP. Теги, которым события принадлежат (дискас - пересечение или объединение) |
| participants | uint[], optional    | WIP. ID участников события                                                    |
| count        | uint, optional      | Количество событий, которое необходимо вернуть. По умолчанию: 100             |
| offset       | uint, optinal       | Количество событий, которое необходимо пропустить. По умолчанию: 0            |
| from         | timestamp, optional | Unix-время в мс, после которого события закончатся (включительно)             |
| to           | timestamp, optional | Unix-время в мс, до которого события могут начаться (включительно)            |
| created_from | timestamp, optional | Unix-время в мс, после которого события были созданы (включительно)           |
| created_to   | timestamp, optional | Unix-время в мс, до которого события были созданы (включительно)              |
| updated_from | timestamp, optional | Unix-время в мс, после которого события были обновлены (включительно)         |
| updated_to   | timestamp, optional | Unix-время в мс, до которого события были обновлены (включительно)            |

**HTTP 200**
```
{
   "success": true,
   "status": 200,
   "data": [
      {
         "id": "...",
         "owner_id": "...",
         "name": "...",
         "details": "...",
         "status": "...",
         "location": "...",
         "tags": [ "tagslug", ... ],
         "participants":
         [
            {
               "id": 1,
               "name": "Alice",
               "phone": "88005553535",
               "email": "alice@example.com"
            },
            ...
         ],
         "started_at": "...",
         "ended_at": "...",
         "created_at": "...",
         "updated_at": "..."
      },
      ...
   ],
   "count": 2,
   "offset": 0
}
```

----------------------------------------------------------------------------------------------------------------------


`POST /api/v1/events`
> Создание нового события.

**Параметры**

| Field        | Type                     | Description                                                         |
|--------------|--------------------------|---------------------------------------------------------------------|
| name         | string, optional         | Название события                                                    |
| details      | string, optional         | Описание события                                                    |
| location     | string, optional         | Место события                                                       |
| status       | string, optional         | Статус события                                                      |
| tags         | Tag[], optional          | WIP. Теги события. В случае, если тег отсутствует - он будет создан |
| participants | uint[], optional         | WIP. ID участников события                                          |

**HTTP 200**
```
{
   "success": true,
   "status": 200,
   "data": [
      {
         "id": 1,
         ...
      }
   ],
   "count": 1
}
```

----------------------------------------------------------------------------------------------------------------------


`PATCH /api/v1/events/{id}`
> Частичное обновление события. `id` события передается в URL запроса.

**Параметры**

| Field        | Type                     | Description                                                         |
|--------------|--------------------------|---------------------------------------------------------------------|
| name         | string, optional         | Название события                                                    |
| details      | string, optional         | Описание события                                                    |
| location     | string, optional         | Место события                                                       |
| status       | string, optional         | Статус события                                                      |

**HTTP 200**
```
{
   "success": true,
   "status": 200,
   "data": [
      {
         "id": 1,
         ...
      }
   ],
   "count": 1
}
```

----------------------------------------------------------------------------------------------------------------------


`DELETE /api/v1/events/{id}`
> Удаление события. `id` события передается в URL запроса.

**HTTP 200**
```
{
   "success": true,
   "status": 200,
   "message": "ok"
}
```

----------------------------------------------------------------------------------------------------------------------


`GET /api/v1/patterns`
> Возвращает коллекцию паттернов для заданного события. Поддерживает пагинацию.

**Параметры**

| Field    | Type             | Description                                           |
|----------|------------------|-------------------------------------------------------|
| id       | uint[], optional | WIP. Массив из ID событий, которые необходимо вернуть |
| event_id | uint, required   | ID события, для которого необходимо вернуть паттерны  |

**HTTP 200**
```
{
   "success": true,
   "status": 200,
   "data": [
      {
         "id": "...",
         "rrule": "...",
         "exrule": "...",
         "duration": "...",
         "started_at": "...",
         "ended_at": "...",
         "created_at": "...",
         "updated_at": "..."
      },
      ...
   ],
   "count": 2,
   "offset": 0
}
```

----------------------------------------------------------------------------------------------------------------------


`POST /api/v1/patterns`
> Создание нового паттерна.

**Параметры**

| Field      | Type                | Description                                                                     |
|------------|---------------------|---------------------------------------------------------------------------------|
| event_id   | uint, required      | GET-параметр. ID события для которого создается паттерн                         |
| rrule      | string, optional    | RRULE-строка, описывающая правило повторения события                            |
| exrule     | string, optional    | Не реализовано. EXRULE-строка, описывающая исключения для повторяющихся событий |
| duration   | uint, optional      | Продолжительность события в мс                                                  |
| started_at | timestamp, optional | Временная отметка начала события / правила                                      |
| ended_at   | timestamp, optional | Временная отметка конца события / правила                                       |

**HTTP 200**
```
{
   "success": true,
   "status": 200,
   "data": [
      {
         "id": "...",
         "rrule": "...",
         "exrule": "...",
         "duration": "...",
         "started_at": "...",
         "ended_at": "...",
         "created_at": "...",
         "updated_at": "..."
      }
   ],
   "count": 1
}
```

----------------------------------------------------------------------------------------------------------------------


`PATCH /api/v1/patterns/{id}`
> Частичное обновление паттерна. `id` паттерна передается в URL запроса.

**Параметры**

| Field      | Type                | Description                                                                     |
|------------|---------------------|---------------------------------------------------------------------------------|
| rrule      | string, optional    | RRULE-строка, описывающая правило повторения события                            |
| exrule     | string, optional    | Не реализовано. EXRULE-строка, описывающая исключения для повторяющихся событий |
| duration   | uint, optional      | Продолжительность события в мс                                                  |
| started_at | timestamp, optional | Временная отметка начала события / правила                                      |
| ended_at   | timestamp, optional | Временная отметка конца события / правила                                       |

**HTTP 200**
```
{
   "success": true,
   "status": 200,
   "data": [
      {
         "id": 1,
         ...
      }
   ],
   "count": 1
}
```

----------------------------------------------------------------------------------------------------------------------


`DELETE /api/v1/patterns/{id}`
> Удаление паттерна. `id` паттерна передается в URL запроса.

**HTTP 200**
```
{
   "success": true,
   "status": 200,
   "message": "ok"
}
```

----------------------------------------------------------------------------------------------------------------------


`GET|POST|PATCH|DELETE /api/v1/tasks`
> Все актуальные методы работы с задачами смотри в [swagger'e](http://planner.skillmasters.ga/docs).

**Параметры**

| Field     | Type           | Description                                                       |
|-----------|----------------|-------------------------------------------------------------------|
| parent_id | uint, optional | ID задачи-родителя. Будут возвращены только дети указанной задачи |

**HTTP 200**
```
{
   "...": "..."
}
```

----------------------------------------------------------------------------------------------------------------------


`GET|POST|PATCH|DELETE /api/v1/events/check`
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


`GET /api/v1/user`
> Возвращает пользователя по id, номеру телефона или почтовому адресу.

**Параметры**

| Field | Type             | Description                            |
|-------|------------------|----------------------------------------|
| id    | uint, optional   | Поиск пользователя по ID               |
| phone | string, optional | Поиск пользователя по номеру телефона  |
| email | string, optional | Поиск пользователя по почтовому адресу |

**HTTP 200**
```
{
   "id": 2,
   "name": "ExampleUser",
   "phone": "88005553535",
   "email": "user@example.com"
}
```

----------------------------------------------------------------------------------------------------------------------


`GET /api/v1/user/freetime`
> Возвращает непересекающиеся отрезки времени в определенном диапозоне, когда пользователь свободен.

**Параметры**

| Field   | Type                | Description                    |
|---------|---------------------|--------------------------------|
| user_id | uint, required      | ID пользователя                |
| from    | timestamp, required | Начало проверяемого промежутка |
| to      | timestamp, required | Конец проверяемого промежутка  |

**HTTP 200**
```
{
   "result":
   [
      {
         "from": 1551852784,
         "to": 1551862784
      },
      ...
   ],
   "count": 10,
   "cached": "true"
}
```

----------------------------------------------------------------------------------------------------------------------


`GET /api/v1/tags`
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


`GET /api/v1/tags/subscribe`
> Подписка пользователя на переданные теги

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


`GET /api/v1/tags/unsubscribe`
> Отписка пользователя от переданных тегов

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
