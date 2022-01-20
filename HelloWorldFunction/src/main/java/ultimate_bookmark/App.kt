package ultimate_bookmark

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import ultimate_bookmark.dynamo.DynamoDbSelector
import ultimate_bookmark.dynamo.toDynamoEnv
import ultimate_bookmark.handler.CreateBookmarkHandler
import ultimate_bookmark.handler.CreateBookmarkRequest

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private val dynamoDbSelector = DynamoDbSelector()
    private val objectMapper = ObjectMapper().registerKotlinModule()

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val ddb = dynamoDbSelector.get(input.requestContext.stage.toDynamoEnv())
        val client = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(ddb)
            .build()
        val createBookmarkHandler = CreateBookmarkHandler(client)
        val request = objectMapper.readValue<CreateBookmarkRequest>(input.body)
        val response = createBookmarkHandler.handle(request, context)

        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-Custom-Header" to "application/json",
        )

        return APIGatewayProxyResponseEvent()
            .withHeaders(headers)
            .withBody(objectMapper.writeValueAsString(response))
    }
}
