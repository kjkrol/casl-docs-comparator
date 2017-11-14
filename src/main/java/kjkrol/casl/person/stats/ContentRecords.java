package kjkrol.casl.person.stats;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static java.util.Arrays.asList;
import static pl.dk.cdocs.model.ContentTypes.PERSON;
import static pl.dk.cdocs.model.ContentTypes.PERSON_CMR;
import static pl.dk.cdocs.model.ContentTypes.PERSON_CONTEXT;
import static pl.dk.cdocs.model.ContentTypes.PERSON_CONTEXT_CMR;
import static pl.dk.cdocs.model.DocumentProperties.CONTENT_TYPE;
import static pl.dk.cdocs.model.DocumentProperties.ID;
import static pl.dk.cdocs.model.DocumentProperties.LINKS;
import static pl.dk.cdocs.model.DocumentProperties.MASTER_LINK;
import static pl.dk.cdocs.model.DocumentProperties.PERSONS;

@Service
class ContentRecords {

    @Value("${spring.data.mongodb.database}")
    @Getter
    private String databaseName;

    private MongoDatabase mongoDatabase;

    private String contentRecordsCollectionName = "mtvi_content_record";

    private final MongoClient mongoClient;

    ContentRecords(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @PostConstruct
    void getMongoDatabase() {
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    @PreDestroy
    void destroy() {
        mongoClient.close();
    }

    MongoCollection<Document> contentRecords() {
        return mongoDatabase.getCollection(contentRecordsCollectionName);
    }

    Document findContent(UUID id) {
        return contentRecords()
                .find(eq(ID, id))
                .first();
    }

    FindIterable<Document> findByLinks(UUID id) {
        return contentRecords()
                .find(eq(LINKS, id));
    }

    FindIterable<Document> findByLinksAndContentType(UUID id, String contentType) {
        return contentRecords()
                .find(and(
                        eq(CONTENT_TYPE, contentType),
                        eq(LINKS, id)
                ));
    }

    FindIterable<Document> findCmrs(UUID id) {
        return contentRecords().find(eq(MASTER_LINK, id));
    }

    MongoCursor<Document> findAllPersonsIds() {
        return contentRecords()
                .find(eq(CONTENT_TYPE, PERSON))
                .projection(fields(include(ID)))
                .iterator();
    }

    MongoCursor<Document> cursorOfContentTypeByIds(List<UUID> ids) {
        return contentRecords()
                .find(in(ID, ids))
                .projection(fields(
                        include(ID),
                        include(CONTENT_TYPE)
                ))
                .iterator();
    }

    MongoCursor<Document> cursorOfCountedPersonContexts() {
        List<Bson> aggregation = asList(
                match(Filters.eq(CONTENT_TYPE, PERSON_CONTEXT)),
                project(fields(
                        include(ID),
                        include(PERSONS)
                )),
                limit(1000000),
                group("$" + PERSONS, sum("count", 1)),
                sort(new Document("count", -1))
        );
        return executeAggregateAndGetCursor(aggregation);
    }

    MongoCursor<Document> cursorOfCountedPersonCmrs() {
        List<Bson> aggregation = asList(
                match(Filters.eq(CONTENT_TYPE, PERSON_CMR)),
                project(fields(
                        include(ID),
                        include(MASTER_LINK)
                )),
                limit(1000000),
                group("$" + MASTER_LINK, sum("count", 1)),
                sort(new Document("count", -1))
        );
        return executeAggregateAndGetCursor(aggregation);
    }

    MongoCursor<Document> cursorOfCountedPersonContextCmrs() {
        List<Bson> aggregation = asList(
                match(Filters.eq(CONTENT_TYPE, PERSON_CONTEXT_CMR)),
                project(fields(
                        include(ID),
                        include(MASTER_LINK)
                )),
                limit(1000000),
                group("$" + MASTER_LINK, sum("count", 1)),
                sort(new Document("count", -1))
        );
        return executeAggregateAndGetCursor(aggregation);
    }

    private MongoCursor<Document> executeAggregateAndGetCursor(List<Bson> aggregation) {
        return contentRecords()
                .aggregate(aggregation)
                .allowDiskUse(true)
                .iterator();
    }

}
