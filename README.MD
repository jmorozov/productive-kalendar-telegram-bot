# productive-kalendar-telegram-bot

Telegram bot for [productive-kalendar](https://github.com/jmorozov/productive-kalendar)

# productive-kalendar-telegram-bot [RU]

Telegram бот для [productive-kalendar](https://github.com/jmorozov/productive-kalendar)

Для работы необходимо в properties указать имя бота, токен и адрес хоста, где крутится productive-kalendar

## Формат сообщений

В сообщениях с интервалами дат, возможно опустить один из концов интервала, тогда вместо него будет использована текущая дата

### Является ли дата выходным или рабочим днём

Просто отправьте дату в формате - гггг-ММ-дд, например 2018-09-02

### Количество выходных дней в интервале дат, не включая конечную

Выходных с гггг-ММ-дд до ггг-ММ-дд

### Количество рабочих дней в интервале дат, не включая конечную

Рабочих с гггг-ММ-дд до ггг-ММ-дд