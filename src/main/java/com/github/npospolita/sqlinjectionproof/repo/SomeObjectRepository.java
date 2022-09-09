package com.github.npospolita.sqlinjectionproof.repo;

import com.github.npospolita.sqlinjectionproof.model.SearchExample;
import com.github.npospolita.sqlinjectionproof.model.SomeObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SomeObjectRepository extends CrudRepository<SomeObject, Long> {

    @Query("Select so from SomeObject so " +
            "where lower(so.name) like lower(concat('%', :#{#searchExample.name} ,'%')) " +
            "order by so.name asc")
    Page<SomeObject> findSomeObjects(@Param("searchExample") SearchExample searchExample, Pageable page);
}
