package idealworld.dew.saas.service.ident;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
* The type Dew sdk gen test.
*
* You can customize (do not change the file name and file path) this file to fit your project.
*
* @author gudaoxuri
*/
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DewSDKGenTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
    * Open id generate.
    *
    * @throws IOException the io exception
    */
    @Test
    public void openIdGenerate() throws IOException {
        var openAPIJson = restTemplate.getForObject("/v3/api-docs", String.class);
        var targetPath = new File(DewSDKGenTest.class.getResource("/").getPath()).getParent() + File.separator + "dew_sdkgen";
        Files.createDirectories(Paths.get(targetPath));
        Files.write(Paths.get(targetPath + File.separator + "openapi.json"), openAPIJson.getBytes(StandardCharsets.UTF_8));
    }

}