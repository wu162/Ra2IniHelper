package com.github.wu162.ra2inihelper.indexer

import com.github.wu162.ra2inihelper.lang.IniFileType
import com.github.wu162.ra2inihelper.lang.IniTokenSets
import com.github.wu162.ra2inihelper.ra2Root
import com.intellij.lang.LighterASTNode
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.impl.source.tree.LightTreeUtil
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.IOUtil
import com.intellij.util.io.KeyDescriptor
import java.io.DataInput
import java.io.DataOutput

class ObjectsIndexer : FileBasedIndexExtension<String, List<String>>() {

    companion object {

        val objectRegisterSectionNames = listOf(
            "[InfantryTypes]",
            "[VehicleTypes]",
            "[AircraftTypes]",
            "[BuildingTypes]",
        )

        val ObjectsIndexerName = ID.create<String, List<String>>(javaClass.canonicalName)
        const val KEY_OBJECT = "objects"

    }

    override fun getName(): ID<String, List<String>> = ObjectsIndexerName

    override fun getIndexer(): DataIndexer<String, List<String>, FileContent> {
        return DataIndexer<String, List<String>, FileContent> { inputData ->
            val map = mutableMapOf<String, List<String>>()

            val objectsMap = mutableListOf<String>()
            map.put(KEY_OBJECT, objectsMap)
            val lighterAST = (inputData as? PsiDependentFileContent)?.lighterAST
            if (lighterAST != null) {
                lighterAST.getChildren(lighterAST.root).forEach { child ->
                    if (child.tokenType.debugName == "SECTIONS") {
                        var legalChild = false
                        lighterAST.getChildren(child).forEach { child2 ->
                            if (child2.tokenType.debugName == "SECTIONHEADER") {
                                val sectionHeaderText = LightTreeUtil.toFilteredString(lighterAST, child2, null)
                                if (objectRegisterSectionNames.contains(sectionHeaderText)) {
                                    legalChild = true
                                    return@forEach
                                }

                            }
                        }
                        if (legalChild) {
                            lighterAST.getChildren(child).forEach { child2 ->
                                if (child2.tokenType.debugName == "ITEM_") {
                                    lighterAST.getChildren(child2).forEach { child3 ->
                                        if (child3.tokenType.debugName == "PROPERTY") {
                                            val propertyText = LightTreeUtil.toFilteredString(lighterAST, child3, null)
                                            val splits = propertyText.split("=")
                                            if (splits.size >= 2) {
                                                objectsMap.add(splits[1])
                                            }
                                        }
                                    }

                                }
                            }
                        }


                    }
                }


            }
            map
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<List<String>> {
        return object : DataExternalizer<List<String>> {
            override fun save(out: DataOutput, list: List<String>?) {
                if (list != null) {
                    IOUtil.writeUTF(out, list.size.toString())
                    list.forEach {
                        IOUtil.writeUTF(out, it)
                    }
                }

            }

            override fun read(input: DataInput): List<String> {
                val result = mutableListOf<String>()
                val size = IOUtil.readUTF(input).toInt()
                for (i in 0 until size) {
                    result.add(IOUtil.readUTF(input))
                }
                return result
            }

        }
    }

    override fun getVersion(): Int = 5

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return object : DefaultFileTypeSpecificInputFilter(IniFileType) {
            override fun acceptInput(file: VirtualFile): Boolean {
                return file.path.startsWith(ra2Root())
            }
        }
    }

    override fun dependsOnFileContent(): Boolean  = true
}