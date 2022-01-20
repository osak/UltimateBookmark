package ultimate_bookmark.handler

import com.amazonaws.services.lambda.runtime.Context
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import ultimate_bookmark.dynamo.Bookmark
import java.util.UUID

class CreateBookmarkHandler(
    private val dynamoDbClient: DynamoDbEnhancedClient,
) {
    private val table = dynamoDbClient.table("Bookmarks", TableSchema.fromImmutableClass(Bookmark::class.java))

    fun handle(request: CreateBookmarkRequest, context: Context): CreateBookmarkResponse {
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
