package org.connectforlife.module.sms.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSExcelFileProcessor;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSInputSourceProcessor;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSInputSourceProcessorContext;
import org.connectforlife.module.sms.api.adhocsms.AdHocSMSSQLDataSetProcessor;
import org.connectforlife.module.sms.api.data.AdHocSMSData;
import org.connectforlife.module.sms.api.service.AdHocSMSInputSourceProcessorService;
import org.connectforlife.module.sms.api.service.ScheduleAdHocSMSesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "Schedules AdHoc SMSes based on data from different input sources",
    tags = "REST API to schedule AdHoc SMSes based on data from different input sources")
@RestController("sms.scheduleAdHocSMSController")
@RequestMapping(value = "/scheduleAdHocSMS")
public class ScheduleAdHocSMSController extends
    org.connectforlife.module.sms.web.controller.RestController {

  private static final Log LOGGER = LogFactory.getLog(ScheduleAdHocSMSController.class);

  @Autowired
  @Qualifier("sms.adHocSMSInputSourceProcessorService")
  private AdHocSMSInputSourceProcessorService adHocSMSInputSourceProcessorService;

  @Autowired
  @Qualifier("sms.scheduleAdHocSMSesService")
  private ScheduleAdHocSMSesService scheduleAdHocSMSesService;

  @ApiOperation(value = "None", notes = "Schedules AdHoc SMSes based on data from file")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "AdHoc SMSes have been successfully scheduled"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failed to schedule AdHoc SMSes")
  })
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void scheduleAdHocSMSesFromFile(
      @ApiParam(name = "file", value = "file") @RequestParam(value = "file") MultipartFile file,
      @ApiParam(name = "sheetName", value = "sheetName") @RequestParam(value = "sheetName", required = false) String sheetName)
      throws IOException {
    AdHocSMSInputSourceProcessorContext context = buildContextForInputFiles(file, sheetName);
    List<AdHocSMSData> smsData = adHocSMSInputSourceProcessorService.getAdHocSMSData(context);
    if (CollectionUtils.isNotEmpty(smsData)) {
      scheduleAdHocSMSesService.scheduleAdHocSMSes(smsData);
    } else {
      LOGGER.info("No SMSes scheduled to be sent");
    }
  }

  @ApiOperation(value = "None", notes = "Schedules AdHoc SMSes based on data from SQL data set")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "AdHoc SMSes have been successfully scheduled"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failed to schedule AdHoc SMSes")
  })
  @RequestMapping(value = "/{dataSetUuid}", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void scheduleAdHocSMSesFromDataSet(
      @ApiParam(name = "dataSetUuid", value = "dataSetUuid") @PathVariable String dataSetUuid) {
    AdHocSMSInputSourceProcessorContext context = buildContextForSQLDataset(dataSetUuid);
    List<AdHocSMSData> smsData = adHocSMSInputSourceProcessorService.getAdHocSMSData(context);
    if (CollectionUtils.isNotEmpty(smsData)) {
      scheduleAdHocSMSesService.scheduleAdHocSMSes(smsData);
    } else {
      LOGGER.info("No SMSes scheduled to be sent");
    }
  }

  private AdHocSMSInputSourceProcessorContext buildContextForInputFiles(MultipartFile file,
      String sheetName)
      throws IOException {
    String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
    InputStream inputStream = file.getInputStream();
    Map<String, String> options = new HashMap<>();
    options.put(AdHocSMSInputSourceProcessor.EXTENSION_FILE_PROP_NAME, fileExtension);
    options.put(AdHocSMSExcelFileProcessor.SHEET_NAME_PROP_NAME, sheetName);

    return new AdHocSMSInputSourceProcessorContext(inputStream, options);
  }

  private AdHocSMSInputSourceProcessorContext buildContextForSQLDataset(String dataSetUuid) {
    Map<String, String> options = new HashMap<>();
    options.put(AdHocSMSSQLDataSetProcessor.DATA_SET_UUID_PROP_NAME, dataSetUuid);

    return new AdHocSMSInputSourceProcessorContext(null, options);
  }
}
