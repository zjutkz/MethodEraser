# MethodEraser
A gradle plugin to erase some method in release build type

### download

project's build.gradle:

```groovy
repositories {
    ....
    maven { url 'https://jitpack.io' }
}
```

```groovy
classpath 'com.github.zjutkz:MethodEraser:1.0'
```



app or lib's build.gradle:

```groovy
compile ('com.github.zjutkz:MethodEraser:0.0.11') {
    exclude module: 'jsr305'
}
```



### usage

```java
@Eraser
public int test() {
    int a = 10;
    a = a + 1;
    return a;
}
```

you can see result in ../build/intermediates/classes/release/..

```java
@Eraser
public int test() {
    return 1;
}
```