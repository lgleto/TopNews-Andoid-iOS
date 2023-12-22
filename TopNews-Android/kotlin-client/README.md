# io.swagger.client - Kotlin client library for News App

## Requires

* Kotlin 1.1.2
* Gradle 3.3

## Build

First, create the gradle wrapper script:

```
gradle wrapper
```

Then, run:

```
./gradlew check assemble
```

This runs all tests and packages the library.

## Features/Implementation Notes

* Supports JSON inputs/outputs, File inputs, and Form inputs.
* Supports collection formats for query parameters: csv, tsv, ssv, pipes.
* Some Kotlin and Java types are fully qualified to avoid conflicts with types defined in Swagger definitions.
* Implementation of ApiClient is intended to reduce method counts, specifically to benefit Android targets.

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *https://newsapi.org/v2*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*NewsApi* | [**topHeadlinesGet**](docs/NewsApi.md#topheadlinesget) | **GET** /top-headlines | 


<a name="documentation-for-models"></a>
## Documentation for Models

 - [io.swagger.client.models.Article](docs/Article.md)
 - [io.swagger.client.models.Articles](docs/Articles.md)
 - [io.swagger.client.models.Source](docs/Source.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
