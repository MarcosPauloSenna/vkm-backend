package com.vkm_backend.infra.global.specification.web;

import com.vkm_backend.infra.global.specification.params.DynamicArgs;
import com.vkm_backend.infra.global.specification.params.DynamicArgsConverter;
import com.vkm_backend.infra.global.specification.params.annotation.DynamicParam;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class QueryArgsHandlerMethodResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(DynamicParam.class) &&
                parameter.getParameterType().equals(DynamicArgs.class);
    }

    @NonNull
    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        var annotation = parameter.getParameterAnnotation(DynamicParam.class);
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        var args = DynamicArgsConverter.converter(request, annotation);
        args.validate(annotation);
        return args;
    }
}
