package org.trf.vend.vendapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trf.vend.model.Denominations;
import org.trf.vend.model.IFloat;
import org.trf.vend.model.Kitty;
import org.trf.vend.model.UnrecognisedDenominationException;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MachineAdminController {

    @Autowired
    IFloat kitty;

    @RequestMapping(value = "/admin/init", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String initReadme() {
        StringBuilder sb = new StringBuilder();
        sb.append("Calling this URI with POST request will initialise the machine kitty with a float defined by the payload\n");
        sb.append("POST expects querystring where each field is a coin denomination, field value is an integer 0..m indicating the number of coins of that denomination.\n");
        sb.append("POST without params will initialise an empty kitty. Absence of a coin denomination implicitly sets count to 0\n");
        sb.append("Repeat POST requests are not additive, but will replace state (caution)\n");
        sb.append("Permissible values:\n");
        Arrays.stream(Denominations.values()).forEach(c -> sb.append(c.name() + "\n"));
        return sb.toString();
    }

    @RequestMapping(value = "/admin/init", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Integer, Integer>> initKitty(@RequestParam Map<String, String> startcoins) throws UnrecognisedDenominationException {

        if (startcoins.size() != startcoins.keySet().stream().filter(Denominations::isValid).count()) {
            return ResponseEntity.badRequest().body(kitty.status());
        }

        Map<Integer, Integer> startfloat = startcoins.entrySet()
                .stream()
                .map(e -> new AbstractMap.SimpleEntry<Integer, Integer>(Denominations.valueOf(e.getKey()).getCoinvalue(), Integer.valueOf(e.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        kitty.setBalance(startfloat);
        return ResponseEntity.ok().body(kitty.status());
    }

    @RequestMapping(value = "/admin/kitty", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Integer, Integer>> kittyStatus() {

        return ResponseEntity.ok().body(this.kitty.status());
    }
}
