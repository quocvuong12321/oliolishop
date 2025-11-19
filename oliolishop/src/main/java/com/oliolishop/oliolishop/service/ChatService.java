package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.agent.AgentRequest;
import com.oliolishop.oliolishop.dto.agent.ChatRequest;
import com.oliolishop.oliolishop.dto.agent.ChatResponse;
import com.oliolishop.oliolishop.dto.agent.HistoryChatResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.entity.HistoryChat;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.HistoryChatMapper;
import com.oliolishop.oliolishop.repository.HistoryChatRepository;
import com.oliolishop.oliolishop.util.AppUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
    private final HistoryChatRepository historyChatRepository;
    private final HistoryChatMapper historyChatMapper;
    private final String AGENT_HOST;
    private final RestTemplate restTemplate;

    private static final int MAX_MESSAGES_PER_SESSION = 100;
    private static final int MESSAGE_TO_DELETE = 30;
    public ChatService(HistoryChatRepository historyChatRepository, HistoryChatMapper historyChatMapper, @Value("${app.fast-api.agent-url}")String AGENT_HOST){
        this.historyChatRepository = historyChatRepository;
        this.historyChatMapper = historyChatMapper;
        this.AGENT_HOST = AGENT_HOST;
        restTemplate = AppUtils.createUnsafeRestTemplate();
    }

    public PaginatedResponse<HistoryChatResponse> getHistoryChat(int page, int size){

        String customerId = AppUtils.getCustomerIdByJwt();

        String sessionId = historyChatRepository.findSessionId(customerId);
        Pageable pageable = PageRequest.of(page,size);
        if(sessionId == null || sessionId.isEmpty()){

            HistoryChatResponse historyChat = createNewSession(customerId);

            return PaginatedResponse.fromSpringPage(new PageImpl<>(
                    List.of(historyChat),
                    pageable,
                    1 //total element
            ));
        }



        Page<HistoryChatResponse> historyChatResponses = (historyChatRepository
                .findBySessionIdOrderByCreatedAtAscIdAsc(sessionId, pageable)
                .map(historyChatMapper::toHistoryChatResponse));

        return PaginatedResponse.fromSpringPage(historyChatResponses);
    }

    @Transactional
    public ChatResponse responseChat(ChatRequest request){

        String customerId = AppUtils.getCustomerIdByJwt();

        boolean checkSession = historyChatRepository.existsBySessionIdAndCustomerId(request.getSessionId(),customerId);
        if(!checkSession)
            throw new AppException(ErrorCode.CHAT_SESSION_INVALID);

        HistoryChat chat = historyChatMapper.toHistoryChat(request);

        chat.setCustomerId(customerId);
        chat.setRole(HistoryChat.RoleChat.user);
        historyChatRepository.save(chat);

        AgentRequest agentRequest = AgentRequest.builder()
                .userId(customerId)
                .message(request.getMessage())
                .sessionId(request.getSessionId())
                .build();

        HttpEntity<AgentRequest> httpEntity = new HttpEntity<>(agentRequest);

        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(AGENT_HOST,httpEntity,ChatResponse.class);

        ChatResponse chatResponse = response.getBody();


        if(chatResponse !=null) {
            HistoryChat entityChatResponse = HistoryChat.builder()
                    .role(HistoryChat.RoleChat.assistant)
                    .message(chatResponse.getAssistantMessage())
                    .customerId(customerId)
                    .sessionId(request.getSessionId())
                    .build();
            historyChatRepository.save(entityChatResponse);
        }

        int numMessages = historyChatRepository.countBySessionId(request.getSessionId()).intValue();

        if(numMessages>=MAX_MESSAGES_PER_SESSION){
            historyChatRepository.cleanupOldMessage(request.getSessionId(),MESSAGE_TO_DELETE);
        }

        return chatResponse;
    }

    @Transactional
    private HistoryChatResponse createNewSession(String customerId){

        String MESSAGE_DEFAULT = "Xin chào";

        HistoryChat historyChat = HistoryChat.builder()
                .sessionId(UUID.randomUUID().toString())
                .customerId(customerId)
                .message(MESSAGE_DEFAULT)
                .role(HistoryChat.RoleChat.assistant)
                .build();


        return  historyChatMapper.toHistoryChatResponse(historyChatRepository.save(historyChat));
    }
    @Transactional
    public HistoryChatResponse newChat(){
        String customerId = AppUtils.getCustomerIdByJwt();

        String sessionId = historyChatRepository.findSessionId(customerId);

        if(sessionId != null){
            deleteSessionHistory(sessionId);
        }

        return  createNewSession(customerId);

    }

    private void deleteSessionHistory(String sessionId){

        historyChatRepository.deleteHistoryChat(sessionId);
    }

}
