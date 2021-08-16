package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.QuestionDefinitionDTO;
import com.viettel.vtman.cms.entity.QuestionDefinition;

import java.util.List;

public interface QuestionDefinitionDAO {
    List<QuestionDefinitionDTO> search(String questionDefinitionName, String answerDefinition,Long topicId, ObjectResult objectResult);
    QuestionDefinition findById(Long questionDefinitionId);
     void deleteById(Long questionDefinitionId);
    QuestionDefinition insert(QuestionDefinition questionDefinition);
    QuestionDefinition updateQuestion(QuestionDefinition questionDefinition);
    List<QuestionDefinition> importExcel (List<QuestionDefinitionDTO> questionDefinitions);
    String exportExcel(List<QuestionDefinitionDTO> questionDefinitionDTO);
    List<QuestionDefinition> getQuestionsByTopicId(Long topicId);
    List<QuestionDefinition> getSuggestion (String questionDefinitionName);
    QuestionDefinitionDTO checkNumberOrder(Long numberOder);
    QuestionDefinitionDTO checkQuestionDefinitionName(QuestionDefinitionDTO questionDefinitionDTO);
    Long countMax();
    List<QuestionDefinitionDTO> exportQuestion(String questionDefinitionName, String answerDefinition,Long topicId);
    List<QuestionDefinitionDTO> countTopicId(Long topicId);

}
