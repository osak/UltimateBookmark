package ultimate_bookmark.handler

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import ultimate_bookmark.dynamo.Bookmark
import ultimate_bookmark.dynamo.DynamoDbSelector
import ultimate_bookmark.dynamo.toDynamoEnv
import ultimate_bookmark.router.RouteHandler

class ListBookmarkHandler(
    private val dynamoDbSelector: DynamoDbSelector
) : RouteHandler<ListBookmarkResponse> {
    override fun handle(apiRequestEvent: APIGatewayProxyRequestEvent, context: Context): ListBookmarkResponse {
        val table = dynamoDbSelector.getEnhanced(apiRequestEvent.requestContext.stage.toDynamoEnv())
            .table("Bookmarks", TableSchema.fromImmutableClass(Bookmark::class.java))
        val limit = apiRequestEvent.queryStringParameters?.get("limit")?.toInt() ?: 10
        val pageIter = table.scan {
            it.limit(limit)
        }
        val bookmarks = pageIter.flatMap { it.items() }
        return ListBookmarkResponse(bookmarks)
    }
}

data class ListBookmarkResponse(val bookmarks: List<Bookmark>)
