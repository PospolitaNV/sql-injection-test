package com.github.npospolita.sqlinjectionproof.controller;

import com.github.npospolita.sqlinjectionproof.model.SearchExample;
import com.github.npospolita.sqlinjectionproof.model.SomeObject;
import com.github.npospolita.sqlinjectionproof.repo.SomeObjectRepository;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {

    private static final List<String> VALID_PARAMS = List.of("sort", "page", "size");
    private final SomeObjectRepository someObjectRepository;

    @GetMapping("/get")
    PagedModel<SomeObject> getObjects(HttpServletRequest request,
                                      Pageable page,
                                      PagedResourcesAssembler assembler) {

        Map<String, List<String>> paramsMap = splitQueryParameters(request);

        SearchExample searchExample = new SearchExample();

        for (Map.Entry<String, List<String>> parameter : paramsMap.entrySet()) {
            switch (parameter.getKey()) {
                case "name":
                    searchExample.setName(parameter.getValue().get(0));
                    break;
                default:
                    if (!VALID_PARAMS.contains(parameter.getKey())) {
                        log.error("No such parameter!");
                        throw new RuntimeException();
                    }
            }
        }

        Page<SomeObject> someObjects = someObjectRepository.findSomeObjects(searchExample, page);

        if (someObjects.isEmpty())
            return assembler.toEmptyModel(someObjects, SomeObject.class);
        else
            return assembler.toModel(someObjects);
    }

    private Map<String, List<String>> splitQueryParameters(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getQueryString())) {
            return Collections.emptyMap();
        }
        return Pattern.compile("&")
                .splitAsStream(URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8))
                .map(p -> Arrays.copyOf(p.split("=", 2), 2))
                .map(v -> ArrayUtils.addAll(Arrays.copyOf(v, 1), v[1].split(",")))
                .collect(Collectors.toMap(s -> s[0], s -> Arrays.asList(Arrays.copyOfRange(s, 1, s.length))));
    }

}
