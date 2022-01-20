package ultimate_bookmark.dynamo

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.util.UUID

@DynamoDbImmutable(builder = Bookmark.Builder::class)
class Bookmark(
    @get:DynamoDbPartitionKey val id: UUID,
    val title: String,
    val url: String,
) {
    // Workaround to @DynamoDbBean not working with Kotlin's immutable classes
    // https://github.com/aws/aws-sdk-java-v2/issues/2096
    class Builder {
        private var id: UUID? = null
        fun id(value: UUID) = apply { id = value }

        private var title: String? = null
        fun title(value: String) = apply { title = value }

        private var url: String? = null
        fun url(value: String) = apply { url = value }

        fun build() = Bookmark(
            id = id!!,
            title = title!!,
            url = url!!
        )
    }
}
