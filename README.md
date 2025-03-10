# Поиск кратчайшего пути в распределённой системе

В этом задании вы реализуете алгоритм поиска кратчайшего пути от узла-инициатора до каждого узла распределённой системы.

## Постановка задачи

В файле [`DijkstraProcess.java`](src/solution/DijkstraProcess.java) находится описание интерфейса, 
который вам предстоит реализовать. Свой код на языке Java пишите в файле 
[`DijkstraProcessImpl.java`](src/solution/DijkstraProcessImpl.java).
**Не забудьте указать свое имя и фамилию в файле с вашим решением.**

Для решения на Kotlin откройте Java решения и нажмите Ctrl+Alt+Shift+K (Cmd+Alt+Shift+K на MacOS) в IntelliJ IDEA для 
конвертации соответствующего фала из XXX.java в XXX.kt. На вопрос 
"Some code in the rest of your project may require corrections after performing this conversion. 
Do you want to find such code and correct it too?" отвечайте "No". 
Пишите код в соответствующем kt файле, который получится после конвертации.

### Окружение процесса

В этом задании распределённая система рассматривается как ориентированный взвешенный граф, в котором у каждого 
процесса `v` есть множество соседей `N(v)`.

Каждому из процессов системы будет присвоен уникальный идентификатор. Через ссылку на объект
[`Environment`](src/internal/Environment.java) ваш процесс
может узнать конфигурацию системы, общаться с другими процессами и сообщать об останове диффундирующего вычисления:

* `env.getProcessId()` &mdash; возвращается идентификатор вашего процесса.
* `env.getNeighbours` &mdash; возвращает множество процессов-соседей вашего процесса, при этом для каждого 
процесса-соседа указан его идентификатор и расстояние от вашего процесса до этого соседа.
* `env.send(dstId, message)` &mdash; посылает сообщение процессу с идентификатором `dstId`. 
При этом топология сети неполная: процесс `v` может послать сообщение процессу `u` только если `u` является соседом 
`v`, либо `v` является соседом `u`.
* `env.finishExecution()` &mdash; сообщает о завершении вычисления. Этот метод может быть вызван лишь один раз, 
после чего вычисление будет считаться завершённым.

Методы вашего класса [`DijkstraProcessImpl`](src/solution/DijkstraProcessImpl.java)
будут вызываться в следующих случаях:

* `onMessage(srcId, message)` &mdash; вызывается при получении сообщения от другого процесса с номером `srcId`.
  Между каждой парой процессов гарантируется FIFO порядок передачи сообщений.
* `startComputation()` &mdash; сообщает данному процессу, что он должен стать инициатором в алгоритме вычисления 
кратчайшего пути. Гарантируется, что только один процесс будет выбран инициатором.
* `getDistance()` &mdash; вызывается после завершения вычисления. Процесс должен вернуть кратчайшее расстояние 
от инициатора до этого процесса, или `null`, если не существует пути от инициатора до этого процесса.

## Работа с сообщениями

**Каждое отправленное сообщение должно быть сериализуемо.** 
Описание класса сообщений может выглядеть, например, так:

```java
record MyMessage(int key, String value) implements java.io.Serializable {}
```

или на Kotlin:

```kotlin
data class MyMessage(val key: Int, val value: String) : java.io.Serializable
```

Размер каждого отправленного сообщения не должен превышать двухсот байт.

Заметьте, что метод `onMessage(int senderPid, Object message)`в качестве аргумента принимает не описанный
вами тип сообщения, а `java.lang.Object`. Используйте приведения типов вида

```java 
MyMessage typedMessage = (MyMessage) message;
```

и, если нужно, оператор `instanceof`

```java
if (message instanceof MyMessage) {
    // ...
}
```

для приведения сообщения к нужному вам типу.

На Kotlin:

```kotlin
val typedMessage = message as MyMessage
// or 
if (message is MyMessage) { 
    // ...
}
```

## Тестирование

Тестирования реализации происходит путем запуска теста [`DijkstraProcessTest.java`](test/solution/DijkstraProcessTest.java)

Из командной строки: `./gradlew test --tests 'solution*'`

## Формат сдачи

Выполняйте задание в этом репозитории.
**Ваш код должен быть реализован в одном файле [`src/solution/DijkstraProcessImpl.java`](src/solution/DijkstraProcessImpl.java) 
или `src/solution/DijkstraProcessImpl.kt`**.

Инструкции по сдаче заданий находятся в
[этом документе](https://docs.google.com/document/d/1kUZl08zUoprZzB2xCX89HbrTkSTAQZens6xAzu-5pvw). 
