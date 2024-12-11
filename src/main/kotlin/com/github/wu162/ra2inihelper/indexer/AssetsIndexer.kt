package com.github.wu162.ra2inihelper.indexer

import com.github.wu162.ra2inihelper.inRa2Root
import com.github.wu162.ra2inihelper.lang.IniFileType
import com.intellij.lang.LighterASTNode
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.impl.source.tree.LightTreeUtil
import com.intellij.psi.impl.source.tree.RecursiveLighterASTNodeWalkingVisitor
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.IOUtil
import com.intellij.util.io.KeyDescriptor
import java.io.DataInput
import java.io.DataOutput

class AssetsIndexer : FileBasedIndexExtension<String, List<String>>() {

    companion object {

        val objectRegisterSectionNames = listOf(
            "[InfantryTypes]",
            "[VehicleTypes]",
            "[AircraftTypes]",
            "[BuildingTypes]",
        )

        val weaponPropertyNames = listOf(
            "Primary",
            "Secondary",
            "DeathWeapon",
            "ElitePrimary",
            "EliteSecondary",
            "OccupyWeapon",
            "EliteOccupyWeapon"
        )

        val projectilePropertyNames = listOf(
            "Projectile",
        )

        val ObjectsIndexerName = ID.create<String, List<String>>(javaClass.canonicalName)
        const val KEY_INFANTRYTYPES = "InfantryTypes"
        const val KEY_VEHICLETYPES = "VehicleTypes"
        const val KEY_AIRCRAFTTYPES = "AircraftTypes"
        const val KEY_BUILDINGTYPES = "BuildingTypes"
        const val KEY_WEAPON = "weapons"
        const val KEY_WARHEADS = "warheads"
        const val KEY_PROJECTILE = "projectile"
    }

    override fun getName(): ID<String, List<String>> = ObjectsIndexerName

    override fun getIndexer(): DataIndexer<String, List<String>, FileContent> {
        return DataIndexer<String, List<String>, FileContent> { inputData ->
            val map = mutableMapOf<String, List<String>>()

            val InfantryTypesList = mutableListOf<String>()
            val VehicleTypesList = mutableListOf<String>()
            val AircraftTypesList = mutableListOf<String>()
            val BuildingTypesList = mutableListOf<String>()
            //避免重复
            val weaponsList = mutableSetOf<String>()
            val warheadsList = mutableListOf<String>()
            val projectileList = mutableListOf<String>()
            val lighterAST = (inputData as? PsiDependentFileContent)?.lighterAST
            if (lighterAST != null) {

                //第一轮 找到注册的物体
                object : RecursiveLighterASTNodeWalkingVisitor(lighterAST) {

                    private var currentSectionName = ""
//                    private var inObjectRegisterSection = false
//                    private var inWarheadsRegisterSection = false

                    override fun visitNode(element: LighterASTNode) {
                        when (element.tokenType.debugName) {
                            "SECTIONHEADER" -> {
                                currentSectionName = LightTreeUtil.toFilteredString(lighterAST, element, null).trim()

//                                if (objectRegisterSectionNames.contains(currentSectionName)) {
//                                    inObjectRegisterSection = true
//                                } else {
//                                    inObjectRegisterSection = false
//                                }
//                                if ("[Warheads]" == currentSectionName) {
//                                    inWarheadsRegisterSection = true
//                                } else {
//                                    inWarheadsRegisterSection = false
//                                }
                            }
                            "PROPERTY" -> {
                                val list: MutableList<String>? =  when (currentSectionName) {
                                    "[InfantryTypes]" -> {
                                        InfantryTypesList
                                    }
                                    "[VehicleTypes]" -> {
                                        VehicleTypesList
                                    }
                                    "[AircraftTypes]" -> {
                                        AircraftTypesList
                                    }
                                    "[BuildingTypes]" -> {
                                        BuildingTypesList
                                    }
                                    else -> {
                                        null
                                    }
                                }
                                if (list != null) {
                                    val propertyText = LightTreeUtil.toFilteredString(lighterAST, element, null)
                                    val splits = propertyText.split("=")
                                    if (splits.size >= 2) {
                                        list.add(splits[1].trim())
                                    }
                                }
//                                if (inObjectRegisterSection) {
//                                    val propertyText = LightTreeUtil.toFilteredString(lighterAST, element, null)
//                                    val splits = propertyText.split("=")
//                                    if (splits.size >= 2) {
//                                        objectsList.add(splits[1].trim())
//                                    }
//                                }
//                                if (inWarheadsRegisterSection) {
//                                    val propertyText = LightTreeUtil.toFilteredString(lighterAST, element, null)
//                                    val splits = propertyText.split("=")
//                                    if (splits.size >= 2) {
//                                        warheadsList.add(splits[1].trim())
//                                    }
//                                }

                            }
                            else -> {

                            }
                        }
                        super.visitNode(element)
                    }
                }.visitNode(lighterAST.root)

                val objectsList = InfantryTypesList + VehicleTypesList + AircraftTypesList + BuildingTypesList
                //第二轮 找到武器
                if (objectsList.isNotEmpty()) {
                    object : RecursiveLighterASTNodeWalkingVisitor(lighterAST) {

                        private var currentSectionName = ""
                        private var inObjectSection = false

                        override fun visitNode(element: LighterASTNode) {
                            when (element.tokenType.debugName) {
                                "SECTIONHEADER" -> {
                                    currentSectionName = LightTreeUtil.toFilteredString(lighterAST, element, null).trim()
                                    if (objectsList.contains(currentSectionName.substring(1..(currentSectionName.length - 2)))) {
                                        inObjectSection = true
                                    } else {
                                        inObjectSection = false
                                    }
                                }
                                "PROPERTY" -> {
                                    if (inObjectSection) {
                                        val propertyText = LightTreeUtil.toFilteredString(lighterAST, element, null)
                                        val splits = propertyText.split("=")
                                        if (splits.size >= 2) {
                                            val keyName = splits[0].trim()
                                            if (weaponPropertyNames.contains(keyName)) {
                                                val propertyValue = splits[1].trim()
                                                //应该要去掉none
                                                weaponsList.add(propertyValue)
                                            }

                                        }
                                    }

                                }
                                else -> {

                                }
                            }
                            super.visitNode(element)
                        }
                    }.visitNode(lighterAST.root)
                }

                //第三轮 找到抛射体  抛射体在武器里，所以在第三轮
                if (weaponsList.isNotEmpty()) {
                    object : RecursiveLighterASTNodeWalkingVisitor(lighterAST) {

                        private var currentSectionName = ""
                        private var inWeaponSection = false

                        override fun visitNode(element: LighterASTNode) {
                            when (element.tokenType.debugName) {
                                "SECTIONHEADER" -> {
                                    currentSectionName = LightTreeUtil.toFilteredString(lighterAST, element, null).trim()
                                    if (weaponsList.contains(currentSectionName.substring(1..(currentSectionName.length - 2)))) {
                                        inWeaponSection = true
                                    } else {
                                        inWeaponSection = false
                                    }
                                }
                                "PROPERTY" -> {
                                    if (inWeaponSection) {
                                        val propertyText = LightTreeUtil.toFilteredString(lighterAST, element, null)
                                        val splits = propertyText.split("=")
                                        if (splits.size >= 2) {
                                            val keyName = splits[0].trim()
                                            if (projectilePropertyNames.contains(keyName)) {
                                                val propertyValue = splits[1].trim()
                                                //应该要去掉none
                                                projectileList.add(propertyValue)
                                            }

                                        }
                                    }

                                }
                                else -> {

                                }
                            }
                            super.visitNode(element)
                        }
                    }.visitNode(lighterAST.root)
                }

                map.put(KEY_INFANTRYTYPES, InfantryTypesList)
                map.put(KEY_VEHICLETYPES, VehicleTypesList)
                map.put(KEY_AIRCRAFTTYPES, AircraftTypesList)
                map.put(KEY_BUILDINGTYPES, BuildingTypesList)
                map.put(KEY_WEAPON, weaponsList.toList())
                map.put(KEY_WARHEADS, warheadsList)
                map.put(KEY_PROJECTILE, projectileList)
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

    override fun getVersion(): Int = 20

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return object : DefaultFileTypeSpecificInputFilter(IniFileType) {
            override fun acceptInput(file: VirtualFile): Boolean {
                return file.inRa2Root()
            }
        }
    }

    override fun dependsOnFileContent(): Boolean  = true
}