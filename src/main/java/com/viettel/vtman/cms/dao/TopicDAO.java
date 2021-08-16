package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.entity.Topic;

import java.util.List;

public interface TopicDAO {
    List<TopicDto> searchByName(String topicName, Long departmentId, ObjectResult objectResult);

    List<Topic> findAll();

    List<Topic> findAllNotCheckStatus();

    List<Topic> findAllWithNotNullAnsEmpId();

    void deleteById(Long topicId);

    Topic findById(Long topicId);

    TopicDto findByName(String topicName);

    Topic insert(Topic topic);

    Topic updateTopic(Topic topic);

    TopicDto findByCode(String topicCode);

    TopicDto findId(Long topicId);
    List<TopicDto> findAllByStatus();
    List<TopicDto> getForwardTopic(Long topicId);
    List<Topic> findByEmployeeId(Long employeeId);

    List<Topic> searchByDto(TopicDto dto);
}
