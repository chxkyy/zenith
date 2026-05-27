---
name: jdk-21
description: JDK 21 新特性使用规范。当使用 Java 21 新特性（虚拟线程、模式匹配、Record Patterns 等）时使用
when_to_use: 使用虚拟线程、switch 模式匹配、Record Patterns、Sequenced Collections 等 JDK 21 新特性时
paths: "**/*.java"
---

# JDK 21 新特性使用规范

使用 JDK 21 新特性时必须遵循以下规范。

## 一、JDK 21 概述

JDK 21 于 **2023 年 9 月 19 日** 正式发布，是一个 **LTS（长期支持）版本**。

### 正式特性

| JEP | 特性名称 | 说明 |
|-----|----------|------|
| 444 | Virtual Threads | 虚拟线程 |
| 441 | Pattern Matching for switch | switch 模式匹配 |
| 440 | Record Patterns | 记录模式 |
| 431 | Sequenced Collections | 有序集合 |
| 439 | Generational ZGC | 分代 ZGC |

### 预览特性

| JEP | 特性名称 | 说明 |
|-----|----------|------|
| 430 | String Templates | 字符串模板 |
| 453 | Structured Concurrency | 结构化并发 |
| 446 | Scoped Values | 作用域值 |

## 二、虚拟线程（Virtual Threads）

### 1. 基本概念

| 特性 | 平台线程 | 虚拟线程 |
|------|---------|---------|
| 实现 | OS 线程包装器 | JDK 实现 |
| 数量 | 受 OS 限制 | 可达数百万 |
| 成本 | 昂贵（需池化） | 便宜（无需池化） |
| 调度 | OS 调度 | JDK 调度器 |

### 2. 创建方式

```java
// ✅ 方式一：ExecutorService（推荐）
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> doTask());
}

// ✅ 方式二：Thread.Builder API
Thread thread = Thread.ofVirtual()
    .name("my-virtual-thread")
    .unstarted(() -> doTask());
thread.start();

// ✅ 方式三：便捷方法
Thread.startVirtualThread(() -> doTask());
```

### 3. 使用规范

#### 不要池化虚拟线程

```java
// ❌ 错误：不要池化虚拟线程
private final ExecutorService pool = Executors.newCachedThreadPool();

// ✅ 正确：每个任务创建新虚拟线程
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> doTask());
}
```

#### 限制并发使用 Semaphore

```java
// ✅ 使用 Semaphore 限制并发
private final Semaphore semaphore = new Semaphore(100);

public void process() {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        executor.submit(() -> {
            semaphore.acquire();
            try {
                doTask();
            } finally {
                semaphore.release();
            }
        });
    }
}
```

#### 避免 Pinning 问题

**什么是 Pinning？**

Pinning 问题是指虚拟线程被"钉住"（Pinned）到载体线程（Carrier Thread）时，无法正常卸载，导致虚拟线程的优势丧失。

| 场景 | 说明 |
|------|------|
| `synchronized` 块/方法内阻塞 | 虚拟线程持有监视器锁时不能卸载 |
| 执行 native 方法 | native 代码执行期间无法卸载 |

```java
// ❌ 可能导致 pinning：synchronized 块内阻塞
public void doWork() {
    synchronized (lock) {
        // 虚拟线程在这里被 "钉" 住
        // 即使调用阻塞 I/O，也无法卸载
        // 载体线程被占用，其他虚拟线程无法使用
        blockingIO();  
    }
}

// ✅ 正确：使用 ReentrantLock
private final ReentrantLock lock = new ReentrantLock();

public void doWork() {
    lock.lock();
    try {
        blockingIO();  // 可以正常卸载，不会 pin
    } finally {
        lock.unlock();
    }
}
```

### 4. Pinning 诊断

```bash
# JVM 参数：追踪 pinning
-Djdk.tracePinnedThreads=full   # 完整堆栈
-Djdk.tracePinnedThreads=short  # 仅问题帧
```

### 5. 适用场景

| ✅ 适合 | ❌ 不适合 |
|--------|----------|
| 高并发 I/O 密集型 | CPU 密集型任务 |
| 请求处理类服务器 | 需要精确优先级控制 |
| 数千以上并发任务 | 依赖 SecurityManager |

## 三、Switch 模式匹配

### 1. 基本用法

```java
// ✅ 推荐：switch 模式匹配
static String formatter(Object obj) {
    return switch (obj) {
        case Integer i -> String.format("int %d", i);
        case Long l    -> String.format("long %d", l);
        case Double d  -> String.format("double %f", d);
        case String s  -> String.format("String %s", s);
        default        -> obj.toString();
    };
}
```

### 2. 处理 null

```java
// ✅ 在 switch 内部处理 null
static void process(String s) {
    switch (s) {
        case null         -> System.out.println("null value");
        case "Foo", "Bar" -> System.out.println("Great");
        default           -> System.out.println("Ok");
    }
}
```

### 3. 守卫条件（when 子句）

```java
// ✅ 使用 when 子句添加条件
static void processResponse(String response) {
    switch (response) {
        case null -> { }
        case String s when s.equalsIgnoreCase("YES") -> 
            System.out.println("Confirmed");
        case String s when s.equalsIgnoreCase("NO") -> 
            System.out.println("Denied");
        case String s -> 
            System.out.println("Unknown: " + s);
    }
}
```

### 4. Sealed 类穷尽性

```java
sealed interface Shape permits Circle, Rectangle, Square {}
record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}
record Square(double side) implements Shape {}

// ✅ 无需 default，编译器自动检查穷尽
static double area(Shape shape) {
    return switch (shape) {
        case Circle c    -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Square s    -> s.side() * s.side();
    };
}
```

## 四、Record Patterns（记录模式）

### 1. 基本解构

```java
record Point(int x, int y) {}

// ✅ 直接解构
if (obj instanceof Point(int x, int y)) {
    System.out.println("x=" + x + ", y=" + y);
}
```

### 2. 嵌套解构

```java
record Point(int x, int y) {}
record ColoredPoint(Point p, Color c) {}
record Rectangle(ColoredPoint upperLeft, ColoredPoint lowerRight) {}

// ✅ 嵌套解构
if (r instanceof Rectangle(ColoredPoint(Point(var x, var y), var c), var lr)) {
    System.out.println("Upper-left: x=" + x + ", y=" + y + ", color=" + c);
}
```

### 3. 结合 switch

```java
record Point(int x, int y) {}

static void process(Object obj) {
    switch (obj) {
        case Point(int x, int y) -> System.out.println("Point: " + x + ", " + y);
        case String s            -> System.out.println("String: " + s);
        default                  -> System.out.println("Unknown");
    }
}
```

## 五、Sequenced Collections（有序集合）

### 1. 新增接口

```java
// SequencedCollection 接口
interface SequencedCollection<E> extends Collection<E> {
    SequencedCollection<E> reversed();  // 反向视图
    void addFirst(E e);                 // 添加到头部
    void addLast(E e);                  // 添加到尾部
    E getFirst();                       // 获取第一个元素
    E getLast();                        // 获取最后一个元素
    E removeFirst();                    // 移除第一个元素
    E removeLast();                     // 移除最后一个元素
}
```

### 2. 统一访问方式

```java
// ✅ JDK 21：统一 API
list.getFirst();
list.getLast();
list.reversed();  // 反向视图

linkedHashSet.getFirst();
linkedHashSet.getLast();
linkedHashSet.reversed();  // LinkedHashSet 也支持了！

sortedSet.getFirst();
sortedSet.getLast();
```

### 3. 反向迭代

```java
// ✅ 反向迭代变得简单
for (var e : list.reversed()) {
    process(e);
}

// 反向 Stream
list.reversed().stream()
    .filter(...)
    .forEach(...);
```

### 4. SequencedMap

```java
// ✅ Map 也支持有序操作
map.firstEntry();
map.lastEntry();
map.reversed();
map.putFirst(key, value);
map.putLast(key, value);
```

## 六、String Templates（预览特性）

### 1. 启用预览

```bash
# 编译
javac --release 21 --enable-preview Main.java

# 运行
java --enable-preview Main
```

### 2. 基本用法

```java
// ✅ 使用 STR 模板处理器
String name = "World";
String greeting = STR."Hello, \{name}!";

// 多行文本
String html = STR."""
    <html>
      <body>
        <h1>\{title}</h1>
      </body>
    </html>
    """;

// 表达式计算
int x = 10, y = 20;
String result = STR."\{x} + \{y} = \{x + y}";
```

### 3. FMT 格式化

```java
// ✅ 使用 FMT 进行格式化
String table = FMT."""
    Name        Score
    %-10s\{name}  %5.2f\{score}
    """;
```

## 七、结构化并发（预览特性）

### 1. 基本用法

```java
// ✅ 使用 StructuredTaskScope
Response handle() throws ExecutionException, InterruptedException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Supplier<User> user = scope.fork(() -> findUser());
        Supplier<Order> order = scope.fork(() -> fetchOrder());

        scope.join()           // 等待所有子任务
             .throwIfFailed(); // 传播异常

        return new Response(user.get(), order.get());
    }
}
```

### 2. 关闭策略

| 策略 | 行为 |
|------|------|
| `ShutdownOnFailure` | 任一子任务失败时关闭 |
| `ShutdownOnSuccess` | 任一子任务成功时关闭 |

### 3. 竞争模式

```java
// ✅ 多个服务竞争，取最快结果
<T> T race(List<Callable<T>> tasks) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnSuccess<T>()) {
        for (var task : tasks) {
            scope.fork(task);
        }
        return scope.join().result();
    }
}
```

## 八、分代 ZGC

### 启用方式

```bash
# JDK 21 启用分代 ZGC
java -XX:+UseZGC -XX:+ZGenerational -jar app.jar
```

### 性能优势

| 指标 | 改进 |
|------|------|
| 堆内存需求 | 降至 1/4 |
| 吞吐量 | 提升 4 倍 |
| 暂停时间 | < 1ms |

## 九、最佳实践总结

| 特性 | 最佳实践 |
|------|----------|
| 虚拟线程 | 不要池化、用 Semaphore 限流、避免 synchronized 阻塞 |
| Switch 模式匹配 | 使用 when 守卫、sealed 类无需 default |
| Record Patterns | 嵌套解构、使用 var 推断类型 |
| Sequenced Collections | 统一使用 getFirst/getLast、reversed() 反向迭代 |
| String Templates | 使用 STR 处理器、注意安全性 |
| 结构化并发 | try-with-resources 确保关闭、选择合适的关闭策略 |

## 十、Maven 配置

```xml
<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>21</source>
                <target>21</target>
                <compilerArgs>
                    <arg>--enable-preview</arg>  <!-- 启用预览特性 -->
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 十一、版本兼容性

| JDK 版本 | 类型 | 发布时间 |
|---------|------|----------|
| JDK 8 | LTS | 2014-03 |
| JDK 11 | LTS | 2018-09 |
| JDK 17 | LTS | 2021-09 |
| **JDK 21** | **LTS** | **2023-09** |
| JDK 25 | LTS（预计） | 2025-09 |
