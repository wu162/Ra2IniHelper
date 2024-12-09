package com.github.wu162.ra2inihelper.lang.format

import com.github.wu162.ra2inihelper.lang.IniLanguage
import com.github.wu162.ra2inihelper.lang.IniTypes
import com.intellij.formatting.*
import com.intellij.psi.codeStyle.CodeStyleSettings


object IniFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val codeStyleSettings = formattingContext.codeStyleSettings
        return FormattingModelProvider
            .createFormattingModelForPsiFile(
                formattingContext.containingFile,
                IniBlock(
                    formattingContext.node,
                    Wrap.createWrap(WrapType.NONE, false),
                    Alignment.createAlignment(),
                    createSpaceBuilder(codeStyleSettings)
                ),
                codeStyleSettings
            )
    }

    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
        return SpacingBuilder(settings, IniLanguage)
            .around(IniTypes.SEPARATOR)
            .spaceIf(settings.getCommonSettings(IniLanguage.id).SPACE_AROUND_ASSIGNMENT_OPERATORS)
            .before(IniTypes.PROPERTY)
            .none()
    }
}