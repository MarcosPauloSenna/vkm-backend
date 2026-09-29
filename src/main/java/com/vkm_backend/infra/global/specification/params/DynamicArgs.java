package com.vkm_backend.infra.global.specification.params;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vkm_backend.infra.global.specification.enums.Conditional;
import com.vkm_backend.infra.global.specification.exception.DynamicParamValidationException;
import com.vkm_backend.infra.global.specification.params.annotation.DynamicParam;
import com.vkm_backend.infra.global.specification.params.deserialize.ListDeserializer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * QueryString
 * @apiNote Object that must be received in the Controller Annotated as  {@link RequestParam @Request param} to receive querystring
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class DynamicArgs {

    @NonNull
    private Map<String, Pair<Object, String>> value;
    /**
     * Enables or disables permission to make complete queries via URL, for example allowing the user to accept parameters AND, OR, EQ, BT, etc.
     */
    @Setter
    private boolean search;

    @Setter
    private Class<?> type;

    public <T> T toObj(Class<T> type) {
        Map<String, Object> map = this.value()
            .entrySet().stream()
            .map(e -> Map.entry(e.getKey(), e.getValue().getFirst()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Object.class, new ListDeserializer());
        return mapper.convertValue(map, type);
    }

    public static DynamicArgs instance() {
        return new DynamicArgs(new HashMap<>());
    }

    public DynamicArgs withParams(String paramName, Object value, Conditional conditional) {
        this.value.put(paramName, Pair.of(value, conditional.name()));
        return this;
    }

    public DynamicArgs withParams(String paramName, Object value) {
        this.value.put(paramName, Pair.of(value, ""));
        return this;
    }

    public void validate(DynamicParam dynamicParam) throws DynamicParamValidationException {
        var error = Arrays.stream(dynamicParam.mandatory())
                .filter(mandatory -> !this.value.containsKey(mandatory))
                .map(mandatory-> "DynamicArgs parameter '" + mandatory + "' is mandatory. ")
                .collect(Collectors.joining());
        if(!error.isEmpty()) {
            throw new DynamicParamValidationException(error,getClass());
        }
    }
}

