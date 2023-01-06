package com.example.aws.dynamodb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkItem {

  private String id;
  private String name;
  private String guide ;
  private String date;
  private String description;
  private String status;
  private int archived;

}
