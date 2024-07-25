# Upgrade notes to BridgeDb 2.0

## 1. Initialing data sources

Replace

```java
    BioDataSource.init();
```

with

```java
    DataSourceTxt.init();
```

## 2. Getting DataSource's

Replace

```java
    DataSource ds = DataSource.getBySystemCode("Ck");
```

with

```java
    DataSource ds = DataSource.getExistingBySystemCode("Ck");
```

Similarly, and more important:

```java
    DataSource ds = DataSource.getByFullName("PubChem-compound");
```

with

```java
    DataSource ds = DataSource.getExistingByFullName("PubChem-compound");
```

The latter two new methods throw an `IllegalArgumentException` if the `DataSource` does not exist.
Use one of the following two methods to check first:

```java
    DataSource.systemCodeExists("Ck");
    DataSource.fullNameExists("PubChem-compound");
```
