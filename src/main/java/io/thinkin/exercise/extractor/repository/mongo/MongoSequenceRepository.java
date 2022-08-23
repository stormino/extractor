package io.thinkin.exercise.extractor.repository.mongo;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import io.thinkin.exercise.extractor.entities.mongo.SensorValueEntity;
import io.thinkin.exercise.extractor.entities.mongo.SequenceEntity;
import io.thinkin.exercise.extractor.repository.SequenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class MongoSequenceRepository implements SequenceRepository {

    @Autowired
    MongoClient mongoClient;

    @Autowired
    MongoOperations mongoOperations;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    private MongoCollection<Document> collection() {

        return mongoClient.getDatabase(databaseName).getCollection(mongoOperations.getCollectionName(SequenceEntity.class));
    }

    private MongoCollection<SequenceEntity> typedCollection() {

        return mongoClient.getDatabase(databaseName)
                .getCollection(mongoOperations.getCollectionName(SequenceEntity.class), SequenceEntity.class);
    }

    @Override
    public Optional<SequenceEntity> getLastSequenceBySensorId(Integer sensorId) {

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
                .causallyConsistent(true)
                .build();
        try (ClientSession session = mongoClient.startSession(sessionOptions)) {
            return mongoOperations.withSession(() -> session).execute(action ->
                    Optional.ofNullable(typedCollection()
                            .find(Filters.eq("sensorId", sensorId))
                            .sort(Sorts.descending("values.timestamp"))
                            .limit(1)
                            .first()
                    )
            );
        }
    }

    @Override
    public void addValueToMostRecentSequence(Integer sensorId, SensorValueEntity value) {

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
                .causallyConsistent(true)
                .build();
        try (ClientSession session = mongoClient.startSession(sessionOptions)) {
            mongoOperations.withSession(() -> session).execute(action -> {
                Document document = collection()
                        .find(Filters.eq("sensorId", sensorId))
                        .sort(Sorts.descending("values.timestamp"))
                        .limit(1)
                        .first();
                Optional.ofNullable(document)
                        .ifPresent(d -> collection().updateOne(d, Updates.push("values", value)));
                return null;
            });
        }
    }

    @Override
    public void startNewSequence(Integer sensorId, SensorValueEntity value) {

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
                .causallyConsistent(true)
                .build();
        try (ClientSession session = mongoClient.startSession(sessionOptions)) {
            mongoOperations.withSession(() -> session).execute(action ->
                    typedCollection().insertOne(
                            SequenceEntity.builder()
                                    .withSensorId(sensorId)
                                    .withValues(Collections.singletonList(value))
                                    .build()
                    ));
        }
    }

    @Override
    public List<SequenceEntity> retrieveOverlappingSequences(Integer sensorId, Date dateFrom, Date dateTo) {

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
                .causallyConsistent(true)
                .build();
        try (ClientSession session = mongoClient.startSession(sessionOptions)) {
            return mongoOperations.withSession(() -> session).execute(action ->
                    typedCollection()
                            .find(
                                    Filters.and(
                                            Optional.ofNullable(sensorId)
                                                    .map(id -> Filters.eq("sensorId", id))
                                                    .orElse(Filters.empty()),
                                            Filters.gte("values.timestamp", dateFrom),
                                            Filters.lt("values.timestamp", dateTo)
                                    )
                            ).into(new ArrayList<>())
            );
        }
    }

    @Override
    public SequenceEntity retrieveFirstLongestSequence() {

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
                .causallyConsistent(true)
                .build();
        try (ClientSession session = mongoClient.startSession(sessionOptions)) {
            return mongoOperations.withSession(() -> session).execute(action ->
                    typedCollection().aggregate(Arrays.asList(
                            Aggregates.addFields(new Field<>("valuesCount", new Document("$size", "$values"))),
                            Aggregates.sort(Sorts.descending("valuesCount"))
                    )).first()
            );
        }
    }
}
