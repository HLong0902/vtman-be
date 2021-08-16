package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.QuestionDefinitionDAO;
import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.QuestionDefinitionDTO;
import com.viettel.vtman.cms.entity.QuestionDefinition;
import com.viettel.vtman.cms.service.QuestionDefinitionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class QuestionDefinitionServiceImpl implements QuestionDefinitionService {

    @Autowired
    private QuestionDefinitionDAO questionDefinitionDAO;

    @Override
    public List<QuestionDefinitionDTO> search(String questionDefinitionName, String answerDefinition, Long topicId, ObjectResult objectResult) {
        return questionDefinitionDAO.search(questionDefinitionName, answerDefinition,topicId,objectResult);
    }

    @Override
    public QuestionDefinition save(QuestionDefinition questionDefinition) {
        return questionDefinitionDAO.insert(questionDefinition);
    }

    @Override
    public QuestionDefinition update(QuestionDefinition questionDefinition) {
        return questionDefinitionDAO.updateQuestion(questionDefinition);
    }

    @Override
    public List<QuestionDefinition> importExel(List<QuestionDefinitionDTO> lisData) {
        return questionDefinitionDAO.importExcel(lisData);
    }

    @Override
    public String exportExcel(List<QuestionDefinitionDTO> questionDefinitionDTO) {
        return questionDefinitionDAO.exportExcel(questionDefinitionDTO);
    }

    @Override
    public List<QuestionDefinition> getQuestionsByTopicId(Long topicId) {
        return questionDefinitionDAO.getQuestionsByTopicId(topicId);
    }


    @Override
    public QuestionDefinition findById(Long questionDefinitionId) {
        return questionDefinitionDAO.findById(questionDefinitionId);
    }

    @Override
    public void deleteById(Long questionDefinitionId) {
        questionDefinitionDAO.deleteById(questionDefinitionId);
    }

    @Override
    public List<QuestionDefinition> getSuggestion (String questionDefinitionName){
        questionDefinitionName = StringUtils.normalizeSpace(questionDefinitionName);
        List<String> lstText = Pattern.compile(StringUtils.SPACE).splitAsStream(questionDefinitionName).collect(Collectors.toList());
//        String text = lstText.stream().limit(3).collect(Collectors.joining(StringUtils.SPACE));
//        return questionDefinitionDAO.getSuggestion(text);
        final AtomicInteger counter = new AtomicInteger();
        Collection<List<String>> tempCollections = lstText.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 2)).values();
        List<String> keySearches = new ArrayList<>();
        tempCollections.forEach(item -> {
            String temp = String.join(StringUtils.SPACE, item);
            keySearches.add(temp);
        });
        List<QuestionDefinition> result = new ArrayList<>();
        keySearches.forEach(txt -> {
            List<QuestionDefinition> lstQuestion = questionDefinitionDAO.getSuggestion(txt);
            result.addAll(lstQuestion);
        });
        Set<Long> idSet = new HashSet<>();
        result.removeIf(p -> !idSet.add(p.getQuestionDefinitionId()));
        result.stream().sorted(Comparator.comparing(QuestionDefinition::getNumberOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(QuestionDefinition::getQuestionDefinitionName)).collect(Collectors.toList());
        return result;
    }

    @Override
    public Long countCheckMax() {
        return questionDefinitionDAO.countMax();
    }

    @Override
    public QuestionDefinitionDTO checkNumberOrder(Long numberOrder) {
        return questionDefinitionDAO.checkNumberOrder(numberOrder);
    }

    @Override
    public QuestionDefinitionDTO checkQuestionDefinitionName(QuestionDefinitionDTO questionDefinitionDTO) {
        return questionDefinitionDAO.checkQuestionDefinitionName(questionDefinitionDTO);
    }

    @Override
    public List<QuestionDefinitionDTO> exportQuestion(String questionDefinitionName, String answerDefinition, Long topicId) {
        return questionDefinitionDAO.exportQuestion(questionDefinitionName, answerDefinition, topicId);
    }

    @Override
    public List<QuestionDefinitionDTO> countTopicId(Long topicId) {
        return questionDefinitionDAO.countTopicId(topicId);
    }

}
