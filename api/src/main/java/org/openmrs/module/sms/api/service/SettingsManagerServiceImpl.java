package org.openmrs.module.sms.api.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.module.sms.api.util.SMSConstants;
import org.openmrs.module.sms.api.util.ResourceUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.core.io.ByteArrayResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SettingsManagerServiceImpl extends BaseOpenmrsService
    implements SettingsManagerService {

  @Override
  public void saveRawConfig(String configFileName, ByteArrayResource resource) {
    File destinationFile = getDestinationFile(configFileName);
    try (InputStream is = resource.getInputStream();
        FileOutputStream fos = new FileOutputStream(destinationFile)) {
      IOUtils.copy(is, fos);
    } catch (IOException e) {
      throw new SmsRuntimeException("Error saving file " + configFileName, e);
    }
  }

  @Override
  public InputStream getRawConfig(String configFileName) {
    InputStream is = null;
    try {
      File configurationFile = getDestinationFile(configFileName);
      if (configurationFile.exists()) {
        is = new FileInputStream(configurationFile);
      }
    } catch (IOException e) {
      throw new SmsRuntimeException("Error loading file " + configFileName, e);
    }
    return is;
  }

  @Override
  public boolean configurationExist(String configurationFileName) {
    return getDestinationFile(configurationFileName).exists();
  }

  @Override
  public void createEmptyConfiguration(String fileName) {
    saveStringConfiguration(StringUtils.EMPTY, fileName);
  }

  @Override
  public void createConfigurationFromResources(String fileName) {
    String defaultConfiguration = ResourceUtil.readResourceFile(fileName);
    saveStringConfiguration(defaultConfiguration, fileName);
  }

  private void saveStringConfiguration(String configuration, String fileName) {
    ByteArrayResource resource = new ByteArrayResource(configuration.getBytes());
    saveRawConfig(fileName, resource);
  }

  private File getDestinationFile(String filename) {
    File configFileFolder =
        OpenmrsUtil.getDirectoryInApplicationDataDirectory(SMSConstants.CONFIG_DIR);
    return new File(configFileFolder, filename);
  }
}
