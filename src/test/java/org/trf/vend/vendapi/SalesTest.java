package org.trf.vend.vendapi;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.trf.vend.model.*;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SalesController.class)
public class SalesTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    ISalesTransaction sts;

    @MockBean
    SalesHolder holder;

    @Test
    public void addCoins() {

        IOfferedAmount oa = OfferedAmount.newInstance();
        Mockito.when(holder.getOa()).thenReturn(oa);

        try {
            mvc.perform(MockMvcRequestBuilders.post("/addFunds/?coin=C50"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['coins']['50']", is(1)));
            assertEquals(50, oa.currentTotal());

            mvc.perform(MockMvcRequestBuilders.post("/addFunds/?coin=C5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['coins']['5']", is(1)));
            assertEquals(55, oa.currentTotal());

            mvc.perform(MockMvcRequestBuilders.post("/addFunds/?coin=EUR10"))
                    .andExpect(status().isBadRequest());
            assertEquals(55, oa.currentTotal());

        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void makeSale() {

        try {
            IOfferedAmount oa = OfferedAmount.newInstance();
            oa.addDenomination("C50");
            oa.addDenomination("C10");
            oa.addDenomination("C20");
            Mockito.when(holder.getOa()).thenReturn(oa);

            TransactionResult r = new TransactionResult("success");
            r.setSuccess(true);
            r.setChange(Map.of(5, 1, 2, 2));

            Mockito.when(sts.execute(71, oa)).thenReturn(r);

            mvc.perform(MockMvcRequestBuilders.post("/makesale/71"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['success']", is(true)))
                    .andExpect(jsonPath("$['change']['2']", is(2)))
                    .andExpect(jsonPath("$['change']['5']", is(1)));

            Mockito.verify(holder, times(1)).reset();

        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void insufficientFunds() {

        IOfferedAmount oa = OfferedAmount.newInstance();
        Mockito.when(holder.getOa()).thenReturn(oa);

        try {
            oa.addDenomination("C50");
            oa.addDenomination("C10");

            Mockito.when(sts.execute(70, oa)).thenThrow(new InsufficientFundsException("bad"));

            mvc.perform(MockMvcRequestBuilders.post("/makesale/70"))
                    .andExpect(status().isBadRequest());

            TransactionResult r = new TransactionResult("success");
            r.setSuccess(true);
            r.setChange(Map.of(10, 1));


        } catch (InsufficientFundsException e) {
            fail(); // should have been caught by controller
        } catch (UnrecognisedDenominationException e) {
            fail();
        } catch (Exception e) {
            fail();
        }

    }

}
