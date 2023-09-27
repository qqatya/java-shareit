package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDto dto, Long ownerId) {
        return post("", ownerId, dto);
    }

    public ResponseEntity<Object> updateItem(Long ownerId, Long id, ItemDto dto) {
        return patch("/" + id, ownerId, dto);
    }

    public ResponseEntity<Object> getItemById(Long id, Long ownerId) {
        return get("/" + id, ownerId);
    }

    public ResponseEntity<Object> getItems(Long ownerId, Integer size, Integer from) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> searchItems(String text, Integer size, Integer from) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> createComment(CommentDto dto, Long itemId, Long authorId) {
        return post("/" + itemId + "/comment", authorId, dto);
    }
}
