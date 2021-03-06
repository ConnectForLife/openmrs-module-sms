package org.openmrs.module.sms.web.controller.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.SmsService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.util.TestUtil;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Verify IncomingController present & functional. */
@WebAppConfiguration
public class IncomingControllerBundleITTest extends BaseModuleWebContextSensitiveTest {

  private static final String CONFIG_NAME = "sample-it-config";

  private static final String MISSING_CONFIG_NAME = "missing-config";

  private static final String TEMPLATE_NAME = "Plivo";

  @Autowired
  @Qualifier("sms.configService")
  private ConfigService configService;

  @Autowired
  @Qualifier("sms.SmsService")
  private SmsService smsService;

  @Autowired
  @Qualifier("templateService")
  private TemplateService templateService;

  @Autowired private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  private Configs backupConfigs;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Before
  public void createConfigs() {
    templateService.loadTemplates();
    backupConfigs = configService.getConfigs();
    Config config = new Config();
    config.setName(CONFIG_NAME);
    config.setTemplateName(TEMPLATE_NAME);

    Configs configs = new Configs();
    configs.setConfigList(singletonList(config));
    configs.setDefaultConfigName(CONFIG_NAME);

    configService.updateConfigs(configs);
  }

  @After
  public void cleanUpDatabase() throws Exception {
    this.deleteAllData();
  }

  @After
  public void restoreConfigs() {
    configService.updateConfigs(backupConfigs);
  }

  @Test
  public void verifyControllerFunctional() {
    assertNotNull(smsService);
  }

  @Test
  public void handleIncomingControllerFunctionalMissingConfig() throws Exception {
    mockMvc
        .perform(
            post(String.format("/sms/incoming/%s", MISSING_CONFIG_NAME))
                .contentType(TestUtil.APPLICATION_JSON)
                .content(TestUtil.encodeString()))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().string(""));
  }

  @Test
  public void handleIncomingControllerFunctionalExistsConfig() throws Exception {
    prepareTemplate();

    mockMvc
        .perform(
            post(String.format("/sms/incoming/%s", CONFIG_NAME))
                .param("From", "testSender")
                .contentType(MediaType.parseMediaType(TestUtil.APPLICATION_JSON))
                .content(TestUtil.encodeString()))
        .andExpect(status().is(HttpStatus.OK.value()));
  }

  private void prepareTemplate() {
    Template template = templateService.getTemplate(TEMPLATE_NAME);
    template.getIncoming().setSenderRegex("(.*)");
  }
}
