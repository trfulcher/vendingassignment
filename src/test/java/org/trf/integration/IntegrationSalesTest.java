package org.trf.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.trf.vend.vendapi.VendapiApplication;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { VendapiApplication.class })
@WebAppConfiguration
@Tag("integration")
class IntegrationSalesTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @BeforeEach
    public void setup()  {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void example() {

        try {

            mvc.perform(
                    MockMvcRequestBuilders.post("/admin/init?C50=2&C2=1&C1=5")
            ).andExpect(status().isOk())
                    .andExpect(jsonPath("$['1']", is(5)))
                    .andExpect(jsonPath("$['2']", is(1)))
                    .andExpect(jsonPath("$['50']", is(2)));


            mvc.perform(MockMvcRequestBuilders.post("/addFunds/?coin=C50"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['coins']['50']", is(1)));

            mvc.perform(MockMvcRequestBuilders.post("/addFunds/?coin=C5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['coins']['5']", is(1)));

            mvc.perform(MockMvcRequestBuilders.post("/addFunds/?coin=EUR10"))
                    .andExpect(status().isBadRequest());

            mvc.perform(MockMvcRequestBuilders.post("/makesale/53"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['success']", is(true)))
                    .andExpect(jsonPath("$['change']['2']", is(1)));

            mvc.perform(
                    MockMvcRequestBuilders.get("/admin/kitty")
            ).andExpect(status().isOk())
                    .andExpect(jsonPath("$['1']", is(5)))
                    .andExpect(jsonPath("$['5']", is(1)))
                    .andExpect(jsonPath("$['50']", is(3)));

        } catch (Exception e) {
            fail();
        }

    }


}
