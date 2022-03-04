package org.trf.vend.vendapi;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.trf.vend.model.IFloat;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MachineAdminController.class)
class MachineAdminTest {

    @MockBean
    IFloat IFloat;

    @Autowired
    private MockMvc mvc;

    @Captor
    ArgumentCaptor<Map<Integer, Integer>> captor;

    @Test
    void viewKitty()  {

        Mockito.when(IFloat.status()).thenReturn(Map.of(1, 1, 50, 2));
        try {
            mvc.perform(
                    MockMvcRequestBuilders.get("/admin/kitty")
            ).andExpect(status().isOk())
                    .andExpect(jsonPath("$['1']", is(1)))
                    .andExpect(jsonPath("$['50']", is(2)));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void initKitty()  throws  Exception {

       try {
            Mockito.when(IFloat.status()).thenReturn(Map.of(50, 2, 2, 1, 1,5));
            mvc.perform(
                    MockMvcRequestBuilders.post("/admin/init?C50=2&C2=1&C1=5")
            ).andExpect(status().isOk())
                    .andExpect(jsonPath("$['1']", is(5)))
                    .andExpect(jsonPath("$['2']", is(1)))
                    .andExpect(jsonPath("$['50']", is(2)));

            Mockito.verify(IFloat).setBalance(captor.capture());
            Map<Integer, Integer> actual = captor.getValue();
            assertEquals(3, actual.size());
            assertEquals(2, actual.get(50));
            assertEquals(1, actual.get(2));
            assertEquals(5, actual.get(1));


       } catch (Exception e) {
            fail();
       }
    }

    @Test
    void initKittyWithBadCoins() {

        try {
           

            Mockito.when(IFloat.status()).thenReturn(Map.of());
            mvc.perform(
                    MockMvcRequestBuilders.post("/admin/init?C50=2&EUR10=1")
            ).andExpect(status().isBadRequest());

            Mockito.verify(IFloat,never()).setBalance(captor.capture());

        } catch (Exception e) {
            fail();
        }
    }
}
