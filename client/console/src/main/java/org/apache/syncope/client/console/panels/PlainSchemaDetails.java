/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.client.console.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.client.console.commons.Constants;
import org.apache.syncope.client.console.commons.JexlHelpUtils;
import org.apache.syncope.client.console.init.MIMETypesLoader;
import org.apache.syncope.client.console.pages.AbstractBasePage;
import org.apache.syncope.client.console.wicket.markup.html.bootstrap.dialog.BaseModal;
import org.apache.syncope.client.console.wicket.markup.html.form.AjaxCheckBoxPanel;
import org.apache.syncope.client.console.wicket.markup.html.form.AjaxDropDownChoicePanel;
import org.apache.syncope.client.console.wicket.markup.html.form.AjaxTextFieldPanel;
import org.apache.syncope.client.console.wicket.markup.html.form.MultiFieldPanel;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.common.lib.to.AbstractSchemaTO;
import org.apache.syncope.common.lib.to.PlainSchemaTO;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.CipherAlgorithm;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.string.Strings;

public class PlainSchemaDetails extends AbstractSchemaDetailsPanel {

    private static final long serialVersionUID = 5378100729213456451L;

    private static final MIMETypesLoader MIME_TYPES_INITIALIZER = new MIMETypesLoader();

    private final MultiFieldPanel<String> enumerationValues;

    private final MultiFieldPanel<String> enumerationKeys;

    public PlainSchemaDetails(
            final String id,
            final PageReference pageReference,
            final BaseModal<AbstractSchemaTO> modal) {
        super(id, pageReference, modal);

        final AjaxDropDownChoicePanel<AttrSchemaType> type = new AjaxDropDownChoicePanel<>(
                "type", getString("type"), new PropertyModel<AttrSchemaType>(schemaTO, "type"));

        type.setChoices(Arrays.asList(AttrSchemaType.values()));
        type.setEnabled(schemaTO.getKey() == null || schemaTO.getKey().isEmpty());
        type.addRequiredLabel();

        schemaForm.add(type);

        // long, double, date
        final AjaxTextFieldPanel conversionPattern = new AjaxTextFieldPanel("conversionPattern",
                getString("conversionPattern"), new PropertyModel<String>(schemaTO, "conversionPattern"));

        schemaForm.add(conversionPattern);

        final WebMarkupContainer conversionParams = new WebMarkupContainer("conversionParams");
        conversionParams.setOutputMarkupPlaceholderTag(true);
        conversionParams.add(conversionPattern);

        schemaForm.add(conversionParams);

        final WebMarkupContainer typeParams = new WebMarkupContainer("typeParams");

        typeParams.setOutputMarkupPlaceholderTag(true);

        // enum
        final AjaxTextFieldPanel enumerationValuesPanel =
                new AjaxTextFieldPanel("panel", "enumerationValues", new Model<String>(null));

        enumerationValues = new MultiFieldPanel.Builder<>(
                new ListModel<String>()).build(
                        "enumerationValues",
                        "enumerationValues",
                        enumerationValuesPanel);
        enumerationValues.setModelObject(getEnumValuesAsList(((PlainSchemaTO) schemaTO).getEnumerationValues()));

        enumerationKeys = new MultiFieldPanel.Builder<>(
                new ListModel<String>()).build(
                        "enumerationKeys",
                        "enumerationKeys",
                        new AjaxTextFieldPanel("panel", "enumerationKeys", new Model<String>()));
        enumerationKeys.setModelObject(getEnumValuesAsList(((PlainSchemaTO) schemaTO).getEnumerationKeys()));

        final WebMarkupContainer enumParams = new WebMarkupContainer("enumParams");
        enumParams.setOutputMarkupPlaceholderTag(true);
        enumParams.add(enumerationValues);
        enumParams.add(enumerationKeys);
        typeParams.add(enumParams);

        // encrypted
        final AjaxTextFieldPanel secretKey = new AjaxTextFieldPanel("secretKey",
                getString("secretKey"), new PropertyModel<String>(schemaTO, "secretKey"));

        final AjaxDropDownChoicePanel<CipherAlgorithm> cipherAlgorithm = new AjaxDropDownChoicePanel<>(
                "cipherAlgorithm", getString("cipherAlgorithm"),
                new PropertyModel<CipherAlgorithm>(schemaTO, "cipherAlgorithm"));

        cipherAlgorithm.setChoices(Arrays.asList(CipherAlgorithm.values()));

        final WebMarkupContainer encryptedParams = new WebMarkupContainer("encryptedParams");
        encryptedParams.setOutputMarkupPlaceholderTag(true);
        encryptedParams.add(secretKey);
        encryptedParams.add(cipherAlgorithm);

        typeParams.add(encryptedParams);

        // binary
        final AjaxTextFieldPanel mimeType = new AjaxTextFieldPanel("mimeType",
                getString("mimeType"), new PropertyModel<String>(schemaTO, "mimeType"));

        mimeType.setChoices(MIME_TYPES_INITIALIZER.getMimeTypes());

        final WebMarkupContainer binaryParams = new WebMarkupContainer("binaryParams");
        binaryParams.setOutputMarkupPlaceholderTag(true);
        binaryParams.add(mimeType);
        typeParams.add(binaryParams);
        schemaForm.add(typeParams);

        // show or hide
        showHide(schemaTO, type,
                conversionParams, conversionPattern,
                enumParams, enumerationValuesPanel, enumerationValues, enumerationKeys,
                encryptedParams, secretKey, cipherAlgorithm,
                binaryParams, mimeType);

        type.getField().add(new AjaxFormComponentUpdatingBehavior(Constants.ON_CHANGE) {

            private static final long serialVersionUID = -1107858522700306810L;

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                PlainSchemaDetails.this.showHide(schemaTO, type,
                        conversionParams, conversionPattern,
                        enumParams, enumerationValuesPanel, enumerationValues, enumerationKeys,
                        encryptedParams, secretKey, cipherAlgorithm,
                        binaryParams, mimeType);
                target.add(conversionParams);
                target.add(typeParams);
            }
        }
        );

        final IModel<List<String>> validatorsList = new LoadableDetachableModel<List<String>>() {

            private static final long serialVersionUID = 5275935387613157437L;

            @Override
            protected List<String> load() {
                return schemaRestClient.getAllValidatorClasses();
            }
        };

        final AjaxDropDownChoicePanel<String> validatorClass = new AjaxDropDownChoicePanel<>("validatorClass",
                getString("validatorClass"), new PropertyModel<String>(schemaTO, "validatorClass"));
        ((DropDownChoice) validatorClass.getField()).setNullValid(true);
        validatorClass.setChoices(validatorsList.getObject());
        schemaForm.add(validatorClass);

        final AutoCompleteTextField<String> mandatoryCondition =
                new AutoCompleteTextField<String>("mandatoryCondition") {

                    private static final long serialVersionUID = -2428903969518079100L;

                    @Override
                    protected Iterator<String> getChoices(final String input) {
                        List<String> choices = new ArrayList<>();

                        if (Strings.isEmpty(input)) {
                            choices = Collections.emptyList();
                        } else if ("true".startsWith(input.toLowerCase())) {
                            choices.add("true");
                        } else if ("false".startsWith(input.toLowerCase())) {
                            choices.add("false");
                        }

                        return choices.iterator();
                    }
                };
        mandatoryCondition.add(new AjaxFormComponentUpdatingBehavior(Constants.ON_CHANGE) {

            private static final long serialVersionUID = -1107858522700306810L;

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        schemaForm.add(mandatoryCondition);

        final WebMarkupContainer pwdJexlHelp = JexlHelpUtils.getJexlHelpWebContainer("jexlHelp");

        final AjaxLink<Void> pwdQuestionMarkJexlHelp = JexlHelpUtils.getAjaxLink(pwdJexlHelp, "questionMarkJexlHelp");
        schemaForm.add(pwdQuestionMarkJexlHelp);
        pwdQuestionMarkJexlHelp.add(pwdJexlHelp);

        final AjaxCheckBoxPanel multivalue = new AjaxCheckBoxPanel("multivalue", getString("multivalue"),
                new PropertyModel<Boolean>(schemaTO, "multivalue"));
        schemaForm.add(multivalue);

        final AjaxCheckBoxPanel readonly = new AjaxCheckBoxPanel("readonly", getString("readonly"),
                new PropertyModel<Boolean>(schemaTO, "readonly"));
        schemaForm.add(readonly);

        final AjaxCheckBoxPanel uniqueConstraint = new AjaxCheckBoxPanel("uniqueConstraint",
                getString("uniqueConstraint"), new PropertyModel<Boolean>(schemaTO, "uniqueConstraint"));
        schemaForm.add(uniqueConstraint);

    }

    private void showHide(final AbstractSchemaTO schema, final AjaxDropDownChoicePanel<AttrSchemaType> type,
            final WebMarkupContainer conversionParams, final AjaxTextFieldPanel conversionPattern,
            final WebMarkupContainer enumParams, final AjaxTextFieldPanel enumerationValuesPanel,
            final MultiFieldPanel<String> enumerationValues, final MultiFieldPanel<String> enumerationKeys,
            final WebMarkupContainer encryptedParams,
            final AjaxTextFieldPanel secretKey, final AjaxDropDownChoicePanel<CipherAlgorithm> cipherAlgorithm,
            final WebMarkupContainer binaryParams, final AjaxTextFieldPanel mimeType) {

        final int typeOrdinal = Integer.parseInt(type.getField().getValue());
        if (AttrSchemaType.Long.ordinal() == typeOrdinal
                || AttrSchemaType.Double.ordinal() == typeOrdinal
                || AttrSchemaType.Date.ordinal() == typeOrdinal) {

            conversionParams.setVisible(true);

            enumParams.setVisible(false);
            if (enumerationValuesPanel.isRequired()) {
                enumerationValuesPanel.removeRequiredLabel();
            }
            enumerationValues.setModelObject(getEnumValuesAsList(null));
            enumerationKeys.setModelObject(getEnumValuesAsList(null));

            encryptedParams.setVisible(false);
            if (secretKey.isRequired()) {
                secretKey.removeRequiredLabel();
            }
            secretKey.setModelObject(null);
            if (cipherAlgorithm.isRequired()) {
                cipherAlgorithm.removeRequiredLabel();
            }
            cipherAlgorithm.setModelObject(null);

            binaryParams.setVisible(false);
            mimeType.setModelObject(null);
        } else if (AttrSchemaType.Enum.ordinal() == typeOrdinal) {
            conversionParams.setVisible(false);
            conversionPattern.setModelObject(null);

            enumParams.setVisible(true);
            if (!enumerationValuesPanel.isRequired()) {
                enumerationValuesPanel.addRequiredLabel();
            }
            enumerationValues.setModelObject(getEnumValuesAsList(((PlainSchemaTO) schema).getEnumerationValues()));
            enumerationKeys.setModelObject(getEnumValuesAsList(((PlainSchemaTO) schema).getEnumerationKeys()));

            encryptedParams.setVisible(false);
            if (secretKey.isRequired()) {
                secretKey.removeRequiredLabel();
            }
            secretKey.setModelObject(null);
            if (cipherAlgorithm.isRequired()) {
                cipherAlgorithm.removeRequiredLabel();
            }
            cipherAlgorithm.setModelObject(null);

            binaryParams.setVisible(false);
            mimeType.setModelObject(null);
        } else if (AttrSchemaType.Encrypted.ordinal() == typeOrdinal) {
            conversionParams.setVisible(false);
            conversionPattern.setModelObject(null);

            enumParams.setVisible(false);
            if (enumerationValuesPanel.isRequired()) {
                enumerationValuesPanel.removeRequiredLabel();
            }
            enumerationValues.setModelObject(getEnumValuesAsList(null));
            enumerationKeys.setModelObject(getEnumValuesAsList(null));

            encryptedParams.setVisible(true);
            if (!secretKey.isRequired()) {
                secretKey.addRequiredLabel();
            }
            if (cipherAlgorithm.isRequired()) {
                cipherAlgorithm.addRequiredLabel();
            }

            binaryParams.setVisible(false);
            mimeType.setModelObject(null);
        } else if (AttrSchemaType.Binary.ordinal() == typeOrdinal) {
            conversionParams.setVisible(false);
            conversionPattern.setModelObject(null);

            enumParams.setVisible(false);
            if (enumerationValuesPanel.isRequired()) {
                enumerationValuesPanel.removeRequiredLabel();
            }
            enumerationValues.setModelObject(getEnumValuesAsList(null));
            enumerationKeys.setModelObject(getEnumValuesAsList(null));

            encryptedParams.setVisible(false);
            if (secretKey.isRequired()) {
                secretKey.removeRequiredLabel();
            }
            secretKey.setModelObject(null);
            if (cipherAlgorithm.isRequired()) {
                cipherAlgorithm.removeRequiredLabel();
            }
            cipherAlgorithm.setModelObject(null);

            binaryParams.setVisible(true);
        } else {
            conversionParams.setVisible(false);
            conversionPattern.setModelObject(null);

            enumParams.setVisible(false);
            if (enumerationValuesPanel.isRequired()) {
                enumerationValuesPanel.removeRequiredLabel();
            }
            enumerationValues.setModelObject(getEnumValuesAsList(null));
            enumerationKeys.setModelObject(getEnumValuesAsList(null));

            encryptedParams.setVisible(false);
            if (secretKey.isRequired()) {
                secretKey.removeRequiredLabel();
            }
            secretKey.setModelObject(null);
            if (cipherAlgorithm.isRequired()) {
                cipherAlgorithm.removeRequiredLabel();
            }
            cipherAlgorithm.setModelObject(null);

            binaryParams.setVisible(false);
            mimeType.setModelObject(null);
        }
    }

    private String getEnumValuesAsString(final List<String> enumerationValues) {
        final StringBuilder builder = new StringBuilder();

        for (String str : enumerationValues) {
            if (StringUtils.isNotBlank(str)) {
                if (builder.length() > 0) {
                    builder.append(SyncopeConstants.ENUM_VALUES_SEPARATOR);
                }

                builder.append(str.trim());
            }
        }

        return builder.toString();
    }

    private List<String> getEnumValuesAsList(final String enumerationValues) {
        final List<String> values = new ArrayList<>();

        if (StringUtils.isNotBlank(enumerationValues)) {
            for (String value : enumerationValues.split(SyncopeConstants.ENUM_VALUES_SEPARATOR)) {
                values.add(value.trim());
            }
        } else {
            values.add(StringUtils.EMPTY);
        }

        return values;
    }

    @Override
    public void getOnSubmit(final AjaxRequestTarget target, final BaseModal<?> modal,
            final Form<?> form, final PageReference pageReference, final boolean createFlag) {

        try {
            final PlainSchemaTO updatedPlainSchemaTO = PlainSchemaTO.class.cast(form.getModelObject());

            updatedPlainSchemaTO.setEnumerationValues(
                    getEnumValuesAsString(enumerationValues.getView().getModelObject()));
            updatedPlainSchemaTO.setEnumerationKeys(getEnumValuesAsString(enumerationKeys.getView().getModelObject()));

            if (createFlag) {
                schemaRestClient.createPlainSchema(updatedPlainSchemaTO);
            } else {
                schemaRestClient.updatePlainSchema(updatedPlainSchemaTO);
            }

            if (pageReference.getPage() instanceof AbstractBasePage) {
                ((AbstractBasePage) pageReference.getPage()).setModalResult(true);
            }
            modal.close(target);
        } catch (Exception e) {
            LOG.error("While creating or updating plain schema", e);
            error(getString(Constants.ERROR) + ": " + e.getMessage());
            modal.getFeedbackPanel().refresh(target);
        }
    }
}