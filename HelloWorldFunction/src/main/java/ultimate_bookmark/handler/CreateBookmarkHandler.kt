package ultimate_bookmark.handler

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import ultimate_bookmark.dynamo.Bookmark
import ultimate_bookmark.dynamo.DynamoDbSelector
import ultimate_bookmark.dynamo.toDynamoEnv
import ultimate_bookmark.router.RouteHandler
import java.util.UUID

class CreateBookmarkHandler(
    private val dynamoDbSelector: DynamoDbSelector
) : RouteHandler<CreateBookmarkResponse> {
    private val objectMapper = ObjectMapper().registerKotlinModule()

    override fun handle(apiRequestEvent: APIGatewayProxyRequestEvent, context: Context): CreateBookmarkResponse {
        val request = objectMapper.readValue<CreateBookmarkRequest>(apiRequestEvent.body)
        val table = dynamoDbSelector.getEnhanced(apiRequestEvent.requestContext.stage.toDynamoEnv())
            .table("Bookmarks", TableSchema.fromImmutableClass(Bookmark::class.java))

        val bookmark = Bookmark(
            id = UUID.randomUUID(),
            title = request.title,
            url = request.url,
        )
        table.putItem {
            it.item(bookmark)
            it.conditionExpression(Expression.builder().expression("attribute_not_exists(id)").build())
        }

        return CreateBookmarkResponse(bookmark)
    }
}

data class CreateBookmarkRequest(val title: String, val url: String)
data class CreateBookmarkResponse(val bookmark: Bookmark)
