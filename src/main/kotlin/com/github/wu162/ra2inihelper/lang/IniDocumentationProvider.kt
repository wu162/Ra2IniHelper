package com.github.wu162.ra2inihelper.lang

import com.github.wu162.ra2inihelper.inRa2Root
import com.github.wu162.ra2inihelper.lang.psi.IniProperty
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getKey
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getParentSectionName
import com.github.wu162.ra2inihelper.lang.util.IniUtil
import com.github.wu162.ra2inihelper.ra2Root
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.jetbrains.rd.util.concurrentMapOf
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

class IniDocumentationProvider : AbstractDocumentationProvider() {

    companion object {

        //这里加载的是所有project共用的
        val objPropDesc = concurrentMapOf<String, String>()
        val generalPropDesc = concurrentMapOf<String, String>()
        val weaponPropDesc = concurrentMapOf<String, String>()
        val warheadPropDesc = concurrentMapOf<String, String>()
        val projectilePropDesc = concurrentMapOf<String, String>()

        val ra2PropertyMetaInfo = concurrentMapOf<String, List<Ra2Property>>()
        val AircraftTypeProps  = CopyOnWriteArrayList<String>()
        val BuildingTypeProps  = CopyOnWriteArrayList<String>()
        val InfantryTypeProps  = CopyOnWriteArrayList<String>()
        val VehicleTypeProps  = CopyOnWriteArrayList<String>()

        val csfObjectName = concurrentMapOf<String, String>()

        val gson = Gson()

        val loaded = AtomicBoolean(false)

        fun loadDoc() {
            if (ra2Root().isEmpty()) {
                return
            }
            if (loaded.get()) {
                return
            }
            loaded.set(true)
            loadObjPropDesc("${ra2Root()}/ideaplugin/ra2ObjectPropDescs.json", objPropDesc)
            loadObjPropDesc("${ra2Root()}/ideaplugin/ra2GeneralPropDescs.json", generalPropDesc)
            loadObjPropDesc("${ra2Root()}/ideaplugin/ra2WeaponPropDescs.json", weaponPropDesc)
            loadObjPropDesc("${ra2Root()}/ideaplugin/ra2WarheadPropDescs.json", warheadPropDesc)
            loadObjPropDesc("${ra2Root()}/ideaplugin/ra2ProjectilePropDescs.json", projectilePropDesc)

            val metaInfoPath = "${ra2Root()}/ideaplugin/ra2IniMetaInfo.json"
            if (!File(metaInfoPath).exists()) {
                return
            }
            BufferedReader(FileReader(metaInfoPath)).use { br ->
                // 解析 JSON 文件为 User 对象
                val descs: Map<String, List<Ra2Property>> = gson.fromJson(br, object : TypeToken<Map<String, List<Ra2Property>>>() {}.type)
                ra2PropertyMetaInfo.putAll(descs)
            }
            val _aircraftTypeProp = ra2PropertyMetaInfo["AircraftTypes"]!!.map { it.name }
            val _buildingTypeProp = ra2PropertyMetaInfo["BuildingTypes"]!!.map { it.name }
            val _infantryTypeProp = ra2PropertyMetaInfo["InfantryTypes"]!!.map { it.name }
            val _vehicleTypeProp = ra2PropertyMetaInfo["VehicleTypes"]!!.map { it.name }
            val _objectTypeProp = ra2PropertyMetaInfo["ObjectTypes"]!!.map { it.name }
            val _technoTypeProp = ra2PropertyMetaInfo["TechnoTypes"]!!.map { it.name }
            val _abstractTypeProp = ra2PropertyMetaInfo["AbstractTypes"]!!.map { it.name }

            val _baseProp = _abstractTypeProp + _technoTypeProp + _objectTypeProp

            AircraftTypeProps.addAll(_baseProp + _aircraftTypeProp)
            BuildingTypeProps.addAll(_baseProp + _buildingTypeProp)
            InfantryTypeProps.addAll(_baseProp + _infantryTypeProp)
            VehicleTypeProps.addAll(_baseProp + _vehicleTypeProp)

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
        val file = element?.containingFile?.virtualFile
        if (file?.inRa2Root() == false) {
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
                            if (IniUtil.isObjectSection(element)) {
                                return objPropDesc[keyName]
                            }
                        }

                        //武器
                        if (weaponPropDesc.containsKey(keyName)) {
                            if (IniUtil.isWeaponSection(element)) {
                                return weaponPropDesc[keyName]
                            }
                        }

                        //弹头
                        if (warheadPropDesc.containsKey(keyName)) {
                            if (IniUtil.isWarheadSection(element)) {
                                return warheadPropDesc[keyName]
                            }
                        }

                        //抛射体
                        if (projectilePropDesc.containsKey(keyName)) {
                            if (IniUtil.isProjectileSection(element)) {
                                return projectilePropDesc[keyName]
                            }
                        }

                    }
                }

            }


        }
        return super.generateDoc(element, originalElement)
    }


}

data class Ra2Property (
    val name: String,
    val type: String,
    val availableChoices: List<String>
)