# 📧 Email-бот: Отправка курсов валют на почту

Этот инструмент отправляет письма с информацией о валютных курсах, используя параметры, передаваемые через флаги командной строки.

---

## 📌 Подготовка

Перед началом необходимо создать **пароль приложения** для Google Mail, если используете Gmail.

🔗 Справка Google:
[Как создать пароль приложения для Google Mail](https://support.google.com/accounts/answer/185833?hl=ru)

---

## ⚙️ Доступные флаги

| Флаг                    | Описание                                                                                                                                                     |
| ----------------------- |--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `--email.from.user="?"` | Email-адрес отправителя.                                                                                                                                     |
| `--email.from.pass="?"` | Пароль приложения для указанного email-адреса.                                                                                                               |
| `--email.to="?"`        | Email-адрес(а) получателя. Можно указать несколько адресов через запятую, обязательно в кавычках.                                                            |
| `--type=?`              | Тип операции. <br>**Доступные значения:** `SELL`, `BUY`. <br>**SELL** — просмотр предложений на покупку валюты. <br>**BUY** — предложения на продажу валюты. |
| `--currency=?`          | Валюта, по которой рассчитываются курсы. <br>**Доступные значения:** значения USD, EUR, RUB. <br>**По умолчанию:** `USD`.                                    |
| `--parsing.delay=?`     | Задержка между парсингами в минутах. <br>**По умолчанию:** 15 минут.                                                                                         |
| `--sum=?`               | Сумма в вашей валюте для расчёта.                                                                                                                            |
| `--always=?`            | Поведение отправки письма. <br>Если `true`, письмо отправляется даже при отсутствии изменений в курсе. <br>**По умолчанию:** `false`.                        |

---

## 🧪 Пример использования
Находясь в папке с архивом файла jar
```bash
java -jar .\CurrencySiteParsing-0.0.1-SNAPSHOT.jar 
--email.from.user=example@gmail.com 
--email.from.pass="**** **** **** ****" 
--parsing.delay=10 
--sum=50000 
--currency=USD 
--type=sell
```