package se.vgregion.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class TestSignController {

    @RequestMapping(value = "/verifyData", method = RequestMethod.GET)
    public String verifyData() {
        return "verifyData";
    }

    @RequestMapping(value = "/sign", method = RequestMethod.GET)
    public String sign() {
        return "signData";
    }

    @RequestMapping(value = "/saveSignature", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.MOVED_TEMPORARILY)
    public void postback(HttpServletResponse response,
            @RequestParam(value = "signature", required = false) String signature) {
        System.out.println("Signature: " + signature);
        response.setHeader("Location",
                "http://localhost:8080/signer-service-core-bc-module-test-web-client/showStatus?id=44");
    }

    @RequestMapping(value = "/showStatus", method = RequestMethod.GET)
    public void showStatus(@RequestParam String id, HttpServletResponse response) throws IOException {
        // Request parameters

        PrintWriter writer = response.getWriter();

        writer.write("<html>");
        writer.write("<head>");
        writer.write("<title></title>");
        writer.write("</head>");
        writer.write("<body>");
        writer.write("<h1>Successfully saved signature with id: " + id + "</h1>");
        writer.write("</body>");
        writer.write("</html>");

    }

}
