package com.example.aws.dynamodb.service;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import com.example.aws.dynamodb.model.WorkData;
import com.example.aws.dynamodb.model.WorkItem;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamoDbEnhancedService {

  private final DynamoDbEnhancedClient enhancedClient;

  public List<WorkItem> getAllItems() {
    try {
      DynamoDbTable<WorkData> table = enhancedClient.table("Work",
          TableSchema.fromBean(WorkData.class));
      return parseItems(table.scan().items().iterator());
    } catch (final DynamoDbException exc) {
      log.error(exc.getMessage());
    }
    return null;
  }

  private static List<WorkItem> parseItems(Iterator<WorkData> results) {
    List<WorkItem> itemList = new ArrayList<>();

    while (results.hasNext()) {
      final var workItem = new WorkItem();
      final var work = results.next();
      workItem.setName(work.getUsername());
      workItem.setGuide(work.getGuide());
      workItem.setDescription(work.getDescription());
      workItem.setStatus(work.getStatus());
      workItem.setDate(work.getDate());
      workItem.setId(work.getId());
      workItem.setArchived(work.getArchived());
      itemList.add(workItem);
    }
    return itemList;
  }

  // Archives an item based on the key.
  public void archiveItem(final String id, final String name) {
    try {
      DynamoDbTable<WorkData> workTable = enhancedClient.table("Work",
          TableSchema.fromBean(WorkData.class));

      //Get the Key object.
      Key key = Key.builder()
          .partitionValue(id)
          .sortValue(name)
          .build();

      // Get the item by using the key.
      WorkData work = workTable.getItem(r -> r.key(key));
      work.setArchived(1);
      log.info("Archiving item {}.", id);
      workTable.updateItem(r -> r.item(work));
      log.info("Archived item {}.", id);
    } catch (final DynamoDbException exc) {
      log.error("Failed to archive item {}. " + exc.getMessage(), id);
    }
  }

  public List<WorkItem> getOpenItems() {
    log.info("Fetching active items...");
    return getItems("0");
  }

  public List<WorkItem> getClosedItems() {
    log.info("Fetching archived items...");
    return getItems("1");
  }

  public List<WorkItem> getItems(final String closed) {
    try {
      // Create a DynamoDbTable object.
      DynamoDbTable<WorkData> table = enhancedClient.table("Work",
          TableSchema.fromBean(WorkData.class));
      AttributeValue attr = builder()
          .n(closed)
          .build();

      Map<String, AttributeValue> myMap = new HashMap<>();
      myMap.put(":val1", attr);
      Map<String, String> myExMap = new HashMap<>();
      myExMap.put("#archived", "archived");

      // Set the Expression so only Closed items are queried from the Work table.
      Expression expression = Expression.builder()
          .expressionValues(myMap)
          .expressionNames(myExMap)
          .expression("#archived = :val1")
          .build();

      ScanEnhancedRequest enhancedRequest = ScanEnhancedRequest.builder()
          .filterExpression(expression)
          .limit(15)
          .build();

      // Get items.
      Iterator<WorkData> results = table.scan(enhancedRequest).items().iterator();
      return parseItems(results);

    } catch (final DynamoDbException exc) {
      log.error(exc.getMessage());
    }
    return null;
  }

  public void saveItem(final WorkItem item) {
    try {
      DynamoDbTable<WorkData> workTable = enhancedClient.table("Work",
          TableSchema.fromBean(WorkData.class));
      WorkData record = new WorkData();
      record.setUsername(item.getName());
      record.setId(UUID.randomUUID().toString());
      record.setDescription(item.getDescription());
      record.setDate(now());
      record.setStatus(item.getStatus());
      record.setArchived(0);
      record.setGuide(item.getGuide());
      log.info("Saving {}.", record);
      workTable.putItem(record);
      log.info("Saved {}.", record);
    } catch (final DynamoDbException exc) {
      log.error(exc.getMessage());
      System.exit(1);
    }
  }

  private static String now() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  public void deleteItem(final String id, final String name) {
    try {
      Key key = Key.builder()
          .partitionValue(id)
          .sortValue(name)
          .build();

      DynamoDbTable<WorkData> workTable = enhancedClient.table("Work",
          TableSchema.fromBean(WorkData.class));

      log.info("Deleting item with id {} and username {}", id, name);
      workTable.deleteItem(key);
      log.info("Deleted item with id {} and username {}", id, name);
    } catch (final DynamoDbException exc) {
      log.error("Could not delete item with id {} and username {}" + exc.getMessage(), id, name);
    }
  }
}
