package com.moskalenko.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moskalenko.api.beans.Counterparty;
import com.moskalenko.api.beans.CounterpartyBuilder;
import com.moskalenko.api.requests.CounterpartyDescriptionRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CounterpartyControllerIT {

    @Autowired
    private MockMvc mvc;

    @Test
    public void createCounterparty() throws Exception {
        CounterpartyDescriptionRequest counterpartyDescriptionRequest = new CounterpartyDescriptionRequest();
        counterpartyDescriptionRequest.setDescription("Google");

        mvc.perform(MockMvcRequestBuilders
                .post("/counterparty")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(counterpartyDescriptionRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    void getCounterparties() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/counterparty")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    private Counterparty generateCounterparty() {
        return CounterpartyBuilder.create()
                .withId(1L)
                .withDescription("Apple")
                .withVersion(3L)
                .build();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}