package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.agent.ChatRequest;
import com.oliolishop.oliolishop.dto.agent.ChatResponse;
import com.oliolishop.oliolishop.dto.agent.HistoryChatResponse;
import com.oliolishop.oliolishop.entity.HistoryChat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface HistoryChatMapper {

    HistoryChat toHistoryChat(ChatRequest chatRequest);


    HistoryChatResponse toHistoryChatResponse (HistoryChat historyChat);

}
