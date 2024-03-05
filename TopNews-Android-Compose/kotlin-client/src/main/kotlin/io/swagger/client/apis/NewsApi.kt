/**
* News App
* newsapi api with one end-point.
*
* OpenAPI spec version: 1.0.0
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package io.swagger.client.apis

import io.swagger.client.models.Articles

import io.swagger.client.infrastructure.*

class NewsApi(basePath: kotlin.String = "https://newsapi.org/v2") : ApiClient(basePath) {

    /**
    * 
    * Get user playlists
    * @param country  (optional)
    * @param category  (optional)
    * @param apiKey  (optional)
    * @return Articles
    */
    @Suppress("UNCHECKED_CAST")
    fun topHeadlinesGet(country: kotlin.String, category: kotlin.String, apiKey: kotlin.String) : Articles {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, kotlin.collections.List<kotlin.String>>().apply {
            if (country != null) {
                put("country", listOf(country.toString()))
            }
            if (category != null) {
                put("category", listOf(category.toString()))
            }
            if (apiKey != null) {
                put("apiKey", listOf(apiKey.toString()))
            }
        }
        

        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/top-headlines",
            query = localVariableQuery,
        )
        val response = request<Articles>(
            localVariableConfig,
            localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as Articles
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
            else -> throw kotlin.IllegalStateException("Undefined ResponseType.")
        }
    }

}
