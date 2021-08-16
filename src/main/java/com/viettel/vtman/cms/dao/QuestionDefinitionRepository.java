package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.entity.QuestionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionDefinitionRepository extends JpaRepository<QuestionDefinition, Long> {
    QuestionDefinition save(QuestionDefinition questionDefinitionDTO);

}
