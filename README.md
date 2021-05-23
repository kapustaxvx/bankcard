## Bankcard
### Описание
Сервис предназначен для работы с клиентами банка, контрагентами и 
агентам технической поддержки банка.



### Функционал
Клиенты банка могут:
- создавать банковские карты на любой из своих
  номеров счета.
- отправлять запросы на пополнение баланса счета и
  снятие с него средств.
- просмотр всех своих банковских карт.
- просмотр баланса на своем лицевом счете.

Контрагент может:
- зарегистрировать себя в банке.
- отправлять запросы на пополнение баланса счета и
  снятие с него средств.
- просматривать всех контрагентов.

Агент тех.поддержки может:
- внести нового клиента банка в систему.
- создать новый счет для клиента банка.
- подтвердить выпуск банковской карты для клиента.
- подтверждать и производить списания и начисления на банковские 
счета как клиентов, так и контрагентов.
    

### Запуск сервиса
Для работы сервиса необходимо наличие инфраструктуры:
- PostgreSQL

#### Используя IDE
Необходимо создать конфигурацию запуска Spring Boot'ового приложения в Idea.
В качестве Main класса указать `com.moskalenko.application.BankApplication`.

#### Используя shell
Для запуска сервиса на машине разработчика достаточно выполнить сборку сервиса с помощью Maven
Для этого в директории с проектом достаточно воспользоваться командой

```shell script
mvn clean install
 ```

После этого запустить сервис, выполнив команду из корневой директории:

```shell script
java -jar service/targer/service-1.0-SNAPSHOT.jar
``` 

При этом сервис будет запущен с конфигурацией, которая подтянется из ресурсов в classpath'е проекта.
Поднят сервис будет в _embedded tomcat_ на порту _8080_ в руте.

### Конфигурация
Конфигурация сервиса представлена следующими файлами:
- [application.yml](service/src/main/resources/application.yml) - файл с основными настройками приложения

### Инструкция по использованию

- Создание клиента отделом тех.поддержки

```shell script
POST http://localhost:8080/support/customers
Content-Type: application/json

{
"firstName": "Ivan",
"secondName": "Ivanov"
}
```

- Создание счета для клиента отделом тех.поддержки

```shell script
POST http://localhost:8080/support/customers/{customerId}/accounts
```

POST http://localhost:8080/support/customers/1/accounts
Content-Type: application/json

- Запрос на создание карты по счету от клиента

```shell script
POST http://localhost:8080/customer/account/{accountId}/cards
```

POST http://localhost:8080/customer/account/1/cards
Content-Type: application/json

- Подтверждение выпуска карты отделом тех.поддержки

```shell script
PUT http://localhost:8080/support/cards/{cardId}/confirm
```

PUT http://localhost:8080/support/cards/1/confirm
Content-Type: application/json

- Запрос на пополнение карты от клиента

```shell script
PUT http://localhost:8080/customer/card/{cardId}/increase/{amount}
```

PUT http://localhost:8080/customer/card/1/increase/7878878
Content-Type: application/json

- Подтверждение запроса на пополнение от тех.поддержки

```shell script
PUT http://localhost:8080/support/increase/{increaseId}/confirm
```

PUT http://localhost:8080/support/increase/1/confirm
Content-Type: application/json

- Запрос на снятие средств клиентом

```shell script
PUT http://localhost:8080/customer/card/{cardId}/decrease/{amount}
```

PUT http://localhost:8080/customer/card/1/decrease/898.898
Content-Type: application/json

- Подтверждение запроса на снятия средств отделом тех.поддержки

```shell script
PUT http://localhost:8080/support/decrease/{decreaseId}/confirm
```

PUT http://localhost:8080/support/decrease/1/confirm
Content-Type: application/json

- Получение баланса клиентом по номеру счета

```shell script
GET http://localhost:8080/customer/account/{accountId}/balance
```

GET http://localhost:8080/customer/account/1/balance
Content-Type: application/json

- Получение всех банковских карт клиента

```shell script
GET http://localhost:8080/customer/{customerId}/cards
```

GET http://localhost:8080/customer/1/cards
Content-Type: application/json

- Создание контрагента

```shell script
POST http://localhost:8080/counterparty
Content-Type: application/json

{
"description": "Apple"
}
```

- Запрос на пополнение баланса от контрагента

```shell script
PUT http://localhost:8080/counterparty/account/{accountId}/increase/{amount}
```

PUT http://localhost:8080/counterparty/account/2/increase/8989
Content-Type: application/json


- Запрос на снятие средств от контрагента

```shell script
PUT http://localhost:8080/counterparty/account/{accountId}/decrease/{amount}
```

PUT http://localhost:8080/counterparty/account/2/decrease/909
Content-Type: application/json

- Получение баланса контрагента

```shell script
GET http://localhost:8080/counterparty/account/{acountId}/balance
```

GET http://localhost:8080/counterparty/account/2/balance
Content-Type: application/json

- Получение всех контрагентов

```shell script
GET http://localhost:8080/counterparty/
```

GET http://localhost:8080/counterparty
Content-Type: application/json