package com.github.wu162.ra2inihelper.lang

import com.github.wu162.ra2inihelper.indexer.ObjectsIndexer
import com.github.wu162.ra2inihelper.lang.psi.IniProperty
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getKey
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getParentSectionName
import com.github.wu162.ra2inihelper.ra2Root
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.rd.util.concurrentMapOf
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class IniDocumentationProvider : AbstractDocumentationProvider() {

    companion object {

        val objPropDesc = concurrentMapOf<String, String>()
        val generalPropDesc = concurrentMapOf<String, String>()

        val gson = Gson()

        fun loadDoc() {
            loadObjPropDesc("${ra2Root()}/ideaplugin/ra2IniPropDescs.json", objPropDesc)
            loadObjPropDesc("${ra2Root()}/ideaplugin/ra2GeneralPropDescs.json", generalPropDesc)
            return
        }

        private fun loadObjPropDesc(path: String, map: MutableMap<String, String>) {
            if (!File(path).exists()) {
                return
            }
            BufferedReader(FileReader(path)).use { br ->
                // 解析 JSON 文件为 User 对象
                val descs: Map<String, String> = gson.fromJson(br, object : TypeToken<Map<String, String>>() {}.type)
                descs.forEach {
                    map.put(it.key, it.value.replace("\n", "<br>"))
                }
            }
        }
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val filePath = element?.containingFile?.virtualFile?.path
        if (filePath != null && !filePath.startsWith(ra2Root())) {
            return null
        }

        if (element is IniProperty) {
            //get section by element
            val sectionName = element.getParentSectionName()
            val keyName = element.getKey()

            if (sectionName != null && keyName != null) {
                when (sectionName) {
                    "General" -> {
                        //全局
                        if (generalPropDesc.containsKey(keyName)) {
                            return generalPropDesc[keyName]
                        }
                    }

                    else -> {
                        // 物体的
                        if (objPropDesc.containsKey(keyName)) {
                            val objectNames = FileBasedIndex.getInstance()
                                .getValues(
                                    ObjectsIndexer.ObjectsIndexerName,
                                    ObjectsIndexer.KEY_OBJECT,
                                    GlobalSearchScope.allScope(element.project)
                                )
                            //check if objectNames contains sectionName
                            objectNames.forEach {
                                it.forEach { name ->
                                    if (name == sectionName) {
                                        return objPropDesc[keyName]
                                    }
                                }
                            }
                        }

                    }
                }

            }


        }
        return super.generateDoc(element, originalElement)
    }


}