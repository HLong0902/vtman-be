package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.QuestionDefinitionDTO;
import com.viettel.vtman.cms.entity.QuestionDefinition;

import java.util.List;

public interface QuestionDefinitionService {
    List<QuestionDefinitionDTO> search(String questionDefinitionName, String answerDefinition, Long topicId, ObjectResult objectResult);

    QuestionDefinition findById(Long questionDefinitionId);

    void deleteById(Long questionDefinitionId);

    QuestionDefinition save(QuestionDefinition questionDefinition);

    QuestionDefinition update(QuestionDefinition questionDefinition);

    List<QuestionDefinition> importExel(List<QuestionDefinitionDTO> lisData);

    String exportExcel(List<QuestionDefinitionDTO> questionDefinitionDTO);

    List<QuestionDefinition> getQuestionsByTopicId(Long topicId);

    List<QuestionDefinition> getSuggestion (String questionDefinitionName);
    Long countCheckMax();
    QuestionDefinitionDTO checkNumberOrder(Long numberOrder);
    QuestionDefinitionDTO checkQuestionDefinitionName(QuestionDefinitionDTO questionDefinitionDTO);
    List<QuestionDefinitionDTO> exportQuestion(String questionDefinitionName, String answerDefinition, Long topicId);
    List<QuestionDefinitionDTO> countTopicId(Long topicId);
}