package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.agent.ChatRequest;
import com.oliolishop.oliolishop.dto.agent.ChatResponse;
import com.oliolishop.oliolishop.dto.agent.HistoryChatResponse;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.Agent.ROOT)
public class AgentController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public ApiResponse<PaginatedResponse<HistoryChatResponse>> getHistoryChat(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ){

        return ApiResponse.<PaginatedResponse<HistoryChatResponse>>builder()
                .result(chatService.getHistoryChat(page,size))
                .build();
    }

    @PostMapping
    public ApiResponse<ChatResponse> chatResponse(@RequestBody ChatRequest request){

        return ApiResponse.<ChatResponse>builder()
                .result(chatService.responseChat(request))
                .build();
    }

    @PostMapping(ApiPath.Agent.CLEAN)
    public ApiResponse<HistoryChatResponse> newChat(){
        return ApiResponse.<HistoryChatResponse>builder()
                .result(chatService.newChat())
                .build();
    }



}
