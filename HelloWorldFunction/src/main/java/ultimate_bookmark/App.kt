package ultimate_bookmark

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ultimate_bookmark.dynamo.DynamoDbSelector
import ultimate_bookmark.handler.CreateBookmarkHandler
import ultimate_bookmark.handler.ListBookmarkHandler
import ultimate_bookmark.router.Route
import ultimate_bookmark.router.Router

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private val dynamoDbSelector = DynamoDbSelector()
    private val objectMapper = ObjectMapper().registerKotlinModule()
    private val router = Router()

    init {
        router.addRoute(Route(Route.HttpMethod.POST, Regex("/bookmark"), CreateBookmarkHandler(dynamoDbSelector)))
        router.addRoute(Route(Route.HttpMethod.GET, Regex("/bookmark"), ListBookmarkHandler(dynamoDbSelector)))
    }

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-Custom-Header" to "application/json",
        )

        return APIGatewayProxyResponseEvent()
            .withHeaders(headers)
            .withBody(objectMapper.writeValueAsString(router.handleRequest(input, context)))
    }
}
