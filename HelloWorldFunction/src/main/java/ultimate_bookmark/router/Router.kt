package ultimate_bookmark.router

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent

class Router {
    private val routes = mutableListOf<Route<*>>()

    fun addRoute(route: Route<*>) {
        routes.add(route)
    }

    fun handleRequest(requestEvent: APIGatewayProxyRequestEvent, context: Context): Any {
        val httpMethod = Route.HttpMethod.valueOf(requestEvent.httpMethod.uppercase())
        val route = routes.find { it.method == httpMethod && it.pattern.matches(requestEvent.path) }
            ?: throw RouteNotFoundException("No route can handle $httpMethod ${requestEvent.path}")
        return route.handler.handle(requestEvent, context)
    }
}
