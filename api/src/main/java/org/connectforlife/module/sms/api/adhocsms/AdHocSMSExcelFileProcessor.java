/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.api.adhocsms;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.connectforlife.module.sms.api.data.AdHocSMSData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class AdHocSMSExcelFileProcessor implements AdHocSMSInputSourceProcessor {

  public static final String SHEET_NAME_PROP_NAME = "sheetName";

  private static final Set<String> EXCEL_FILES_EXTENSION =
      new HashSet<>(Arrays.asList("xls", "xlsx"));

  private static final Log LOGGER = LogFactory.getLog(AdHocSMSExcelFileProcessor.class);

  private static final int NUMBER_OF_COLUMNS = 5;

  private static final int PHONE_NUMBER_COLUMN_INDEX = 0;

  private static final int SMS_TEXT_COLUMN_INDEX = 1;

  private static final int PARAMETERS_COLUMN_INDEX = 2;

  private static final int CONTACT_TIME_COLUMN_INDEX = 3;

  private static final int CONFIG_COLUMN_INDEX = 4;

  private static final Map<Integer, BiConsumer<AdHocSMSData, String>> COLUMNS_INDEX_VALUE_MAP =
      new HashMap<>();

  static {
    COLUMNS_INDEX_VALUE_MAP.put(PHONE_NUMBER_COLUMN_INDEX, (AdHocSMSData::setPhone));
    COLUMNS_INDEX_VALUE_MAP.put(SMS_TEXT_COLUMN_INDEX, (AdHocSMSData::setSmsText));
    COLUMNS_INDEX_VALUE_MAP.put(
        PARAMETERS_COLUMN_INDEX,
        ((adHocSMSData, cellValue) -> {
          try {
            adHocSMSData.setParameters(cellValue);
          } catch (IOException ex) {
            LOGGER.error(
                String.format(
                    "Error occurred while converting parameters string value %s into map",
                    cellValue),
                ex);
          }
        }));
    COLUMNS_INDEX_VALUE_MAP.put(CONTACT_TIME_COLUMN_INDEX, (AdHocSMSData::setContactTime));
    COLUMNS_INDEX_VALUE_MAP.put(CONFIG_COLUMN_INDEX, (AdHocSMSData::setConfig));
  }

  @Override
  public boolean shouldProcessData(AdHocSMSInputSourceProcessorContext context) {
    String fileExtension =
        context.getOptions().get(AdHocSMSInputSourceProcessor.EXTENSION_FILE_PROP_NAME);
    if (StringUtils.isNotBlank(fileExtension)) {
      return EXCEL_FILES_EXTENSION.contains(fileExtension.toLowerCase());
    }

    return false;
  }

  @Override
  public List<AdHocSMSData> getAdHocSMSData(AdHocSMSInputSourceProcessorContext context) {
    String sheetName = context.getOptions().get(SHEET_NAME_PROP_NAME);
    List<AdHocSMSData> smsDataList = new ArrayList<>();
    try (Workbook workbook = WorkbookFactory.create(context.getFile())) {
      Sheet sheet = getSheet(workbook, sheetName);
      for (Row row : sheet) {
        if (isRowInvalid(row)) {
          LOGGER.warn(
              String.format(
                  "Row number %d has invalid format. It will be skipped during processing",
                  row.getRowNum() + 1));
          continue;
        }
        smsDataList.add(buildAdHocSMSObject(row));
      }
    } catch (IOException ex) {
      LOGGER.error("Error while processing Excel file", ex);
    }

    return smsDataList;
  }

  private Sheet getSheet(Workbook workbook, String sheetName) {
    Sheet sheet;
    if (StringUtils.isNotBlank(sheetName)) {
      sheet = workbook.getSheet(sheetName);
    } else {
      sheet = workbook.getSheetAt(0);
    }

    return sheet;
  }

  private boolean isRowInvalid(Row row) {
    if (row.getLastCellNum() != NUMBER_OF_COLUMNS) {
      return true;
    }

    return isHeaderRow(row);
  }

  private boolean isHeaderRow(Row row) {
    for (Cell cell : row) {
      if (AdHocSMSData.COLUMN_NAMES.contains(getValueFromCell(cell))) {
        return true;
      }
    }

    return false;
  }

  private AdHocSMSData buildAdHocSMSObject(Row row) {
    AdHocSMSData adHocSMSData = new AdHocSMSData();
    for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
      COLUMNS_INDEX_VALUE_MAP.get(i).accept(adHocSMSData, getValueFromCell(row.getCell(i)));
    }

    return adHocSMSData;
  }

  private String getValueFromCell(Cell cell) {
    return new DataFormatter().formatCellValue(cell);
  }
}
