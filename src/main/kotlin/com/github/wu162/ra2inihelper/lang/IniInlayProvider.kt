package com.github.wu162.ra2inihelper.lang

import com.github.wu162.ra2inihelper.lang.psi.IniSectionheader
import com.intellij.codeInsight.hints.*
import com.intellij.lang.Language
import com.intellij.openapi.editor.BlockInlayPriority
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class IniInlayProvider : InlayHintsProvider<NoSettings> {
    override val key: SettingsKey<NoSettings>
        get() = SettingsKey("ini.ra2.inlay.section.name")
    override val name: String
        get() = "ini.ra2.inlay.section.name"
    override val previewText: String?
        get() = null

    override fun createSettings(): NoSettings {
        return NoSettings()
    }

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: NoSettings,
        sink: InlayHintsSink
    ): InlayHintsCollector? {
        return IniInlayTypeHintsCollector(editor)
    }

    override fun createConfigurable(settings: NoSettings): ImmediateConfigurable {
        return object : ImmediateConfigurable {
            override fun createComponent(listener: ChangeListener): JComponent = panel { }
        }
    }

    override fun isLanguageSupported(language: Language): Boolean {
        return language == IniLanguage
    }
}

class IniInlayTypeHintsCollector(editor: Editor) : FactoryInlayHintsCollector(editor) {

    val names = mapOf(
        "E1" to "美国大兵",
        "ADOG" to "盟军警犬",
        "ENGINEER" to "盟军工程师",
        "GGI" to "守护大兵",
        "JUMPJET" to "火箭飞行兵",
        "SPY" to "间谍",
    )

    override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
        if (!element.isValid || element.project.isDefault) return false
        if (element is IniSectionheader) {
            //TODO 应该使用csf里的
            //TODO 应该检查有没有 UI:Name 有的话看看csf里有没有对应的名字
            val text = element.node.findChildByType(IniTypes.SECTIONNAME)?.text
            if (!text.isNullOrEmpty() && names.containsKey(text)) {
                sink.addInlineElement(
                    element.endOffset,
                    false
                    , factory.roundWithBackground(factory.seq(factory.smallText(names[text]!!))), false)
            }

        }
        return true
    }

}