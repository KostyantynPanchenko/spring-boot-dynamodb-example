package com.example.aws.dynamodb.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Getter
@Setter
@ToString
public class WorkData {

  private String id;
  private String date;
  private String description ;
  private String guide;
  private String username ;
  private String status ;
  private int archived;

  @DynamoDbPartitionKey
  public String getId() {
    return this.id;
  }

  @DynamoDbSortKey
  public String getUsername() {
    return this.username;
  }

}
