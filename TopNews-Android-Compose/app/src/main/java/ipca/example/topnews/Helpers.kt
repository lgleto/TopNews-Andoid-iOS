package ipca.example.topnews

import java.net.URLDecoder
import java.net.URLEncoder


fun String.encodeURL() : String{
    return  URLEncoder.encode(this, "UTF-8")
}

fun String.decodeURL() : String{
    return  URLDecoder.decode(this, "UTF-8")
}