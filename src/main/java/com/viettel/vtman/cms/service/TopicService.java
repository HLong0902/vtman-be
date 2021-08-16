package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.entity.Topic;

import java.util.List;

public interface TopicService {
    List<Topic> findAll();
    List<Topic> findAllWithNotNullAnsEmpId();
    List<TopicDto> searchByName(String topicName, Long departmentId, ObjectResult objectResult);
    void deleteById(Long topicId);
    Topic findById(Long topicId);
    Topic save(Topic topic);
    Topic update(Topic topic);
    TopicDto findByName(String topicName);
    TopicDto findByCode(String topicCode);
    TopicDto findId(Long topicId);
    List<TopicDto> findAllByStatus();
    List<TopicDto> getForwardTopic(Long topicId);
}
