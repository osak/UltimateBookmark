package ultimate_bookmark.router

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent

interface RouteHandler<Response : Any> {
    fun handle(apiRequestEvent: APIGatewayProxyRequestEvent, context: Context): Response
}
