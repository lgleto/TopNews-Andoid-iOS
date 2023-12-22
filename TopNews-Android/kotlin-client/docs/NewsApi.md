# NewsApi

All URIs are relative to *https://newsapi.org/v2*

Method | HTTP request | Description
------------- | ------------- | -------------
[**topHeadlinesGet**](NewsApi.md#topHeadlinesGet) | **GET** /top-headlines | 


<a name="topHeadlinesGet"></a>
# **topHeadlinesGet**
> Articles topHeadlinesGet(country, category, apiKey)



Get user playlists

### Example
```kotlin
// Import classes:
//import io.swagger.client.infrastructure.*
//import io.swagger.client.models.*

val apiInstance = NewsApi()
val country : kotlin.String = country_example // kotlin.String | 
val category : kotlin.String = category_example // kotlin.String | 
val apiKey : kotlin.String = apiKey_example // kotlin.String | 
try {
    val result : Articles = apiInstance.topHeadlinesGet(country, category, apiKey)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling NewsApi#topHeadlinesGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling NewsApi#topHeadlinesGet")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **country** | **kotlin.String**|  | [optional]
 **category** | **kotlin.String**|  | [optional]
 **apiKey** | **kotlin.String**|  | [optional]

### Return type

[**Articles**](Articles.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json, text/json
 - **Accept**: text/plain, application/json, text/json, application/_*+json

