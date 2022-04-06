package ultimate_bookmark.router

data class Route<Response : Any>(
    val method: HttpMethod,
    val pattern: Regex,
    val handler: RouteHandler<Response>,
) {
    enum class HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
    }
}
