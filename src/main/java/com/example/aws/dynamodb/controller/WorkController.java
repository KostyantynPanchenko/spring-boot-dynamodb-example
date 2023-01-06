package com.example.aws.dynamodb.controller;

import com.example.aws.dynamodb.model.WorkItem;
import com.example.aws.dynamodb.service.DynamoDbEnhancedService;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/work/item")
@RequiredArgsConstructor
class WorkController {

  private final DynamoDbEnhancedService service;

  @GetMapping
  public List<WorkItem> getItems(@RequestParam(required = false) final String archived) {
    Iterable<WorkItem> result;
    if (archived != null && archived.compareTo("false") == 0) {
      result = service.getOpenItems();
    } else if (archived != null && archived.compareTo("true") == 0) {
      result = service.getClosedItems();
    } else {
      result = service.getAllItems();
    }

    return StreamSupport.stream(result.spliterator(), false).toList();
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("{id}:{name}:archive")
  public String archiveWorkItem(@PathVariable final String id, @PathVariable final String name) {
    service.archiveItem(id, name);
    return "Item with id " + id + " was archived";
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public List<WorkItem> addItem(@RequestBody final Map<String, String> payload) {
    final WorkItem item = new WorkItem();
        item.setGuide(payload.get("guide"));
    item.setDescription(payload.get("description"));
    item.setName(payload.get("name"));
    item.setStatus(payload.get("status"));
    item.setArchived(0);
    service.saveItem(item);
    Iterable<WorkItem> result = service.getOpenItems();
    return StreamSupport.stream(result.spliterator(), false).toList();
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("{id}:{name}:delete")
  public String deleteWorkItem(@PathVariable final String id, @PathVariable final String name) {
    service.deleteItem(id, name);
    return "Item with id " + id + " and username " + name + " deleted";
  }
}
