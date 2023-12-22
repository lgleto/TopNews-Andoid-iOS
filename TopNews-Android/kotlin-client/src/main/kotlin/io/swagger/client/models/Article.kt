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
package io.swagger.client.models

import io.swagger.client.models.Source

/**
 * 
 * @param source 
 * @param author 
 * @param title 
 * @param description 
 * @param url 
 * @param urlToImage 
 * @param publishedAt 
 * @param content 
 */
data class Article (
    val source: Source? = null,
    val author: kotlin.String? = null,
    val title: kotlin.String? = null,
    val description: kotlin.String? = null,
    val url: kotlin.String? = null,
    val urlToImage: kotlin.String? = null,
    val publishedAt: kotlin.String? = null,
    val content: kotlin.String? = null,
) {

}
