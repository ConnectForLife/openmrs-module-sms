/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.connectforlife.module.sms.handler.metadatasharing;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openmrs.module.metadatasharing.ImportType;
import org.connectforlife.module.sms.api.service.TemplateService;
import org.connectforlife.module.sms.api.templates.Incoming;
import org.connectforlife.module.sms.api.templates.Outgoing;
import org.connectforlife.module.sms.api.templates.Template;
import org.openmrs.test.BaseContextMockTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TemplateHandlerTest extends BaseContextMockTest {

    @Mock
    private TemplateService templateService;

    @Before
    public void setupConfigServiceMock() {
        contextMockHelper.setService(TemplateService.class, templateService);
    }

    @Test
    public void shouldReturnCorrectTemplateId() {
        // given
        final String templateName = "templateName";
        final Integer expectedTemplateId = templateName.hashCode();
        final Template templateWithId = new Template();
        templateWithId.setName(templateName);

        final TemplateHandler handler = new TemplateHandler();

        // when
        final Integer templateId = handler.getId(templateWithId);

        // then
        assertEquals(expectedTemplateId, templateId);
    }

    @Test
    public void shouldReturnCorrectTemplateUUID() {
        // given
        final String templateName = "templateName";
        final Template templateWithName = new Template();
        templateWithName.setName(templateName);

        final TemplateHandler handler = new TemplateHandler();

        // when
        final String templateUuid = handler.getUuid(templateWithName);

        // then
        assertEquals(templateName, templateUuid);
    }

    @Test
    public void shouldReturnCorrectName() {
        // given
        final String templateName = "templateName";
        final Template templateWithName = new Template();
        templateWithName.setName(templateName);

        final TemplateHandler handler = new TemplateHandler();

        // when
        final String actualName = handler.getName(templateWithName);

        // then
        assertEquals(templateName, actualName);
    }

    @Test
    public void shouldSaveNewTemplateAsDefault() {
        // given
        final Incoming newIncoming = new Incoming();
        newIncoming.setMessageKey("IncomingKey");
        final Outgoing newOutgoing = new Outgoing();
        final Template newTemplate = new Template();
        newTemplate.setName("name");
        newTemplate.setIncoming(newIncoming);
        newTemplate.setOutgoing(newOutgoing);

        when(templateService.getDefaultTemplates()).thenReturn(new LinkedList<>());
        when(templateService.getCustomTemplates()).thenReturn(new LinkedList<>());

        final TemplateHandler handler = new TemplateHandler();

        // when
        handler.saveItem(newTemplate);

        // then
        final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(templateService).writeDefaultTemplates(argumentCaptor.capture());
        verify(templateService, never()).writeCustomTemplates(any());

        final List<Template> capturedList = argumentCaptor.getValue();
        assertEquals(1, capturedList.size());
        assertEquals(newTemplate.getName(), capturedList.get(0).getName());
        assertEquals(newTemplate.getIncoming(), capturedList.get(0).getIncoming());
        assertEquals(newTemplate.getOutgoing(), capturedList.get(0).getOutgoing());
    }

    @Test
    public void shouldUpdateExistingDefaultTemplate() {
        // given
        final Template existingDefaultTemplate = new Template();
        existingDefaultTemplate.setName("name");
        existingDefaultTemplate.setConfigurables(singletonList("param1"));

        final Incoming newIncoming = new Incoming();
        newIncoming.setMessageKey("IncomingKey");
        final Outgoing newOutgoing = new Outgoing();
        final Template newTemplate = new Template();
        newTemplate.setName("name");
        newTemplate.setIncoming(newIncoming);
        newTemplate.setOutgoing(newOutgoing);
        newTemplate.setConfigurables(singletonList("param1"));

        when(templateService.getDefaultTemplates()).thenReturn(new LinkedList<>(singletonList(existingDefaultTemplate)));
        when(templateService.getCustomTemplates()).thenReturn(new LinkedList<>());

        final TemplateHandler handler = new TemplateHandler();

        // when
        handler.saveItem(newTemplate);

        // then
        final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(templateService).writeDefaultTemplates(argumentCaptor.capture());
        verify(templateService, never()).writeCustomTemplates(any());

        final List<Template> capturedList = argumentCaptor.getValue();
        assertEquals(1, capturedList.size());
        assertEquals(newTemplate.getName(), capturedList.get(0).getName());
        assertEquals(newTemplate.getIncoming(), capturedList.get(0).getIncoming());
        assertEquals(newTemplate.getOutgoing(), capturedList.get(0).getOutgoing());
        assertEquals(1, capturedList.get(0).getConfigurables().size());
        assertEquals("param1", capturedList.get(0).getConfigurables().get(0));
    }

    @Test
    public void shouldUpdateExistingCustomTemplate() {
        // given
        final Template existingCustomTemplate = new Template();
        existingCustomTemplate.setName("name");

        final Incoming newIncoming = new Incoming();
        newIncoming.setMessageKey("IncomingKey");
        final Outgoing newOutgoing = new Outgoing();
        final Template newTemplate = new Template();
        newTemplate.setName("name");
        newTemplate.setIncoming(newIncoming);
        newTemplate.setOutgoing(newOutgoing);

        when(templateService.getDefaultTemplates()).thenReturn(new LinkedList<>());
        when(templateService.getCustomTemplates()).thenReturn(new LinkedList<>(singletonList(existingCustomTemplate)));

        final TemplateHandler handler = new TemplateHandler();

        // when
        handler.saveItem(newTemplate);

        // then
        final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(templateService, never()).writeDefaultTemplates(any());
        verify(templateService).writeCustomTemplates(argumentCaptor.capture());

        final List<Template> capturedList = argumentCaptor.getValue();
        assertEquals(1, capturedList.size());
        assertEquals(newTemplate.getName(), capturedList.get(0).getName());
        assertEquals(newTemplate.getIncoming(), capturedList.get(0).getIncoming());
        assertEquals(newTemplate.getOutgoing(), capturedList.get(0).getOutgoing());
    }

    @Test
    public void shouldReturnCorrectCountOfTemplates() {
        // given
        final List<Template> allDefaultTemplates = singletonList(new Template());
        final List<Template> allCustomTemplates = Arrays.asList(new Template(), new Template());
        final List<Template> allTemplates = new LinkedList<>(allDefaultTemplates);
        allTemplates.addAll(allCustomTemplates);

        when(templateService.getDefaultTemplates()).thenReturn(allDefaultTemplates);
        when(templateService.getCustomTemplates()).thenReturn(allCustomTemplates);
        when(templateService.getAllTemplates()).thenReturn(allTemplates);

        final TemplateHandler handler = new TemplateHandler();

        // when
        final int count = handler.getItemsCount(Template.class, false, null);

        // then
        assertEquals(allDefaultTemplates.size() + allCustomTemplates.size(), count);
    }

    @Test
    public void shouldReturnAllTemplates() {
        // given
        final List<Template> allDefaultTemplates = singletonList(new Template());
        final List<Template> allCustomTemplates = Arrays.asList(new Template(), new Template());
        final List<Template> allTemplates = new LinkedList<>(allDefaultTemplates);
        allTemplates.addAll(allCustomTemplates);

        when(templateService.getDefaultTemplates()).thenReturn(allDefaultTemplates);
        when(templateService.getCustomTemplates()).thenReturn(allCustomTemplates);
        when(templateService.getAllTemplates()).thenReturn(allTemplates);

        final TemplateHandler handler = new TemplateHandler();

        // when
        final List<Template> allItems = handler.getItems(Template.class, false, null, null, null);

        // then
        assertEquals(allTemplates.size(), allItems.size());
        assertThat(allItems, CoreMatchers.hasItems(allTemplates.toArray(new Template[0])));
    }

    @Test
    public void shouldReturnPaginatedTemplates() {
        // given
        final List<Template> allDefaultTemplates = singletonList(new Template());
        final List<Template> allCustomTemplates = Arrays.asList(new Template(), new Template());
        final List<Template> allTemplates = new LinkedList<>(allDefaultTemplates);
        allTemplates.addAll(allCustomTemplates);

        when(templateService.getDefaultTemplates()).thenReturn(allDefaultTemplates);
        when(templateService.getCustomTemplates()).thenReturn(allCustomTemplates);
        when(templateService.getAllTemplates()).thenReturn(allTemplates);

        final TemplateHandler handler = new TemplateHandler();

        // when
        final List<Template> paginatedItems = handler.getItems(Template.class, false, null, 1, 20);

        // then
        assertEquals(2, paginatedItems.size());
    }

    @Test
    public void shouldReturnTemplateById() {
        // given
        final Template defaultTemplate = new Template();
        defaultTemplate.setName("defaultTemplate");
        final Template customTemplate = new Template();
        customTemplate.setName("customTemplate");

        final List<Template> allDefaultTemplates = singletonList(defaultTemplate);
        final List<Template> allCustomTemplates = singletonList(customTemplate);
        final List<Template> allTemplates = new LinkedList<>(allDefaultTemplates);
        allTemplates.addAll(allCustomTemplates);

        when(templateService.getDefaultTemplates()).thenReturn(allDefaultTemplates);
        when(templateService.getCustomTemplates()).thenReturn(allCustomTemplates);
        when(templateService.getAllTemplates()).thenReturn(allTemplates);

        final TemplateHandler handler = new TemplateHandler();

        // when
        final Template returnedTemplate = handler.getItemById(Template.class, defaultTemplate.getName().hashCode());

        // then
        assertEquals(defaultTemplate, returnedTemplate);
    }

    @Test
    public void shouldReturnTemplateByUUID() {
        // given
        final Template defaultTemplate = new Template();
        defaultTemplate.setName("defaultTemplate");
        final Template customTemplate = new Template();
        customTemplate.setName("customTemplate");

        final List<Template> allDefaultTemplates = singletonList(defaultTemplate);
        final List<Template> allCustomTemplates = singletonList(customTemplate);
        final List<Template> allTemplates = new LinkedList<>(allDefaultTemplates);
        allTemplates.addAll(allCustomTemplates);

        when(templateService.getDefaultTemplates()).thenReturn(allDefaultTemplates);
        when(templateService.getCustomTemplates()).thenReturn(allCustomTemplates);
        when(templateService.getAllTemplates()).thenReturn(allTemplates);

        final TemplateHandler handler = new TemplateHandler();

        // when
        final Template returnedTemplate = handler.getItemByUuid(Template.class, customTemplate.getName());

        // then
        assertEquals(customTemplate, returnedTemplate);
    }

    @Test
    public void shouldMergeCorrectly() {
        // given
        final Template existingCustomTemplate = new Template();
        existingCustomTemplate.setName("name");
        existingCustomTemplate.setConfigurables(singletonList("param1"));

        final Incoming newIncoming = new Incoming();
        newIncoming.setMessageKey("IncomingKey");
        final Outgoing newOutgoing = new Outgoing();
        final Template newTemplate = new Template();
        newTemplate.setName("name");
        newTemplate.setIncoming(newIncoming);
        newTemplate.setOutgoing(newOutgoing);
        newTemplate.setConfigurables(singletonList("NEW_param1"));

        final TemplateHandler handler = new TemplateHandler();

        // when
        handler.merge(existingCustomTemplate, newTemplate, ImportType.PREFER_THEIRS, Collections.emptyMap());

        // then
        assertEquals(newTemplate.getName(), "name");
        assertEquals(newTemplate.getIncoming(), newIncoming);
        assertEquals(newTemplate.getOutgoing(), newOutgoing);
        assertEquals(newTemplate.getConfigurables().size(), 1);
        assertEquals(newTemplate.getConfigurables().get(0), "NEW_param1");

    }
}
