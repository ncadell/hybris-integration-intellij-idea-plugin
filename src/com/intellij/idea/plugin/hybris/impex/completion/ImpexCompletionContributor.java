/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.intellij.idea.plugin.hybris.impex.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.idea.plugin.hybris.impex.ImpexLanguage;
import com.intellij.idea.plugin.hybris.impex.completion.provider.ImpexHeaderAttributeModifierNameCompletionProvider;
import com.intellij.idea.plugin.hybris.impex.completion.provider.ImpexHeaderAttributeModifierValueCompletionProvider;
import com.intellij.idea.plugin.hybris.impex.completion.provider.ImpexHeaderItemTypeAttributeNameCompletionProvider;
import com.intellij.idea.plugin.hybris.impex.completion.provider.ImpexHeaderItemTypeCodeCompletionProvider;
import com.intellij.idea.plugin.hybris.impex.completion.provider.ImpexHeaderTypeModifierNameCompletionProvider;
import com.intellij.idea.plugin.hybris.impex.completion.provider.ImpexHeaderTypeModifierValueCompletionProvider;
import com.intellij.idea.plugin.hybris.impex.completion.provider.ImpexKeywordCompletionProvider;
import com.intellij.idea.plugin.hybris.impex.completion.provider.ImpexMacrosCompletionProvider;
import com.intellij.idea.plugin.hybris.impex.constants.ImpexKeywords;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexFullHeaderParameter;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexFullHeaderType;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexModifiers;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexTypes;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class ImpexCompletionContributor extends CompletionContributor {

    private static final Logger LOG = Logger.getInstance(ImpexCompletionContributor.class);

    public ImpexCompletionContributor() {
        // case: header type modifier -> attribute_name
        extend(
            CompletionType.BASIC,
            psiElement()
                .withLanguage(ImpexLanguage.getInstance())
                .withElementType(ImpexTypes.ATTRIBUTE_NAME)
                .inside(ImpexFullHeaderType.class)
                .inside(ImpexModifiers.class),
            ImpexHeaderTypeModifierNameCompletionProvider.getInstance()
        );

        // case: header attribute's modifier name -> attribute_name
        extend(
            CompletionType.BASIC,
            psiElement()
                .withLanguage(ImpexLanguage.getInstance())
                .withElementType(ImpexTypes.ATTRIBUTE_NAME)
                .inside(ImpexFullHeaderParameter.class)
                .inside(ImpexModifiers.class),
            ImpexHeaderAttributeModifierNameCompletionProvider.getInstance()
        );

        // case: header type value -> attribute_value
        extend(
            CompletionType.BASIC,
            psiElement()
                .withLanguage(ImpexLanguage.getInstance())
                .withElementType(ImpexTypes.ATTRIBUTE_VALUE)
                .inside(ImpexFullHeaderType.class)
                .inside(ImpexModifiers.class),
            ImpexHeaderTypeModifierValueCompletionProvider.getInstance()
        );

        // case: header attribute's modifier value -> attribute_value
        extend(
            CompletionType.BASIC,
            psiElement()
                .withLanguage(ImpexLanguage.getInstance())
                .withElementType(ImpexTypes.ATTRIBUTE_VALUE)
                .inside(ImpexFullHeaderParameter.class)
                .inside(ImpexModifiers.class),
            ImpexHeaderAttributeModifierValueCompletionProvider.getInstance()
        );

        // case: itemtype-code
        extend(
            CompletionType.BASIC,
            psiElement()
                .withLanguage(ImpexLanguage.getInstance())
                .withElementType(ImpexTypes.HEADER_TYPE),
            ImpexHeaderItemTypeCodeCompletionProvider.getInstance()
        );

        // case: item's attribute
        extend(
            CompletionType.BASIC,
            psiElement()
                .withLanguage(ImpexLanguage.getInstance())
                .withElementType(ImpexTypes.HEADER_PARAMETER_NAME),
            ImpexHeaderItemTypeAttributeNameCompletionProvider.getInstance()
        );
        // case: impex keywords
        extend(
            CompletionType.BASIC,
            topLevel(),
            new ImpexKeywordCompletionProvider(ImpexKeywords.keywords())
        );

        // case: impex macros
        extend(
            CompletionType.BASIC,
            psiElement()
                .withLanguage(ImpexLanguage.getInstance())
                .withElementType(ImpexTypes.MACRO_USAGE),
            new ImpexMacrosCompletionProvider()
        );

    }

    private static PsiElementPattern.Capture<PsiElement> topLevel() {
        return psiElement()
            .withLanguage(ImpexLanguage.getInstance())
            .andNot(psiElement()
                        // FIXME bad code, but working
                        .andOr(
                            psiElement(ImpexTypes.HEADER_TYPE),
                            psiElement(ImpexTypes.MACRO_NAME_DECLARATION),
                            psiElement(ImpexTypes.ROOT_MACRO_USAGE),
                            psiElement(ImpexTypes.MACRO_DECLARATION),
                            psiElement(ImpexTypes.ASSIGN_VALUE),
                            psiElement(ImpexTypes.MACRO_VALUE),
                            psiElement(ImpexTypes.ATTRIBUTE),
                            psiElement(ImpexTypes.HEADER_TYPE_NAME),
                            psiElement(ImpexTypes.HEADER_PARAMETER_NAME),
                            psiElement(ImpexTypes.ATTRIBUTE_NAME),
                            psiElement(ImpexTypes.ATTRIBUTE_VALUE)
                        )
            );
    }
}
