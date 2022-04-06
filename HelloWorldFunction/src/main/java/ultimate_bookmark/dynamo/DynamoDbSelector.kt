package ultimate_bookmark.dynamo

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

class DynamoDbSelector {
    private val cache = mutableMapOf<Env, DynamoDbClient>()

    fun get(env: Env): DynamoDbClient {
        synchronized(cache) {
            val cached = cache[env]
            if (cached != null) {
                return cached
            }

            cache[env] = when (env) {
                Env.LOCAL -> DynamoDbClient.builder()
                    .region(Region.US_EAST_1)
                    .endpointOverride(URI.create("http://dynamodb-local:8000"))
                    .credentialsProvider(
                        StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                "dummy-key",
                                "dummy-secret"
                            )
                        )
                    )
                    .build()
                Env.PROD -> DynamoDbClient.builder()
                    .region(Region.US_EAST_1)
                    .build()
            }

            return cache[env]!!
        }
    }

    fun getEnhanced(env: Env): DynamoDbEnhancedClient {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(get(env))
            .build()
    }

    enum class Env {
        LOCAL,
        PROD
    }
}

fun String.toDynamoEnv(): DynamoDbSelector.Env {
    return when (this) {
        "Local" -> DynamoDbSelector.Env.LOCAL
        "Prod" -> DynamoDbSelector.Env.PROD
        else -> throw IllegalArgumentException("'$this' is not a valid stage")
    }
}
